package rest.api.web.provider.config;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ResourceBundleMessageSource;

@Configuration
@Configurable
@ComponentScan(basePackages = {"rest.api.web.provider"})
@PropertySource("classpath:application.properties")
public class RestApiWebProviderConfig {

    @Value("${rest.api.service}")
    String restApiServicePackage;

    @Value("${rest.api.model}")
    String restApiModelPackage;

    @Bean
    public RestApiWebProviderProperties restApiWebProviderProperties() {
        return new RestApiWebProviderProperties(restApiServicePackage, restApiModelPackage);
    }

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("messages/error_messages");

        return messageSource;
    }
}
