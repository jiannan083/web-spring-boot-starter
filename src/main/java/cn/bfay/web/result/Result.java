package cn.bfay.web.result;

import cn.bfay.web.exception.enums.SystemError;
import lombok.Data;

import java.io.Serializable;

/**
 * 操作结果集.
 *
 * @author wangjiannan
 */
@Data
public class Result implements Serializable {
    private static final long serialVersionUID = -7529630554309028207L;
    private int code;

    private String message;

    private Object data;

    private static Result build(int code, String message, Object data) {
        Result result = new Result();
        result.setCode(code);
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    /**
     * 成功时候的调用.
     *
     * @return {@link Result}
     */
    public static Result buildSuccess() {
        return build(SystemError.SUCCESS.getCode(), SystemError.SUCCESS.getMessage(), null);
    }

    /**
     * 成功时候的调用.
     *
     * @param data 数据
     * @return {@link Result}
     */
    public static Result buildSuccess(Object data) {
        return build(SystemError.SUCCESS.getCode(), SystemError.SUCCESS.getMessage(), data);
    }

    /**
     * 失败时候的调用.
     *
     * @param code    错误码
     * @param message 错误信息
     * @return {@link Result}
     */
    public static Result buildError(int code, String message) {
        return build(code, message, null);
    }
}
