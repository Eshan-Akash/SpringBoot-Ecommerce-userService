package dev.eshan.userservice.services;

import dev.eshan.userservice.dtos.*;
import dev.eshan.userservice.models.SessionStatus;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<UserDto> login(String email, String password) throws Exception;
    ResponseEntity<Void> logout(String token, Long userId);
    UserDto signUp(String email, String password);
    SessionStatus validateToken(String token, Long userId);
}
