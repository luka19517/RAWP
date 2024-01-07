package rest.api.web.provider.swagger;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
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
        Map<String, PathItem> all = allServices.stream()
                .collect(Collectors.toMap(swaggerEndpoint -> "/api/" + swaggerEndpoint.getServiceName() + "/" + swaggerEndpoint.getMethodName(),
                        this::buildPathItem));
        return openApi -> openApi.getPaths().putAll(all);

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
                swaggerEndpoint.setReturnType(method.getReturnType());
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
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException |
                 JsonProcessingException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        ApiResponses apiResponses = new ApiResponses();
        apiResponses.addApiResponse("200", new ApiResponse().description("OK").content(new Content().addMediaType("application/json", new MediaType().schema(ModelConverters.getInstance().resolveAsResolvedSchema(new AnnotatedType(swaggerEndpoint.getReturnType())).schema))));
        apiResponses.addApiResponse("500", new ApiResponse().description("Internal Server Error"));

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

    private Map<String, Schema> constructBodyProperties(SwaggerEndpoint swaggerEndpoint) throws JsonProcessingException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        Map<String, Schema> requestBodyProperties = new HashMap<>();
        for (Parameter parameter : swaggerEndpoint.getParameters()) {
            Schema schema = null;
            if (parameter.getParameterizedType().getTypeName().matches("java.util.List.*")) {
                String listTypeName = parameter.getParameterizedType().getTypeName();
                Class itemType = Class.forName(parameter.getParameterizedType().getTypeName().substring(listTypeName.indexOf("<") + 1, listTypeName.lastIndexOf(">")));
                schema = new ArraySchema().items(
                        ModelConverters.getInstance().resolveAsResolvedSchema(new AnnotatedType(itemType)).schema);
            } else {
                schema = ModelConverters.getInstance().resolveAsResolvedSchema(new AnnotatedType(parameter.getType())).schema;
            }
            requestBodyProperties.put(parameter.getName(), schema);
        }
        return requestBodyProperties;
    }
}
