package com.manosgrigorakis.logisticsplatform.common.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class ClientInfo {

    public ClientInfo() {
    }

    /**
     * Returns the original client IP address
     * If there is `X-Forwarded-For` header, it will return the first value in the chain.
     * Otherwise, the remote address is returned
     * @return Client IP or null
     */
    public String getClientIp() {
        HttpServletRequest request = this.getCurrentRequest();

        if (request == null) return null;

        String header = request.getHeader("X-Forwarded-For");

        if (header != null && !header.isEmpty()) {
            return header.split(",")[0];
        }

        return request.getRemoteAddr();
    }

    /**
     * Gets the current user agent header from the HTTP request
     * @return user agent or null from the request
     */
    public String getUserAgent() {
        HttpServletRequest request = this.getCurrentRequest();

        if (request == null) return null;

        return request.getHeader("User-Agent");
    }

    /**
     * Returns the current request bound to this thread
     * @return Current request attributes or null
     */
    private HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if(attributes == null) return null;
        return attributes.getRequest();
    }
}
