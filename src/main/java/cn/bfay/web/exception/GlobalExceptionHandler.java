package cn.bfay.web.exception;

import cn.bfay.web.exception.enums.SystemError;
import cn.bfay.web.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.websocket.DecodeException;

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
public class GlobalExceptionHandler {// extends ResponseEntityExceptionHandler
    ///**
    // * 应用到所有@RequestMapping注解方法，在其执行之前初始化数据绑定器.
    // *
    // * @param binder b
    // */
    //@InitBinder
    //public void initBinder(WebDataBinder binder) {
    //}

    ///**
    // * 把值绑定到Model中，使全局@RequestMapping可以获取到该值.
    // * 在Model上设置的值，对于所有被 @RequestMapping 注解的方法中，都可以通过 ModelMap 获取，如
    // * ,@RequestMapping("/home")
    // * public String home(ModelMap modelMap) {
    // * System.out.println(modelMap.get("author"));
    // * }
    // * //或者 通过@ModelAttribute获取
    // * ,@RequestMapping("/home")
    // * public String home(@ModelAttribute("author") String author) {
    // * System.out.println(author);
    // * }
    // *
    // * @param model m
    // */
    //@ModelAttribute
    //public void addAttributes(Model model) {
    //    model.addAttribute("author", "Magical Sam");
    //}

    /**
     * 400 (Bad Request):
     * MissingServletRequestParameterException-缺少参数;
     * TypeMismatchException-参数类型不匹配;
     * HttpMessageNotReadableException-参数不可读,要求json形式,但实际是String类型.
     *
     * @param e 异常.
     * @return {@link Result}
     */
    @ExceptionHandler( {MissingServletRequestParameterException.class, TypeMismatchException.class, HttpMessageNotReadableException.class})
    public Result requestMissingServletRequestParameter(Exception e) {
        log.warn("", e);
        return Result.buildError(400, "Bad Request");
    }

    ///**
    // * 404 (No Such Method ERROR).
    // *
    // * @param e 异常.
    // * @return {@link Result}
    // */
    //@ExceptionHandler(NoHandlerFoundException.class)
    //public Result ooHandlerFound(Exception e) {
    //    log.warn("", e);
    //    return Result.buildError(404, "Not Found");
    //}

    /**
     * 405 (Method Not Allowed).
     *
     * @param e 异常.
     * @return {@link Result}
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result requestHttpRequestMethodNotSupported(Exception e) {
        log.warn("", e);
        return Result.buildError(405, "Method Not Allowed");
    }

    /**
     * 406 (Not Acceptable).
     *
     * @param e 异常.
     * @return {@link Result}
     */
    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public Result requestHttpMediaTypeNotAcceptable(Exception e) {
        log.warn("", e);
        return Result.buildError(406, "Not Acceptable");
    }

    /**
     * 415 (Unsupported Media Type).
     *
     * @param e 异常.
     * @return {@link Result}
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public Result requestHttpMediaTypeNotSupported(Exception e) {
        log.warn("", e);
        return Result.buildError(415, "Unsupported Media Type");
    }

    /**
     * 500 (Internal Server Error).
     *
     * @param e 异常.
     * @return {@link Result}
     */
    @ExceptionHandler( {ConversionNotSupportedException.class, HttpMessageNotWritableException.class})
    public Result server500(Exception e) {
        log.warn("", e);
        return Result.buildError(500, "Internal Server Error");
    }

    /**
     * Exception to be thrown when validation on an argument annotated with @Valid fails.
     *
     * @param e 异常.
     * @return {@link Result}
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        // 获得所有错误，包括全局错误和字段错误。
        //e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        // 获得与给定字段关联的所有错误
        String message = e.getBindingResult().getFieldErrors().get(0).getDefaultMessage();

        //AppResponse response = new AppResponse();
        //response.setFail(e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        //return response;
        return Result.buildError(SystemError.SYSTEM_ERROR.getCode(), message);
    }

    /**
     * Validate注解所抛异常.
     *
     * @param e 异常
     * @return {@link Result}
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result handle(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> set = e.getConstraintViolations();
        //String aa =set.iterator().next().getMessage();
        //String bb =set.iterator().next().getMessageTemplate();
        return Result.buildError(SystemError.SYSTEM_ERROR.getCode(), set.iterator().next().getMessage());
    }

    /**
     * feign decode异常处理.
     *
     * @return {@link Result}
     */
    @ExceptionHandler(DecodeException.class)
    //@ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Object handleException(DecodeException ex) {
        if (ex.getCause() instanceof BfayException) {
            return Result.buildError(
                ((BfayException) ex.getCause()).getCode(),
                ((BfayException) ex.getCause()).getMessage()
            );
        } else {
            log.error(ex.getMessage(), ex);
            return Result.buildError(SystemError.SYSTEM_ERROR.getCode(), SystemError.SYSTEM_ERROR.getMessage());
        }
    }

    /**
     * 异常.
     *
     * @return {@link Result}
     */
    //@ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(BfayException.class)
    public Result bfayException(BfayException e) {
        if (String.valueOf(e.getCode()).startsWith("101")) {
            log.warn("业务错误{},{}", e.getCode(), e.getMessage());
        } else {
            log.error("", e);
        }
        return Result.buildError(e.getCode(), e.getMessage());
    }

    /**
     * 异常.
     *
     * @return {@link Result}
     */
    //@ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(Exception.class)
    public Result exception(Exception e) {
        log.error("", e);
        return Result.buildError(SystemError.SYSTEM_ERROR.getCode(), SystemError.SYSTEM_ERROR.getMessage());
    }

}
