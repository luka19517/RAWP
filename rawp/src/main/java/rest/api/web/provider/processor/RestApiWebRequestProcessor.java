package rest.api.web.provider.processor;

public interface RestApiWebRequestProcessor {

    String processRequest(String serviceName, String methodName, String requestBody);
    
}
