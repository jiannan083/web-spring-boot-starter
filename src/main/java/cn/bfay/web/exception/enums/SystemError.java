package cn.bfay.web.exception.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 系统错误(9).
 *
 * @author wangjiannan
 */
@Getter
@AllArgsConstructor
public enum SystemError implements ErrorType {

    SYSTEM_ERROR(999999, "系统错误");

    private int code;
    private String message;
}