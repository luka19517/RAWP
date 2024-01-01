package rest.api.web.provider.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import rest.api.web.provider.config.RestApiWebProviderProperties;
import rest.api.web.provider.converter.TypeConverter;
import rest.api.web.provider.exception.RAWPException;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class RestApiWebRequestProcessorImpl implements RestApiWebRequestProcessor {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    TypeConverter typeConverter;
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
            return typeConverter.serialize(targetMethod.invoke(applicationContext.getBean(serviceClass), getParamsInOrder(targetMethod, paramsMap)));
        } catch (Exception e) {
            log.error("Error", e);
            throw new RAWPException("error.messages.process", e);
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
                paramsMap.put(param.getName(), typeConverter.deserialize(objectMapper.writeValueAsString(paramsMap.get(param.getName())), paramTypeName));
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
