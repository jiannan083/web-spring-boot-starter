package cn.bfay.web.exception;

import cn.bfay.web.exception.enums.ErrorType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * BizBaseException,登录失败等错误使用，打印warn日志.
 *
 * @author wangjiannan
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BizBaseException extends RuntimeException {
    private String code;

    private String message;

    public BizBaseException(ErrorType errorType) {
        super(errorType.getMessage());
        this.setCode(errorType.getCode());
        this.setMessage(errorType.getMessage());
    }
}