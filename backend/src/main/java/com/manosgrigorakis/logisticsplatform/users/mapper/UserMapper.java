package com.manosgrigorakis.logisticsplatform.users.mapper;

import com.manosgrigorakis.logisticsplatform.users.dto.UserRequestDTO;
import com.manosgrigorakis.logisticsplatform.users.dto.UserResponseDTO;
import com.manosgrigorakis.logisticsplatform.users.model.Role;
import com.manosgrigorakis.logisticsplatform.users.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring",  builder = @org.mapstruct.Builder(disableBuilder = true))
public interface UserMapper {
    @Mapping(target = "role", source = "role")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "phone", source = "dto.phone", qualifiedByName = "normalizePhone")
    User toEntity(UserRequestDTO dto, Role role);

    @Mapping(target = "role", source = "role")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "phone", source = "dto.phone", qualifiedByName = "normalizePhone")
    void toUpdate(@MappingTarget User user, UserRequestDTO dto, Role role);

    @Mapping(target = "roleId", source = "role.id")
    @Mapping(target = "roleName", source = "role.name")
    UserResponseDTO toResponse(User user);

    @Named("normalizePhone")
    default String normalizePhone(String phone) {
        if (phone == null || phone.isBlank()) return null;
        return phone;
    }
}
