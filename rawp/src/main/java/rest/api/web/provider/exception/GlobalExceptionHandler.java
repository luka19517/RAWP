package rest.api.web.provider.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Locale;
import java.util.Objects;

@ControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    MessageSource messageSource;

    @ExceptionHandler({RAWPException.class})
    public ResponseEntity<Object> handleRAWPException(RAWPException exception, WebRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(messageSource.getMessage(exception.getErrorCode(), null, "RAWPException is thrown", new Locale(Objects.requireNonNull(request.getHeader(HttpHeaders.ACCEPT_LANGUAGE)))));
    }
}
