package cn.bfay.web.exception;

import cn.bfay.web.exception.enums.ErrorType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * SystemException,系统错误使用，打印error日志.
 *
 * @author wangjiannan
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SystemException extends RuntimeException {
    private String code;

    private String message;

    public SystemException(ErrorType errorType) {
        super(errorType.getMessage());
        this.setCode(errorType.getCode());
        this.setMessage(errorType.getMessage());
    }
}