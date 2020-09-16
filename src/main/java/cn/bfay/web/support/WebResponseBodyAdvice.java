package cn.bfay.web.support;

import cn.bfay.web.result.BaseResult;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.HashSet;
import java.util.Set;


/**
 * 统一返回出口.
 *
 * @author wangjiannan
 */
@RestControllerAdvice
public class WebResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    private static final Set<String> EXCLUDE_URL = new HashSet<>(3);

    static {
        EXCLUDE_URL.add("/swagger-resources/configuration/ui");
        EXCLUDE_URL.add("/swagger-resources");
        EXCLUDE_URL.add("/v2/api-docs");
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return !BaseResult.class.isAssignableFrom(returnType.getParameterType())
            && MappingJackson2HttpMessageConverter.class.isAssignableFrom(converterType);
    }

    @Override
    public Object beforeBodyWrite(Object resultData,
                                  MethodParameter methodParameter,
                                  MediaType mediaType,
                                  Class<? extends HttpMessageConverter<?>> converterType,
                                  ServerHttpRequest serverHttpRequest,
                                  ServerHttpResponse serverHttpResponse) {
        if (null == resultData) {
            return BaseResult.buildSuccess();
        }

        if (!shouldBuildResult(serverHttpRequest)) {
            return resultData;
        }

        if (resultData instanceof MappingJacksonValue) {
            MappingJacksonValue result = (MappingJacksonValue) resultData;
            Object body = result.getValue();
            result.setValue(body == null ? BaseResult.buildSuccess() : BaseResult.buildSuccess(body));
            return result;
        }
        return BaseResult.buildSuccess(resultData);
    }

    private boolean shouldBuildResult(ServerHttpRequest request) {
        String url = request.getURI().getPath();
        return !EXCLUDE_URL.contains(url);
    }
}
