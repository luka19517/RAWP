package rest.api.web.provider;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api")
public class RestApiWebProvider {


    @PostMapping("/{serviceName}/{methodName}")
    public ResponseEntity<String> processRequest(@PathVariable String serviceName, @PathVariable String methodName, @RequestBody String body) {
        try{

        }catch(Exception e){
            System.out.println("Exception thrown");
        }
        return null;
    }
}
