package rest.api.web.provider.converter;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface TypeConverter {

    Object deserialize(String jsonString, String typeName) throws ClassNotFoundException, JsonProcessingException;

    String serialize(Object value) throws JsonProcessingException;
}
