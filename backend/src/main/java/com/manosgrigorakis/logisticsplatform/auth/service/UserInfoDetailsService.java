package com.manosgrigorakis.logisticsplatform.auth.service;

import com.manosgrigorakis.logisticsplatform.users.model.User;
import com.manosgrigorakis.logisticsplatform.auth.model.UserInfoDetails;
import com.manosgrigorakis.logisticsplatform.users.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserInfoDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final Logger log = LoggerFactory.getLogger(UserInfoDetailsService.class);

    public UserInfoDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isEmpty()) {
            log.warn("Load user failed. User {} not found", email);
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        // Convert User to UserInfoDetails
        return new UserInfoDetails(user.get());
    }


    public UserInfoDetails loadUserById(Long id) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findById(id);

        if (user.isEmpty()) {
            log.warn("Load user failed. User {} not found", id);
            throw new UsernameNotFoundException("User not found with id: " + id);
        }

        // Convert User to UserInfoDetails
        return new UserInfoDetails(user.get());
    }
}
