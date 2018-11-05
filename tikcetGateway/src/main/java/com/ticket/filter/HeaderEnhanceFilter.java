package com.ticket.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.zuul.context.RequestContext;
import com.ticket.contants.SecurityConstants;
import com.ticket.properties.PermitAllUrlProperties;
import org.apache.commons.lang.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.*;

/**
 * 加强头部
 */
public class HeaderEnhanceFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeaderEnhanceFilter.class);

    private static final String ANONYMOUS_USER_ID = "d4a65d04-a5a3-465c-8408-405971ac3346";

    @Autowired
    private PermitAllUrlProperties permitAllUrlProperties;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String authorization = ((HttpServletRequest) servletRequest).getHeader("Authorization");
         String requestURI = ((HttpServletRequest) servletRequest).getRequestURI();
        // test if request url is permit all , then remove authorization from header
        LOGGER.info(String.format("Enhance request URI : %s.", requestURI));
        //将isPermitAllUrl的请求进行传递
        if(isPermitAllUrl(requestURI) && isNotOAuthEndpoint(requestURI)) {
            //移除头部，但不包括登录端点的头部
            HttpServletRequest resetRequest = removeValueFromRequestHeader((HttpServletRequest) servletRequest);
            filterChain.doFilter(resetRequest, servletResponse);
            return;
        }
        //判断是不是符合规范的头部
        if (StringUtils.isNotEmpty(authorization)) {
            if (isJwtBearerToken(authorization)) {
                try {
                    authorization = StringUtils.substringBetween(authorization, ".");
                    String decoded = new String(Base64.decodeBase64(authorization));

                    Map properties = new ObjectMapper().readValue(decoded, Map.class);
                    //解析authorization中的token，构造USER_ID_IN_HEADER
                    String userId = (String) properties.get(SecurityConstants.USER_ID_IN_HEADER);

                    RequestContext.getCurrentContext().addZuulRequestHeader(SecurityConstants.USER_ID_IN_HEADER, userId);
                } catch (Exception e) {
                    LOGGER.error("Failed to customize header for the request, but still release it as the it would be regarded without any user details.", e);
                }
            }
        } else {
            //为了适配，设置匿名头部
            LOGGER.info("Regard this request as anonymous request, so set anonymous user_id in the header.");
            RequestContext.getCurrentContext().addZuulRequestHeader(SecurityConstants.USER_ID_IN_HEADER, ANONYMOUS_USER_ID);
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }

    private boolean isJwtBearerToken(String token) {
        return StringUtils.countMatches(token, ".") == 2 && (token.startsWith("Bearer") || token.startsWith("bearer"));
    }

    private boolean isNotOAuthEndpoint(String requestURI) {
        return !requestURI.contains("/login");
    }

    private HttpServletRequestWrapper removeValueFromRequestHeader(HttpServletRequest request) {
        HttpServletRequestWrapper httpServletRequestWrapper = new HttpServletRequestWrapper(request) {
            private Set<String> headerNameSet;
            private Set<String> headerSet;

            @Override
            public Enumeration<String> getHeaderNames() {
                if (headerNameSet == null) {
                    // first time this method is called, cache the wrapped request's header names:
                    headerNameSet = new HashSet();
                    Enumeration<String> wrappedHeaderNames = super.getHeaderNames();
                    while (wrappedHeaderNames.hasMoreElements()) {
                        String headerName = wrappedHeaderNames.nextElement();
                        if (!"Authorization".equalsIgnoreCase(headerName)) {
                            headerNameSet.add(headerName);
                        }
                    }
                    //set default header name value of tenant id and user id
                    headerNameSet.add(SecurityConstants.USER_ID_IN_HEADER);
                }

                return Collections.enumeration(headerNameSet);
            }

            @Override
            public Enumeration<String> getHeaders(String name) {

                if ("Authorization".equalsIgnoreCase(name)) {
                    return Collections.emptyEnumeration();
                }
                if (SecurityConstants.USER_ID_IN_HEADER.equalsIgnoreCase(name)) {
                    headerSet = new HashSet();
                    headerSet.add(ANONYMOUS_USER_ID);
                    return Collections.enumeration(headerSet);
                }
                return super.getHeaders(name);
            }

            @Override
            public String getHeader(String name) {
                if ("Authorization".equalsIgnoreCase(name)) {
                    return null;
                }
                if (SecurityConstants.USER_ID_IN_HEADER.equalsIgnoreCase(name)) {
                    return ANONYMOUS_USER_ID;
                }
                return super.getHeader(name);
            }
        };
        return httpServletRequestWrapper;
    }

    private boolean isPermitAllUrl(String url) {
        return permitAllUrlProperties.isPermitAllUrl(url);
    }

}
