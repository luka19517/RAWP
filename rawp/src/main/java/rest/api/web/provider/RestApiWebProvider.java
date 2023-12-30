package rest.api.web.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rest.api.web.provider.processor.RestApiWebRequestProcessor;

@RestController
@CrossOrigin
@RequestMapping("api")
public class RestApiWebProvider {

    @Autowired
    RestApiWebRequestProcessor requestProcessor;

    @PostMapping("/{serviceName}/{methodName}")
    public ResponseEntity<String> processRequest(@PathVariable String serviceName, @PathVariable String methodName, @RequestBody String body) {
        try {
            System.out.println(requestProcessor);
            requestProcessor.processRequest(serviceName, methodName, body);
        } catch (Exception e) {
            System.out.println("Exception thrown");
        }
        return null;
    }
}
