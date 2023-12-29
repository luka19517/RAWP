package rest.api.web.provider.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
public class RestApiWebProviderConfig {

    @Value("rest.api.service")
    String restApiServicePackage;

    @Value("rest.api.model")
    String restApiModelPackage;

    @Bean
    public RestApiWebProviderProperties restApiWebProviderProperties() {
        return new RestApiWebProviderProperties(restApiServicePackage, restApiModelPackage);
    }
}
