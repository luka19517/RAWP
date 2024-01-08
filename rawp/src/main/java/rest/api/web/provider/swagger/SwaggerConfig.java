package rest.api.web.provider.swagger;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import rest.api.web.provider.config.RestApiWebProviderProperties;
import rest.api.web.provider.converter.SupportedTypes;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

@Configuration
public class SwaggerConfig {

    @Autowired
    RestApiWebProviderProperties props;

    @Bean
    public GroupedOpenApi usersGroup(@Value("${springdoc.version}") String appVersion) {
        return GroupedOpenApi.builder().group("API")
                .addOperationCustomizer((operation, handlerMethod) -> {
                    operation.addSecurityItem(new SecurityRequirement().addList("basicScheme"));
                    return operation;
                })
                .packagesToExclude("rest.api.web.provider")
                .addOpenApiCustomizer(openApiCustomizer())
                .build();
    }

    @Bean
    public OpenApiCustomizer openApiCustomizer() {
        List<SwaggerEndpoint> allServices = findAllApiEndpoints();
        Map<String, Schema> schemas = registerAllSchemas();
        Map<String, PathItem> all = allServices.stream()
                .collect(Collectors.toMap(swaggerEndpoint -> "/api/" + swaggerEndpoint.getServiceName() + "/" + swaggerEndpoint.getMethodName(),
                        this::buildPathItem));
        return openApi -> {
            openApi.getComponents().setSchemas(schemas);
            openApi.getPaths().putAll(all);

        };
    }

    private Map<String, Schema> registerAllSchemas() {
        Map<String, Schema> result = new HashMap<>();
        InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream(props.getRestApiModelPackage().replaceAll("[.]", "/"));
        assert stream != null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        List<Class> models = reader.lines().filter(line -> line.endsWith(".class")).map(line -> getClass(line, props.getRestApiModelPackage())).sorted(Comparator.comparing(Class::getCanonicalName)).collect(Collectors.toList());
        for (Class model : models) {
            result.put(model.getSimpleName(), ModelConverters.getInstance().resolveAsResolvedSchema(new AnnotatedType(model)).schema);
        }
        return result;
    }

    private List<SwaggerEndpoint> findAllApiEndpoints() {
        List<SwaggerEndpoint> result = new ArrayList<>();
        InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream(props.getRestApiServicePackage().replaceAll("[.]", "/"));
        assert stream != null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        List<Class> interfaces = reader.lines().filter(line -> line.endsWith(".class")).map(line -> getClass(line, props.getRestApiServicePackage())).sorted(Comparator.comparing(Class::getCanonicalName)).collect(Collectors.toList());

        for (Class service : interfaces) {
            for (Method method : service.getDeclaredMethods()) {
                SwaggerEndpoint swaggerEndpoint = SwaggerEndpoint.builder().serviceName(service.getSimpleName()).methodName(method.getName()).build();
                swaggerEndpoint.setParameters(method.getParameters());
                swaggerEndpoint.setReturnType(method.getGenericReturnType().getTypeName());
                result.add(swaggerEndpoint);
            }
        }
        return result;
    }

    private Class getClass(String className, String packageName) {
        try {
            return Class.forName(packageName + "." + className.substring(0, className.lastIndexOf(".")));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private PathItem buildPathItem(SwaggerEndpoint swaggerEndpoint) {
        PathItem pathItem = new PathItem();

        Map<String, Schema> bodyProperties = null;
        try {
            bodyProperties = constructBodyProperties(swaggerEndpoint);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException |
                 JsonProcessingException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        ApiResponses apiResponses = new ApiResponses();
        ApiResponse okApiResponse = new ApiResponse().description("OK");
        ApiResponse errorApiResponse = new ApiResponse().description("Internal Server Error");
        try {
            System.out.println("Return type for " + swaggerEndpoint.getMethodName() + " " + swaggerEndpoint.getReturnType());
            okApiResponse.setContent(new Content().addMediaType("application/json", new MediaType().schema(createSchema(swaggerEndpoint.getReturnType()))));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        apiResponses.addApiResponse("200", okApiResponse);
        apiResponses.addApiResponse("500", errorApiResponse);

        Operation operation = new Operation()
                .operationId(swaggerEndpoint.getServiceName() + "-" + swaggerEndpoint.getMethodName())
                .description(swaggerEndpoint.getMethodName())
                .summary(swaggerEndpoint.getMethodName())
                .addTagsItem(swaggerEndpoint.getServiceName())
                .responses(apiResponses);

        operation.setRequestBody(new RequestBody().content(
                new Content().addMediaType("application/json", new MediaType().schema(new Schema().type("object").properties(bodyProperties)))
        ));

        pathItem.operation(PathItem.HttpMethod.POST, operation);
        return pathItem;
    }

    private Map<String, Schema> constructBodyProperties(SwaggerEndpoint swaggerEndpoint) throws
            JsonProcessingException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        Map<String, Schema> requestBodyProperties = new HashMap<>();
        for (Parameter parameter : swaggerEndpoint.getParameters()) {
            System.out.println(parameter.getParameterizedType().getTypeName());
            requestBodyProperties.put(parameter.getName(), createSchema(parameter.getParameterizedType().getTypeName()));
        }
        return requestBodyProperties;
    }

    private Schema createSchema(String fullClassName) throws ClassNotFoundException {
        if (fullClassName.matches("java.util.List.*")) {
            String parametrizedTypePart = fullClassName.substring(fullClassName.indexOf("<") + 1, fullClassName.lastIndexOf(">"));
            return new ArraySchema().items(
                    ModelConverters.getInstance().resolveAsResolvedSchema(new AnnotatedType(Class.forName(parametrizedTypePart))).schema);
        } else if (fullClassName.matches("java.util.Map.*")) {
            String parametrizedTypePart = fullClassName.substring(fullClassName.indexOf("<") + 1, fullClassName.lastIndexOf(">"));
            return new MapSchema().properties(createMapSchema(parametrizedTypePart.split(",")[1].trim()));
        } else if (fullClassName.matches(".*\\[].*")) {
            Class itemType = Class.forName(fullClassName.substring(0, fullClassName.indexOf("[")));
            return new ArraySchema().items(
                    ModelConverters.getInstance().resolveAsResolvedSchema(new AnnotatedType(itemType)).schema);
        } else if (fullClassName.matches("void")) {
            return ModelConverters.getInstance().resolveAsResolvedSchema(new AnnotatedType(void.class)).schema;
        } else if (SupportedTypes.isSupported(fullClassName)) {
            return ModelConverters.getInstance().resolveAsResolvedSchema(new AnnotatedType(SupportedTypes.findSimpleClassType(fullClassName))).schema;
        }
        return ModelConverters.getInstance().resolveAsResolvedSchema(new AnnotatedType(Class.forName(fullClassName))).schema;
    }

    Map<String, Schema> createMapSchema(String className) throws ClassNotFoundException {
        Map<String, Schema> map = new HashMap<>();
        map.put("key", ModelConverters.getInstance().readAllAsResolvedSchema(new AnnotatedType(Class.forName(className))).schema);
        return map;
    }
}
