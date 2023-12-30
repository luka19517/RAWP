package dummy.rest.api.impl;

import dummy.rest.api.service.DummyService;
import org.springframework.stereotype.Service;

@Service
public class DummyServiceImpl implements DummyService {

    @Override
    public String withoutParametersReturnsString() {
        return "withoutParametersReturnsString";
    }

    @Override
    public String withParameterReturnsString(String parameter1, Integer parameter2) {
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
}
