package dummy.rest.api.impl;

import dummy.rest.api.model.Dummy;
import dummy.rest.api.service.DummyService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DummyServiceImpl implements DummyService {

    @Override
    public String withoutParametersReturnsString() {
        return "withoutParametersReturnsString";
    }

    @Override
    public String withParameterReturnsString(String parameter1, float parameter2) {
        return parameter1 + ";" + parameter2;
    }

    @Override
    public Integer withoutParameterReturnsInteger() {
        return 2024;
    }

    @Override
    public Integer withParametersReturnsInteger(String parameter1, Integer parameter2) {
        return parameter1.hashCode() + parameter2;
    }

    @Override
    public String withCustomTypeParameter(Dummy model) {
        return model.getName() + ";" + model.getAddress().getCity();
    }

    @Override
    public String withMapParameter(Map<String, Object> param1) {
        return "" + param1.size();
    }

    @Override
    public String withCustomMapParameter(Map<String, Dummy> param1) {
        String city = param1.get("KEY1").getAddress().getCity();
        return param1.size() + "";
    }

    @Override
    public String withMapInsideMap(Map<String, Map<String, Dummy>> param1) {
        return param1.size() + "";
    }

    @Override
    public String withListParameter(List<Dummy> param1) {
        return param1.size() + "";
    }

    @Override
    public String withListOfMapParameter(List<Map<String, String>> param1) {
        return param1.size() + "";
    }

    @Override
    public String withArrayParameter(Dummy[] param1) {
        return param1.length + "";
    }

    @Override
    public int withArrayParameterPrimitive(int[] param1) {
        return param1.length;
    }
}
