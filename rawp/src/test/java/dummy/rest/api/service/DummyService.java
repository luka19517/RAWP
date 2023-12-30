package dummy.rest.api.service;

public interface DummyService {

    String withoutParametersReturnsString();

    String withParameterReturnsString(String parameter1, Integer parameter2);

    Integer withoutParameterReturnsInteger();

    Integer withParametersReturnsInteger(String parameter1, Integer parameter2);
}
