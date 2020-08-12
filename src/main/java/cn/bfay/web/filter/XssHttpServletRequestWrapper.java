package cn.bfay.web.filter;

import cn.bfay.web.util.XssUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;


/**
 * 类说明.
 *
 * @author wangjiannan
 */
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {
    HttpServletRequest orgRequest = null;
    private boolean isIncludeRichText = false;

    public XssHttpServletRequestWrapper(HttpServletRequest request, boolean isIncludeRichText) {
        super(request);
        orgRequest = request;
        this.isIncludeRichText = isIncludeRichText;
    }

    /**
     * 获取最原始的request的静态方法.
     *
     * @return {@link HttpServletRequest}
     */
    public static HttpServletRequest getOrgRequest(HttpServletRequest req) {
        if (req instanceof XssHttpServletRequestWrapper) {
            return ((XssHttpServletRequestWrapper) req).getOrgRequest();
        }

        return req;
    }

    /**
     * 覆盖getParameter方法，将参数名和参数值都做xss过滤.<br/>
     * 如果需要获得原始的值，则通过super.getParameterValues(name)来获取<br/>
     * getParameterNames,getParameterValues和getParameterMap也可能需要覆盖.
     */
    @Override
    public String getParameter(String name) {
        Boolean flag = ("content".equals(name) || name.endsWith("WithHtml"));
        if (flag && !isIncludeRichText) {
            return super.getParameter(name);
        }
        name = XssUtils.clean(name);
        String value = super.getParameter(name);
        if (StringUtils.isNotBlank(value)) {
            value = XssUtils.clean(value);
        }
        return value;
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] arr = super.getParameterValues(name);
        if (arr != null) {
            for (int i = 0; i < arr.length; i++) {
                arr[i] = XssUtils.clean(arr[i]);
            }
        }
        return arr;
    }

    /**
     * 覆盖getHeader方法，将参数名和参数值都做xss过滤。<br/>
     * 如果需要获得原始的值，则通过super.getHeaders(name)来获取<br/>
     * getHeaderNames 也可能需要覆盖
     */
    @Override
    public String getHeader(String name) {
        name = XssUtils.clean(name);
        String value = super.getHeader(name);
        if (StringUtils.isNotBlank(value)) {
            value = XssUtils.clean(value);
        }
        return value;
    }

    /**
     * 获取最原始的request.
     *
     * @return {@link HttpServletRequest}
     */
    public HttpServletRequest getOrgRequest() {
        return orgRequest;
    }

}
