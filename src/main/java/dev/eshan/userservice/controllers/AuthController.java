package dev.eshan.userservice.controllers;

import dev.eshan.userservice.dtos.*;
import dev.eshan.userservice.models.JwtObject;
import dev.eshan.userservice.models.SessionStatus;
import dev.eshan.userservice.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody LoginRequestDto request) throws Exception {
        return authService.login(request.getEmail(), request.getPassword());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LogoutRequestDto request) {
        return authService.logout(request.getToken(), request.getUserId());
    }

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signUp(@RequestBody SignUpRequestDto request) {
        UserDto userDto = authService.signUp(request.getEmail(), request.getPassword());
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

//    @GetMapping("/signup")
//    public String signUp() {
////        UserDto userDto = authService.signUp(request.getEmail(), request.getPassword());
//        return "hello";
//    }

    @PostMapping("/validate")
    public ResponseEntity<SessionStatus> validateToken(@RequestBody ValidateTokenRequestDto request) {
        SessionStatus sessionStatus = authService.validateToken(request.getToken(), request.getUserId());
        return new ResponseEntity<>(sessionStatus, HttpStatus.OK);
    }

    @PostMapping("/jwtDetails")
    public ResponseEntity<JwtObject> getUserDetails(@RequestBody ValidateTokenRequestDto request) {
        JwtObject jwtObject = authService.getJwtDetails(request.getToken(), request.getUserId());
        return new ResponseEntity<>(jwtObject, HttpStatus.OK);
    }
}
