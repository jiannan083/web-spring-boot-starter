package cn.bfay.web.interceptor;

import cn.bfay.web.util.WebUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * OkhttpLoggingInterceptor.
 *
 * @author wangjiannan
 */
public class OkhttpLoggingInterceptor implements Interceptor {
    Logger logger = LoggerFactory.getLogger(OkhttpLoggingInterceptor.class);

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Date startTime = new Date();
        Response response = null;
        try {
            response = chain.proceed(request);
            //这里不能直接使用response.body().string()的方式输出日志
            //因为response.body().string()之后，response中的流会被关闭，程序会报错，我们需要创建出一
            //个新的response给应用层处理
            long responseLength = response.body().contentLength();
            ResponseBody responseBody = response.peekBody(responseLength < 0 ? 1024 * 1024 : responseLength);
            log(request, startTime, response.code(), responseBody.string());
        } catch (IOException e) {
            log(request, startTime, 500, "exception response");
            throw e;
        }
        return response;
    }

    private void log(Request request, Date startTime, int responseCode, String responseBody) throws IOException {
        //格式:|startTime|issuccess|userid|calltype[HTTP|RPC]|httpmethod|url|retcode|method|etime
        // |sourceip|localIp|req|resp|headers
        String logFomat = "{}|{}|{}|{}|{}|{}|{}|{}|{}|{}|{}|{}|{}";
        logger.info(logFomat,
                isHttpSucceed(responseCode) ? "SUCCESS" : "FAILED",
                "",
                "HTTP",
                request.method(),
                request.url(),
                responseCode,
                request.url(),
                String.valueOf(System.currentTimeMillis() - startTime.getTime()),
                WebUtils.getLocalIp(),
                WebUtils.getLocalIp(),
                stringifyRequestBody(request),
                responseBody,
                getHeadersJson(request.headers()));
    }


    private static final int[] HTTP_SUCCESS_CODE = new int[4];

    static {
        HTTP_SUCCESS_CODE[0] = 200;
        HTTP_SUCCESS_CODE[1] = 304;
        HTTP_SUCCESS_CODE[2] = 301;
        HTTP_SUCCESS_CODE[3] = 302;
    }

    private boolean isHttpSucceed(int statusCode) {
        return ArrayUtils.contains(HTTP_SUCCESS_CODE, statusCode);
    }

    private String stringifyRequestBody(Request request) {
        if (request.body() != null) {
            try {
                final Request copy = request.newBuilder().build();
                //上传文件不打印内容，返回length.
                MediaType contentType = copy.body().contentType();
                if (contentType != null && contentType.toString().contains("multipart/form-data")) {
                    return copy.body().contentLength() + "";
                }
                final Buffer buffer = new Buffer();
                copy.body().writeTo(buffer);
                return buffer.readUtf8();
            } catch (final IOException e) {
                logger.warn("Failed to stringify request body: " + e.getMessage());
            }
        }
        return "";
    }

    private String getHeadersJson(Headers headers) {
        if (headers == null) {
            return null;
        } else {
            Map<String, String> resultMap = new HashMap<>();
            headers.names().forEach((key) -> {
                resultMap.put(key, headers.get(key));
            });
            ObjectMapper objectMapper = new ObjectMapper();
            String result = null;

            try {
                result = objectMapper.writeValueAsString(resultMap);
            } catch (JsonProcessingException var6) {
                logger.error(var6.getMessage(), var6);
            }

            return result;
        }
    }
}
