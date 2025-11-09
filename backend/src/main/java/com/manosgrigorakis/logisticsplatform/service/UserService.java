package com.manosgrigorakis.logisticsplatform.service;

import com.manosgrigorakis.logisticsplatform.dto.user.UserRequestDTO;
import com.manosgrigorakis.logisticsplatform.dto.user.UserResponseDTO;

import java.util.List;

public interface UserService {
    List<UserResponseDTO> getAllUsers();

    UserResponseDTO getUserById(Long id);

    UserResponseDTO createUser(UserRequestDTO dto);

    UserResponseDTO updateUserById(Long id, UserRequestDTO dto);

    void deleteUserById(Long id);
}
