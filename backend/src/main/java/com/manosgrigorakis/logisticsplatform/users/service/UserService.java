package com.manosgrigorakis.logisticsplatform.users.service;

import com.manosgrigorakis.logisticsplatform.users.dto.UserRequestDTO;
import com.manosgrigorakis.logisticsplatform.users.dto.UserResponseDTO;

import java.util.List;

public interface UserService {
    List<UserResponseDTO> getAllUsers();

    UserResponseDTO getUserById(Long id);

    UserResponseDTO createUser(UserRequestDTO dto);

    UserResponseDTO updateUserById(Long id, UserRequestDTO dto);

    void deleteUserById(Long id);
}
