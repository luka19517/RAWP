package test.rest.api.web.provider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import rest.api.web.provider.RestApiWebProvider;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class RestApiWebProviderMockitoTest {

    MockMvc mockMvc;

    @Autowired
    RestApiWebProvider restApiWebProvider;

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(restApiWebProvider).build();
    }

    @Test
    public void test1() throws Exception {
        mockMvc.perform(post("/api/DummyService/withoutParametersReturnsString").contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isOk()).andExpect(content().string("withoutParametersReturnsString"));
    }
}
