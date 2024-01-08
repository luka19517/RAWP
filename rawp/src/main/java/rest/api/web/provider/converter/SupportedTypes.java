package rest.api.web.provider.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import lombok.Builder;
import lombok.Data;
import org.springframework.util.Assert;

import java.util.*;

public class SupportedTypes {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static ClassDefinition[] supportedTypes = new ClassDefinition[]{
            ClassDefinition.builder().namePattern("int").simpleClassType(Integer.class).classTypeResolveFunction(((paramTypeString, jsonContent) -> {
                try {
                    return objectMapper.readValue(jsonContent, Class.forName("java.lang.Integer"));
                } catch (ClassNotFoundException | JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            )).build(),
            ClassDefinition.builder().namePattern("float").simpleClassType(Float.class).classTypeResolveFunction(((paramTypeString, jsonContent) -> {
                try {
                    return objectMapper.readValue(jsonContent, Class.forName("java.lang.Float"));
                } catch (ClassNotFoundException | JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            )).build(),
            ClassDefinition.builder().namePattern("double").simpleClassType(Double.class).classTypeResolveFunction(((paramTypeString, jsonContent) -> {
                try {
                    return objectMapper.readValue(jsonContent, Class.forName("java.lang.Double"));
                } catch (ClassNotFoundException | JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            )).build(),
            ClassDefinition.builder().namePattern("java.lang.Integer").simpleClassType(Integer.class).classTypeResolveFunction(((paramTypeString, jsonContent) -> {
                try {
                    return objectMapper.readValue(jsonContent, Class.forName("java.lang.Integer"));
                } catch (ClassNotFoundException | JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            )).build(),
            ClassDefinition.builder().namePattern("java.lang.Float").simpleClassType(Float.class).classTypeResolveFunction(((paramTypeString, jsonContent) -> {
                try {
                    return objectMapper.readValue(jsonContent, Class.forName("java.lang.Float"));
                } catch (ClassNotFoundException | JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            )).build(),
            ClassDefinition.builder().namePattern("java.lang.Double").simpleClassType(Double.class).classTypeResolveFunction(((paramTypeString, jsonContent) -> {
                try {
                    return objectMapper.readValue(jsonContent, Class.forName("java.lang.Double"));
                } catch (ClassNotFoundException | JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            )).build(),
            ClassDefinition.builder().namePattern("java.lang.String").simpleClassType(String.class).classTypeResolveFunction(((paramTypeString, jsonContent) -> {
                try {
                    return objectMapper.readValue(jsonContent, Class.forName("java.lang.String"));
                } catch (ClassNotFoundException | JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            )).build(),
            ClassDefinition.builder().namePattern("java.util.Map.*").simpleClassType(Map.class).classTypeResolveFunction(((paramTypeString, jsonContent) -> {
                try {
                    String[] genericTypes = paramTypeString.split(",");
                    String typeParameter1 = genericTypes[0].trim();
                    String typeParameter2 = genericTypes[1].trim();
                    MapType collectionType = objectMapper.getTypeFactory().constructMapType(HashMap.class, Class.forName(typeParameter1), Class.forName(typeParameter2));
                    return objectMapper.readValue(jsonContent, collectionType);
                } catch (ClassNotFoundException | JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            )).build(),
            ClassDefinition.builder().namePattern("java.util.List.*").simpleClassType(List.class).classTypeResolveFunction(((paramTypeString, jsonContent) -> {
                try {
                    String typeParameter = paramTypeString.trim();
                    CollectionType collectionType = objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, Class.forName(typeParameter));
                    return objectMapper.readValue(jsonContent, collectionType);
                } catch (ClassNotFoundException | JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            )).build(),
            ClassDefinition.builder().namePattern(".*\\[\\].*").classTypeResolveFunction(((paramTypeString, jsonContent) -> {
                try {
                    return objectMapper.readValue(jsonContent, Class.forName(paramTypeString).arrayType());
                } catch (ClassNotFoundException | JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            )).build(),
    };

    @FunctionalInterface
    public interface ClassTypeResolver {
        Object resolveClassType(String paramTypeString, String jsonContent);
    }

    public static boolean isSupported(String fullClassName) {
        return Arrays.stream(supportedTypes).anyMatch(cd -> fullClassName.matches(cd.getNamePattern()));
    }

    public static Class findSimpleClassType(String fullClassName) {
        if (!isSupported(fullClassName))
            return null;
        List<ClassDefinition> targetCD = Arrays.stream(supportedTypes).filter(cd -> fullClassName.matches(cd.getNamePattern())).toList();
        Assert.isTrue(targetCD.size() == 1, "Multiple supportedTypes are found");
        return targetCD.get(0).simpleClassType;
    }

    @Data
    @Builder
    public static class ClassDefinition {
        private String namePattern;
        private Class simpleClassType;
        private ClassTypeResolver classTypeResolveFunction;
    }
}
