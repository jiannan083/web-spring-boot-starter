package cn.bfay.web.support;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.websocket.DecodeException;
import java.util.List;
import java.util.Set;
import cn.bfay.web.exception.BizBaseException;
import cn.bfay.web.exception.BizException;
import cn.bfay.web.exception.SystemException;
import cn.bfay.web.exception.enums.SystemError;
import cn.bfay.web.result.BaseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * 全局异常处理类.
 * "@ExceptionHandler"可定义多个, 当多个匹配时, 按编写最早的处理函数优先处理.
 * 这边就像try/catch那种常见的情况了, 既顺序敏感。最佳实践, 需要把处于顶层的异常类搁置到代码最尾端.
 * 启动应用后，被 @ExceptionHandler、@InitBinder、@ModelAttribute 注解的方法，都会作用在 被 @RequestMapping 注解的方法上.
 *
 * @author wangjiannan
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 参数.
     * MissingServletRequestParameterException-缺少参数;
     * TypeMismatchException-参数类型不匹配;
     * HttpMessageNotReadableException-参数不可读,要求json形式,但实际是String类型.
     *
     * @return {@link Object}
     */
    @ExceptionHandler({MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class,
        HttpMessageNotReadableException.class, TypeMismatchException.class})
    public Object requestParameterException() {
        return BaseResult.buildError(
            SystemError.PARAM_ERROR.getCode(),
            SystemError.PARAM_ERROR.getMessage()
        );
    }

    /**
     * 统一处理参数预校验异常.
     *
     * @param ex 异常.
     * @return {@link BaseResult}
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object validationError(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        final List<FieldError> fieldErrors = result.getFieldErrors();
        return BaseResult.buildError(
            SystemError.PARAM_ERROR.getCode(),
            fieldErrors.get(0).getDefaultMessage()
        );
    }

    /**
     * 统一处理参数预校验异常: Spring将数据解释为Web表单数据（而不是JSON）。
     * Spring使用FormHttpMessageConverter将POST主体转换为域对象，并导致BindException.
     *
     * @param ex 异常.
     * @return {@link BaseResult}
     */
    @ExceptionHandler(BindException.class)
    public Object bindError(BindException ex) {
        BindingResult result = ex.getBindingResult();
        final List<FieldError> fieldErrors = result.getFieldErrors();
        return BaseResult.buildError(
            SystemError.PARAM_ERROR.getCode(),
            fieldErrors.get(0).getDefaultMessage()
        );
    }

    /**
     * Validate注解所抛异常.
     *
     * @param exception 异常
     * @return {@link BaseResult}
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Object handle(ConstraintViolationException exception) {
        Set<ConstraintViolation<?>> set = exception.getConstraintViolations();
        return BaseResult.buildError(
            SystemError.PARAM_ERROR.getCode(),
            set.iterator().next().getMessage()
        );
    }

    /**
     * HttpRequestMethodNotSupported.
     *
     * @return {@link Object}
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Object httpRequestMethodNotSupportedException() {
        return BaseResult.buildError(
            SystemError.NON_SUPPORT_METHOD_ERROR.getCode(),
            SystemError.NON_SUPPORT_METHOD_ERROR.getMessage()
        );
    }

    /**
     * feign decode异常处理.
     *
     * @return {@link BaseResult}
     */
    @ExceptionHandler(DecodeException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Object handleException(DecodeException ex) {
        if (ex.getCause() instanceof BizException) {
            return BaseResult.buildError(
                ((BizException) ex.getCause()).getCode(),
                ex.getCause().getMessage()
            );
        } else {
            log.error(ex.getMessage(), ex);
            return BaseResult.buildError(SystemError.SYSTEM_ERROR.getCode(), SystemError.SYSTEM_ERROR.getMessage());
        }
    }

    /**
     * BizBaseException异常.
     *
     * @return {@link BaseResult}
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(BizBaseException.class)
    public BaseResult bizBaseException(BizBaseException e) {
        log.warn("{}", e.getMessage());
        return BaseResult.buildError(e.getCode(), e.getMessage());
    }

    /**
     * BizException异常.
     *
     * @return {@link BaseResult}
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(BizException.class)
    public BaseResult bizException(BizException e) {
        log.error("", e);
        return BaseResult.buildError(e.getCode(), e.getMessage());
    }

    /**
     * SystemException异常.
     *
     * @return {@link BaseResult}
     */
    @ExceptionHandler(SystemException.class)
    public BaseResult systemException(SystemException e) {
        log.error("", e);
        return BaseResult.buildError(e.getCode(), e.getMessage());
    }

    /**
     * Exception异常.
     *
     * @return {@link BaseResult}
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(Exception.class)
    public BaseResult exception(Exception e) {
        log.error("", e);
        return BaseResult.buildError(SystemError.SYSTEM_ERROR.getCode(), SystemError.SYSTEM_ERROR.getMessage());
    }
}
