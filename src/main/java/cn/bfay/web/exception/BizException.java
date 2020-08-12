package cn.bfay.web.exception;

import cn.bfay.web.exception.enums.ErrorType;

/**
 * BizException.
 *
 * @author wangjiannan
 */
public class BizException extends RuntimeException {
    private static final long serialVersionUID = -5009589496558759560L;

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

    public BizException(int code, String message) {
        super(message);
        this.setCode(code);
        this.setMessage(message);
    }

    public BizException(ErrorType errorType) {
        super(errorType.getMessage());
        this.setCode(errorType.getCode());
        this.setMessage(errorType.getMessage());
    }
}
