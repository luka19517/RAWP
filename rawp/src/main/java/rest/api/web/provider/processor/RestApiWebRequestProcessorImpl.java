package rest.api.web.provider.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import rest.api.web.provider.config.RestApiWebProviderProperties;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

@Service
public class RestApiWebRequestProcessorImpl implements RestApiWebRequestProcessor {


    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    RestApiWebProviderProperties props;

    @Autowired
    ApplicationContext applicationContext;

    @Override
    public Object processRequest(String serviceName, String methodName, String requestBody) {

        try {
            Class<?> serviceClass = Class.forName(props.getRestApiServicePackage() + "." + serviceName);
            Map<String, Object> paramsMap = objectMapper.readValue(requestBody, HashMap.class);
            Method targetMethod = findTargetMethod(serviceClass, methodName, paramsMap);
            return targetMethod.invoke(applicationContext.getBean(serviceClass), getParamsInOrder(targetMethod, paramsMap));
        } catch (Exception e) {
            throw new IllegalStateException("Error while parsing ", e);
        }
    }


    private Method findTargetMethod(Class serviceBeanClass, String targetMethodName, Map<String, Object> paramsMap) {
        for (Method method : serviceBeanClass.getDeclaredMethods()) {
            if (method.getName().equals(targetMethodName)) {
                if (checkParamTypes(method, paramsMap)) {
                    return method;
                }
            }
        }
        return null;
    }

    private boolean checkParamTypes(Method method, Map<String, Object> paramsMap) {
        if (!(method.getParameters().length == paramsMap.size()))
            return false;
        for (Parameter param : method.getParameters()) {

            try {
                String paramTypeName = param.getParameterizedType().getTypeName();

                switch (paramTypeName) {
                    case "java.lang.Integer", "int" -> {
                        paramsMap.put(param.getName(), Integer.valueOf(paramsMap.get(param.getName()).toString()));
                    }
                    case "java.lang.Float", "float" -> {
                        paramsMap.put(param.getName(), Float.valueOf(paramsMap.get(param.getName()).toString()));
                    }
                    case "java.lang.Double", "double" -> {
                        paramsMap.put(param.getName(), Double.valueOf(paramsMap.get(param.getName()).toString()));
                    }
                    default -> {
                        String.valueOf(paramsMap.get(param.getName()));
                    }
                }
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    private Object[] getParamsInOrder(Method method, Map<String, Object> paramsMap) {
        Object[] paramsInOrder = new Object[method.getParameters().length];
        for (int i = 0; i < paramsInOrder.length; i++) {
            paramsInOrder[i] = paramsMap.get(method.getParameters()[i].getName());
        }
        return paramsInOrder;
    }
}
