package cn.bfay.web.exception;

import cn.bfay.web.exception.enums.ErrorType;

/**
 * BfayException.
 *
 * @author wangjiannan
 */
public class BfayException extends RuntimeException {
    private int code;
    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    //public BfayException(ErrorType exceptionType) {
    //    super(exceptionType.getMessage());
    //    this.setCode(exceptionType.getCode());
    //    this.setMessage(exceptionType.getMessage());
    //}

    public BfayException(int code, String message) {
        super(message);
        this.setCode(code);
        this.setMessage(message);
    }

    public BfayException(ErrorType errorType) {
        super(errorType.getMessage());
        this.setCode(errorType.getCode());
        this.setMessage(errorType.getMessage());
    }
}
