package cn.bfay.web.exception.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 系统错误.
 *
 * @author wangjiannan
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum SystemError implements ErrorType {
    SUCCESS("0", "success"),
    //
    SYSTEM_ERROR("-1", "系统错误"),
    PARAM_ERROR("-2", "参数错误"),
    NON_SUPPORT_METHOD_ERROR("-3", "请求方式错误");

    private String code;
    private String message;
}