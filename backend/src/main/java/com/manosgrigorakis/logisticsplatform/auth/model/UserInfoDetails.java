package com.manosgrigorakis.logisticsplatform.auth.model;

import com.manosgrigorakis.logisticsplatform.users.enums.UserStatus;
import com.manosgrigorakis.logisticsplatform.users.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserInfoDetails implements UserDetails {
    private final User user;

    public UserInfoDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(user.getRole().getName()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    } // Use email instead of username

    public Long getUserId() {return user.getId();};

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !UserStatus.SUSPENDED.equals(user.getStatus());
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(user.getEnabled());
    }
}
