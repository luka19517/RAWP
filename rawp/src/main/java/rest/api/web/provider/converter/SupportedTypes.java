package rest.api.web.provider.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;

public class SupportedTypes {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static ClassDefinition[] supportedTypes = new ClassDefinition[]{
            ClassDefinition.builder().namePattern("int").classTypeResolveFunction(((paramTypeString, jsonContent) -> {
                try {
                    return objectMapper.readValue(jsonContent, Class.forName("java.lang.Integer"));
                } catch (ClassNotFoundException | JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            )).build(),
            ClassDefinition.builder().namePattern("float").classTypeResolveFunction(((paramTypeString, jsonContent) -> {
                try {
                    return objectMapper.readValue(jsonContent, Class.forName("java.lang.Float"));
                } catch (ClassNotFoundException | JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            )).build(),
            ClassDefinition.builder().namePattern("double").classTypeResolveFunction(((paramTypeString, jsonContent) -> {
                try {
                    return objectMapper.readValue(jsonContent, Class.forName("java.lang.Double"));
                } catch (ClassNotFoundException | JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            )).build(),
            ClassDefinition.builder().namePattern("java.lang.Integer").classTypeResolveFunction(((paramTypeString, jsonContent) -> {
                try {
                    return objectMapper.readValue(jsonContent, Class.forName("java.lang.Integer"));
                } catch (ClassNotFoundException | JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            )).build(),
            ClassDefinition.builder().namePattern("java.lang.Float").classTypeResolveFunction(((paramTypeString, jsonContent) -> {
                try {
                    return objectMapper.readValue(jsonContent, Class.forName("java.lang.Float"));
                } catch (ClassNotFoundException | JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            )).build(),
            ClassDefinition.builder().namePattern("java.lang.Double").classTypeResolveFunction(((paramTypeString, jsonContent) -> {
                try {
                    return objectMapper.readValue(jsonContent, Class.forName("java.lang.Double"));
                } catch (ClassNotFoundException | JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            )).build(),
            ClassDefinition.builder().namePattern("java.lang.String").classTypeResolveFunction(((paramTypeString, jsonContent) -> {
                try {
                    return objectMapper.readValue(jsonContent, Class.forName("java.lang.String"));
                } catch (ClassNotFoundException | JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            )).build(),
            ClassDefinition.builder().namePattern("java.util.Map.*").classTypeResolveFunction(((paramTypeString, jsonContent) -> {
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
            ClassDefinition.builder().namePattern("java.util.List.*").classTypeResolveFunction(((paramTypeString, jsonContent) -> {
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
    interface ClassTypeResolver {
        Object resolveClassType(String paramTypeString, String jsonContent);
    }

    @Data
    @Builder
    public static class ClassDefinition {
        private String namePattern;
        private ClassTypeResolver classTypeResolveFunction;
    }
}
