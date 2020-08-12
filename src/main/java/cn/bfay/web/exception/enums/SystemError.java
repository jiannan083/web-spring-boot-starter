package cn.bfay.web.exception.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 系统错误.
 *
 * @author wangjiannan
 */
@Getter
@AllArgsConstructor
public enum SystemError implements ErrorType {

    SUCCESS(0, "success"),
    SYSTEM_ERROR(-1, "系统错误");

    private int code;
    private String message;
}