package cn.bfay.web.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * SystemException.
 *
 * @author wangjiannan
 */
@EqualsAndHashCode(callSuper = true)
@Data
class SystemException extends RuntimeException {
    private static final long serialVersionUID = -5009589496558759560L;

    private int code;
    private String message;

    //public int getCode() {
    //    return code;
    //}
    //
    //public void setCode(int code) {
    //    this.code = code;
    //}
    //
    //@Override
    //public String getMessage() {
    //    return message;
    //}
    //
    //public void setMessage(String message) {
    //    this.message = message;
    //}
    //
    //public SystemException(int code, String message) {
    //    super(message);
    //    this.setCode(code);
    //    this.setMessage(message);
    //}
    //
    //public SystemException(ErrorType errorType) {
    //    super(errorType.getMessage());
    //    this.setCode(errorType.getCode());
    //    this.setMessage(errorType.getMessage());
    //}
}
