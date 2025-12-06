package com.manosgrigorakis.logisticsplatform.auth.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponseDTO {
    private String tokenType = "Bearer";
    private String token;
    private UserDetailsDTO user;
}
