package test.rest.api.web.provider;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import rest.api.web.provider.config.RestApiWebProviderConfig;

@SpringBootConfiguration
@ComponentScan(basePackages = {"dummy.rest.api"})
@Import(RestApiWebProviderConfig.class)
public class RestApiWebProviderTestConfig {
}
