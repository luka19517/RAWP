package rest.api.web.provider.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rest.api.web.provider.config.RestApiWebProviderProperties;

@Service
public class RestApiWebRequestProcessorImpl implements RestApiWebRequestProcessor {

    @Autowired
    RestApiWebProviderProperties props;

    @Override
    public String processRequest(String serviceName, String methodName, String requestBody) {
        return null;
    }
}
