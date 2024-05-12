package dev.eshan.userservice.dtos;

import lombok.Data;

@Data
public class ValidateTokenRequestDto {
    private Long userId;
    private String token;
}
