package cn.bfay.web.filter;

import cn.bfay.web.util.WebUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TraceLoggingFilter.
 *
 * @author wangjiannan
 */
@Slf4j
public class TraceLoggingFilter extends OncePerRequestFilter {
    private static final String TRACE_ID = "TRACE_ID";
    private static final Set<String> EXCLUDE_URL = new HashSet<>(24);
    private static final int[] HTTP_SUCCESS_CODE = new int[4];
    private static final String RESULT_CODE = "errCode";
    private static final String SUCCESS = "SUCCESS";
    private static final String FAILED = "FAILED";

    public TraceLoggingFilter() {
    }

    private String contxtPath;

    static {
        EXCLUDE_URL.add("/health");
        EXCLUDE_URL.add("/beans");
        EXCLUDE_URL.add("/trace");
        EXCLUDE_URL.add("/mappings");
        EXCLUDE_URL.add("/configprops");
        EXCLUDE_URL.add("/metrics");
        EXCLUDE_URL.add("/env");
        EXCLUDE_URL.add("/info");
        EXCLUDE_URL.add("/dump");
        EXCLUDE_URL.add("/autoconfig");
        EXCLUDE_URL.add("/favicon.ico");
        EXCLUDE_URL.add("/swagger-");
        EXCLUDE_URL.add("/webjars");
        EXCLUDE_URL.add("/v2/api-docs");
        EXCLUDE_URL.add("/static");
        EXCLUDE_URL.add("/page");

        HTTP_SUCCESS_CODE[0] = HttpServletResponse.SC_OK;
        HTTP_SUCCESS_CODE[1] = HttpServletResponse.SC_NOT_MODIFIED;
        HTTP_SUCCESS_CODE[2] = HttpServletResponse.SC_MOVED_PERMANENTLY;
        HTTP_SUCCESS_CODE[3] = HttpServletResponse.SC_MOVED_TEMPORARILY;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        boolean isFirstRequest = !isAsyncDispatch(request);
        HttpServletRequest requestToUse = request;
        HttpServletResponse responseToUse = response;

        if (isFirstRequest) {
            if (!(request instanceof ContentCachingRequestWrapper)) {
                requestToUse = new ContentCachingRequestWrapper(request);
            }
            if (!(response instanceof ContentCachingResponseWrapper)) {
                responseToUse = new ContentCachingResponseWrapper(response);
            }
        }
        Date startTime = new Date();
        long elapsedTime;
        try {
            filterChain.doFilter(requestToUse, responseToUse);
        } finally {
            elapsedTime = System.currentTimeMillis() - startTime.getTime();
            ContentCachingResponseWrapper wrapper =
                    WebUtils.getNativeResponse(responseToUse, ContentCachingResponseWrapper.class);
            if (isFirstRequest && !isAsyncStarted(requestToUse) && shouldLog(request)) {
                printLog(WebUtils.getNativeRequest(requestToUse, ContentCachingRequestWrapper.class), wrapper,
                        elapsedTime);
            }
            if (wrapper != null) {
                wrapper.copyBodyToResponse();
            }
        }
    }

    private boolean shouldLog(HttpServletRequest request) {
        String url = request.getRequestURI();
        String contextPath = request.getServletContext().getContextPath();
        boolean[] shouldLog = new boolean[]{true};
        EXCLUDE_URL.forEach(uurl -> {
            if (url.startsWith(contextPath.concat(uurl))) {
                shouldLog[0] = false;
                return;
            }
        });
        return shouldLog[0];
    }

    private void printLog(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response,
                          long elapsedTime) {
        try {
            String url = request.getRequestURL().toString();
            String callMethod = request.getRequestURI();
            String httpMethod = request.getMethod();
            String callIp = WebUtils.getRemoteAddressHost(request);
            String localIp = request.getLocalAddr();
            String inputParamOri = getRequestString(request);
            String headersOri = WebUtils.getHeadersJson(request);
            String userid = WebUtils.getCookieValue(request, "userid");
            userid = StringUtils.isBlank(userid) ? "unknown-userid" : userid;

            String outParamOri = getResponseString(response);
            int resultCode = getResultCode(outParamOri, response);

            String logStatus = (0 == resultCode) ? SUCCESS : FAILED;

            //格式:issuccess|userid|calltype[HTTP|RPC]|httpmethod|url|retcode|method|etime
            // |sourceip|localIp|req|resp|headers
            String accessTokenFomat = "{}|{}|{}|{}|{}|{}|{}|{}|{}|{}|{}|{}|{}";
            log.info(accessTokenFomat,
                    logStatus,
                    userid,
                    "HTTP",
                    httpMethod,
                    url,
                    String.valueOf(resultCode),
                    callMethod,
                    String.valueOf(elapsedTime),
                    callIp,
                    localIp,
                    inputParamOri,
                    outParamOri,
                    headersOri);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }

    private int getResultCode(String outParamOri, HttpServletResponse response) {
        Pattern pattern = Pattern.compile("\"" + RESULT_CODE + "\":\"(\\d+)\"");
        Matcher matcher = pattern.matcher(outParamOri);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        } else {
            return isHttpSucceed(response.getStatus()) ? 0 : response.getStatus();
        }
    }

    private boolean isHttpSucceed(int statusCode) {
        return ArrayUtils.contains(HTTP_SUCCESS_CODE, statusCode);
    }


    private String getRequestString(ContentCachingRequestWrapper request) {
        String payload = "{}";
        if (request != null) {
            if (request.getMethod().equalsIgnoreCase(HttpMethod.POST.name())) {
                byte[] buf = request.getContentAsByteArray();
                if (buf.length > 0 && buf.length < 1024 * 64) {
                    try {
                        payload = new String(buf, 0, buf.length, request.getCharacterEncoding());
                    } catch (UnsupportedEncodingException ex) {
                        payload = "[unknown]";
                    }
                } else if (buf.length >= 1024 * 64) {
                    return String.format("[too long content, length = %s]", buf.length);
                }
            } else {
                payload = request.getQueryString() == null ? "{}" : request.getQueryString();
            }
        }
        return payload;
    }

    private String getResponseString(ContentCachingResponseWrapper response) throws IOException {
        if (response != null) {
            if (shouldPrintContent(response)) {
                byte[] buf = response.getContentAsByteArray();
                if (buf.length > 0 && buf.length < 1024 * 64) {
                    String payload;
                    try {
                        payload = new String(buf, 0, buf.length, response.getCharacterEncoding());
                    } catch (UnsupportedEncodingException ex) {
                        payload = "[unknown]";
                    }
                    return payload;
                } else if (buf.length >= 1024 * 64) {
                    return String.format("[too long content, length = %s]", buf.length);
                }
            } else {
                return "[" + response.getHeader("location") + "]";
            }
        }
        return "{}";
    }

    private boolean shouldPrintContent(HttpServletResponse response) {
        String contentType = response.getContentType();
        if (null != contentType) {
            MediaType mediaType = MediaType.parseMediaType(contentType);
            return MediaType.APPLICATION_JSON.includes(mediaType)
                    || contentType.startsWith(MediaType.TEXT_HTML_VALUE)
                    || contentType.startsWith(MediaType.TEXT_PLAIN_VALUE);
        } else {
            return false;
        }
    }
}
