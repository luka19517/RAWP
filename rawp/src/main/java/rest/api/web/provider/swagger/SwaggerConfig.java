package rest.api.web.provider.swagger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.ObjectSchema;
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
                 JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        ApiResponses apiResponses = new ApiResponses();
        apiResponses.addApiResponse("200", new ApiResponse().description("OK"));
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

    private Map<String, Schema> constructBodyProperties(SwaggerEndpoint swaggerEndpoint) throws JsonProcessingException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Map<String, Schema> requestBodyProperties = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        SchemaFactoryWrapper visitor = new SchemaFactoryWrapper();
        for (Parameter parameter : swaggerEndpoint.getParameters()) {
            mapper.acceptJsonFormatVisitor(parameter.getType(), visitor);
            String paramSchema = mapper.writeValueAsString(visitor.finalSchema());
            Map<String, Object> schemeMap = mapper.readValue(paramSchema, HashMap.class);
            System.out.println("parameter name : " + parameter.getName() + "Parameter scheme : " + mapper.writeValueAsString(schemeMap.get("properties")));
            if (!Arrays.stream(SupportedTypes.supportedTypes).anyMatch(cd -> parameter.getParameterizedType().getTypeName().matches(cd.getNamePattern()))) {
                Schema schema = new ObjectSchema().type("object").$schema(mapper.writeValueAsString(schemeMap.get("properties")))
                        .type("object").description(parameter.getName());
                requestBodyProperties.put(parameter.getName(), schema);
            } else {
                Schema schema = new ObjectSchema().$schema(paramSchema).description(parameter.getName());
                requestBodyProperties.put(parameter.getName(), schema);
            }
        }
        return requestBodyProperties;
    }
}
