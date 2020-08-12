package cn.bfay.web.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * WebUtils.
 *
 * @author wangjiannan
 */
public class WebUtils extends org.springframework.web.util.WebUtils {
    private static final Logger log = LoggerFactory.getLogger(WebUtils.class);

    /**
     * 获取本机ip.
     *
     * @return String
     */
    public static String getLocalIp() {
        try {
            String ipString = "";
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    ip = (InetAddress) addresses.nextElement();
                    if (ip != null && ip instanceof Inet4Address && !ip.getHostAddress().equals("127.0.0.1")) {
                        return ip.getHostAddress();
                    }
                }
            }
            return ipString;
        } catch (Exception e) {
            e.printStackTrace();
            return "127.0.0.1";
        }

    }

    /**
     * 获取请求的真实IP.
     *
     * @param request req
     * @return string
     */
    public static String getRealIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.isNotBlank(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            //多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = ip.indexOf(",");
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        }
        ip = request.getHeader("X-Real-IP");
        if (StringUtils.isNotBlank(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            return ip;
        }
        return request.getRemoteAddr();
    }

    /**
     * 从nginx里获取转换过后的用户ip.
     *
     * @param request {@link HttpServletRequest}
     * @return String of ip address
     */
    public static String getRemoteAddressHost(HttpServletRequest request) {
        String host = request.getHeader("X-Real-IP");
        if (StringUtils.isBlank(host) || "unknown".equalsIgnoreCase(host)) {
            host = request.getHeader("X-Forwarded-For");
        }
        if (StringUtils.isBlank(host) || "unknown".equalsIgnoreCase(host)) {
            host = request.getHeader("ip");//这个是客户端传的
        }
        if (StringUtils.isBlank(host) || "unknown".equalsIgnoreCase(host)) {
            host = request.getRemoteAddr();
        }
        if (StringUtils.isNotBlank(host) && host.contains(",")) {
            //取最后一个IP
            //host = host.substring(host.lastIndexOf(",") + 1, host.length()).trim();
            //取第一个IP
            host = host.substring(0, host.indexOf(",")).trim();
        }
        return host;
    }

    /**
     * 获得headers的json字符串.
     *
     * @param request {@link HttpServletRequest}
     * @return json String
     */
    public static String getHeadersJson(HttpServletRequest request) {
        Map<String, String> resultMap = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            String value = request.getHeader(key);
            resultMap.put(key, value);
        }
        String result = null;
        try {
            result = new ObjectMapper().writeValueAsString(resultMap);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * 判断是否ajax请求 spring ajax 返回含有 ResponseBody 或者 RestController注解.
     *
     * @param handlerMethod HandlerMethod
     * @return 是否ajax请求
     */
    public static boolean isAjax(HandlerMethod handlerMethod) {
        ResponseBody responseBody = handlerMethod.getMethodAnnotation(ResponseBody.class);
        if (null != responseBody) {
            return true;
        }
        // 获取类上面的Annotation，可能包含组合注解，故采用spring的工具类
        Class<?> beanType = handlerMethod.getBeanType();
        responseBody = AnnotationUtils.getAnnotation(beanType, ResponseBody.class);
        if (null != responseBody) {
            return true;
        }
        return false;
    }

    /**
     * 读取cookie.
     *
     * @param request req
     * @param key     cookie名称
     * @return string
     */
    public static String getCookieValue(HttpServletRequest request, String key) {
        Cookie cookie = getCookie(request, key);
        return cookie != null ? cookie.getValue() : null;
    }

    /**
     * 清除 某个指定的cookie.
     *
     * @param response rep
     * @param key      cookie名称
     */
    public static void removeCookie(HttpServletResponse response, String key) {
        setCookie(response, key, null, 0);
    }

    /**
     * 设置cookie.
     *
     * @param response        rep
     * @param key             cookie名称
     * @param value           cookie值
     * @param maxAgeInSeconds 生存周期
     */
    public static void setCookie(HttpServletResponse response, String key, String value, int maxAgeInSeconds) {
        Cookie cookie = new Cookie(key, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(maxAgeInSeconds);
        // 将cookie对象添加到response对象中，这样服务器在输出response对象中的内容时就会把cookie也输出到客户端浏览器
        response.addCookie(cookie);
    }
}
