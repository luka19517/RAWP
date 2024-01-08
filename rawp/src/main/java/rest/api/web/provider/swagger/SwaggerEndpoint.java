package rest.api.web.provider.swagger;

import lombok.Builder;
import lombok.Data;

import java.lang.reflect.Parameter;

@Data
@Builder
public class SwaggerEndpoint {

    private String serviceName;
    private String methodName;
    private Parameter[] parameters;
    private String returnType;

}
