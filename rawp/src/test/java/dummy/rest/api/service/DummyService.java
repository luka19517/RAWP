package dummy.rest.api.service;

import dummy.rest.api.model.Dummy;

import java.util.List;
import java.util.Map;

public interface DummyService {

    String withoutParametersReturnsString();

    String withParameterReturnsString(String parameter1, float parameter2);

    Integer withoutParameterReturnsInteger();

    Integer withParametersReturnsInteger(String parameter1, Integer parameter2);

    String withCustomTypeParameter(Dummy model);

    String withMapParameter(Map<String, Object> param1);

    String withCustomMapParameter(Map<String, Dummy> param1);

    String withMapInsideMap(Map<String, Map<String, Dummy>> param1);

    String withListParameter(List<Dummy> param1);

    String withListOfMapParameter(List<Map<String, String>> param1);

    String withArrayParameter(Dummy[] param1);

    int withArrayParameterPrimitive(int[] param1);
}
