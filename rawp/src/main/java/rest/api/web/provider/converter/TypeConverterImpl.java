package rest.api.web.provider.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Service
public class TypeConverterImpl implements TypeConverter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Object deserialize(String jsonString, String typeName) throws ClassNotFoundException, JsonProcessingException {
        if (Arrays.stream(SupportedTypes.supportedTypes).anyMatch(cd -> typeName.matches(cd.getNamePattern()))) {
            Optional<SupportedTypes.ClassDefinition> targetClassDefinition = Arrays.stream(SupportedTypes.supportedTypes).filter(cd -> typeName.matches(cd.getNamePattern())).findFirst();
            if (targetClassDefinition.isPresent()) {
                String paramTypeString = null;
                if (typeName.indexOf("<") > 0 && typeName.lastIndexOf(">") > 0)
                    paramTypeString = typeName.substring(typeName.indexOf("<") + 1, typeName.lastIndexOf(">"));
                if (typeName.indexOf("[") > 0 && typeName.lastIndexOf("]") > 0) {
                    paramTypeString = typeName.substring(0, typeName.indexOf("["));
                }
                return targetClassDefinition.get().getClassTypeResolveFunction().resolveClassType(paramTypeString, jsonString);
            }
        }
        return objectMapper.readValue(jsonString, Class.forName(typeName));
    }

    @Override
    public String serialize(Object value) throws JsonProcessingException {
        return objectMapper.writeValueAsString(value);
    }

}
