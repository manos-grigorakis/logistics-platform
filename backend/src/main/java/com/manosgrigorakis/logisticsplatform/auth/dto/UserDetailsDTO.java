package com.manosgrigorakis.logisticsplatform.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDetailsDTO {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
}
