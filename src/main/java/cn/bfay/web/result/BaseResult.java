package cn.bfay.web.result;

import java.io.Serializable;
import cn.bfay.web.exception.enums.SystemError;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 操作结果集.
 *
 * @author wangjiannan
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseResult implements Serializable {
    private static final long serialVersionUID = -7529630554309028207L;
    private String code;

    private String message;

    private Object data;

    private static BaseResult build(String code, String message, Object data) {
        BaseResult baseResult = new BaseResult();
        baseResult.setCode(code);
        baseResult.setMessage(message);
        baseResult.setData(data);
        return baseResult;
    }

    /**
     * 成功时候的调用.
     *
     * @return {@link BaseResult}
     */
    public static BaseResult buildSuccess() {
        return build(SystemError.SUCCESS.getCode(), SystemError.SUCCESS.getMessage(), null);
    }

    /**
     * 成功时候的调用.
     *
     * @param data 数据
     * @return {@link BaseResult}
     */
    public static BaseResult buildSuccess(Object data) {
        return build(SystemError.SUCCESS.getCode(), SystemError.SUCCESS.getMessage(), data);
    }

    /**
     * 失败时候的调用.
     *
     * @param code    错误码
     * @param message 错误信息
     * @return {@link BaseResult}
     */
    public static BaseResult buildError(String code, String message) {
        return build(code, message, null);
    }
}
