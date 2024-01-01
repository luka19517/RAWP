package rest.api.web.provider.exception;

import lombok.Data;

@Data
public class RAWPException extends RuntimeException {

    private String errorCode;
    private Throwable err;

    public RAWPException(String errorCode, Throwable e) {
        super(errorCode, e);
        this.errorCode = errorCode;
    }

}
