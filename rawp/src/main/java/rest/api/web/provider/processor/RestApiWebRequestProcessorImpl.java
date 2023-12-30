package rest.api.web.provider.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import rest.api.web.provider.config.RestApiWebProviderProperties;

import java.lang.reflect.Method;
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
            System.out.println(serviceClass.getCanonicalName());
            Map<String, Object> paramsMap = objectMapper.readValue(requestBody, HashMap.class);
//            Method targetMethod = findTargetMethod(serviceClass, methodName, paramsMap);

            Object serviceBean = applicationContext.getBean(serviceClass);
            return findTargetMethod(serviceBean.getClass(), methodName).invoke(serviceBean);
        } catch (Exception e) {
            throw new IllegalStateException("Error while parsing ", e);
        }
    }

    private Method findTargetMethod(Class serviceClass, String methodName) {
        for (Method method : serviceClass.getDeclaredMethods()) {
            if (method.getName().equals(methodName))
                return method;
        }
        return null;
    }

//    private Method findTargetMethod(Class serviceBeanClass, String targetMethodName, Map<String, Object> paramsMap) {
//        for (Method method : serviceBeanClass.getDeclaredMethods()) {
//            if (method.getName().equals(targetMethodName)) {
//                if (checkParamTypes(method, paramsMap)) {
//                    return method;
//                }
//            }
//        }
//        return null;
//    }

//    private boolean checkParamTypes(Method method, Map<String, Object> paramsMap) {
//        if (!(method.getParameters().length == paramsMap.size()))
//            return false;
//        for (Parameter param : method.getParameters()) {
//
//            try {
//                String paramTypeName = param.getParameterizedType().getTypeName();
//                if (param.isNamePresent()) {
//                    System.out.println("POZZZ - " + param.getName());
//                }
//                switch (paramTypeName) {
//                    case "java.lang.Integer" -> {
//                        Integer.valueOf(paramsMap.get(param.isNamePresent()).toString());
//                    }
//                    case "java.lang.Float" -> {
//                        Float.valueOf(paramsMap.get(param.getName()).toString());
//                    }
//                    case "java.lang.Double" -> {
//                        Double.valueOf(paramsMap.get(param.getName()).toString());
//                    }
//                    default -> {
//                        String.valueOf(paramsMap.get(param.getName()));
//                    }
//                }
//            } catch (Exception e) {
//                return false;
//            }
//        }
//        return true;
//    }
}
