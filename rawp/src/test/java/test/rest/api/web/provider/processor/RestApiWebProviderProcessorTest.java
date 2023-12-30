package test.rest.api.web.provider.processor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import rest.api.web.provider.processor.RestApiWebRequestProcessor;

@SpringBootTest
public class RestApiWebProviderProcessorTest {

    @Autowired
    RestApiWebRequestProcessor requestProcessor;

    @Test
    public void test() {

        Object result = requestProcessor.processRequest("DummyService", "withoutParametersReturnsString", "{}");

        Assertions.assertEquals("withoutParametersReturnsString", result.toString());
    }

    @Test
    public void test2() {

        Object result = requestProcessor.processRequest("DummyService", "withParameterReturnsString", "{\"parameter1\": \"p1\", \"parameter2\": 2}");
    }

}
