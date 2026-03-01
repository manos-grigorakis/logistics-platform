package com.manosgrigorakis.logisticsplatform.security;

import com.manosgrigorakis.logisticsplatform.auth.model.UserInfoDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUser {

    public CurrentUser() {
    }

    /**
     * Returns authenticated user's ID from the Spring Security context
     * @return Authenticated user's id or null if no user is authenticated
     */
    public Long getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            if(principal instanceof UserInfoDetails userInfoDetails) {
                return userInfoDetails.getUserId();
            }
        }

        return null;
    }
}
