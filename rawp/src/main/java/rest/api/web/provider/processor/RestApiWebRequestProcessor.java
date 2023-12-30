package rest.api.web.provider.processor;

public interface RestApiWebRequestProcessor {

    Object processRequest(String serviceName, String methodName, String requestBody);

}
