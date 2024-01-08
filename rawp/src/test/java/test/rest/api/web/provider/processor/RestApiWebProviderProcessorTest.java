package test.rest.api.web.provider.processor;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import rest.api.web.provider.processor.RestApiWebRequestProcessor;

@SpringBootTest
public class RestApiWebProviderProcessorTest {

    @Autowired
    RestApiWebRequestProcessor requestProcessor;

    @Test
    public void test_withoutParameters() {
        requestProcessor.processRequest("DummyService", "withoutParametersReturnsString", "{}");
    }

    @Test
    public void test_withFloatParam() {
        requestProcessor.processRequest("DummyService", "withParameterReturnsString", "{\"parameter1\": \"p1\", \"parameter2\": 2.5}");
    }

    @Test
    public void test_withCustomParam() {
        requestProcessor.processRequest("DummyService", "withCustomTypeParameter", "{\"model\": { \"name\": \"Dummy name\", \"address\":{ \"city\": \"Beograd\"}}}");
    }


    @Test
    public void test_withMapParam() {
        requestProcessor.processRequest("DummyService", "withMapParameter", "{\"param1\": { \"key1\": \"KEY\", \"key2\": \"KEY2\"}}");
    }

    @Test
    public void test_withCustomMapParam() {
        requestProcessor.processRequest("DummyService", "withCustomMapParameter", "{\"param1\": { \"KEY1\": { \"name\": \"POZZ\", \"address\":{\"city\": \"Beograd\"}}}}");
    }

    @Test
    public void test_withCustomListParam() {
        requestProcessor.processRequest("DummyService", "withListParameter", "{\"param1\":[{ \"name\": \"POZZ\", \"address\":{\"city\": \"Beograd\"}}]}");
    }

    @Test
    public void test_withCustomArrayParam() {
        requestProcessor.processRequest("DummyService", "withArrayParameter", "{\"param1\":[{ \"name\": \"POZZ\", \"address\":{\"city\": \"Beograd\"}}]}");
    }

    @Test
    public void test_withArrayParameterPrimitive() {
        requestProcessor.processRequest("DummyService", "withArrayParameterPrimitive", "{\"param1\":[123]}");
    }

    //This tests should fail because support is only for 1 level generics
    @Test
    public void test_withListOfMapParam() {
        requestProcessor.processRequest("DummyService", "withListOfMapParameter", "{\"param1\":[{ \"KEY1\": \"POZZ\"}]}");
    }
    @Test
    public void test_withMapInsideMapParam() {
        requestProcessor.processRequest("DummyService", "withMapInsideMap", "{\"param1\":{ \"OUTTERKEY1\": { \"KEY1\": { \"name\": \"POZZ\", \"address\":{\"city\": \"Beograd\"}}}}}");
    }


}
