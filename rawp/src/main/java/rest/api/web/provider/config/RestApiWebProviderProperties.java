package rest.api.web.provider.config;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RestApiWebProviderProperties {

    private String restApiServicePackage;
    private String restApiModelPackage;

}
