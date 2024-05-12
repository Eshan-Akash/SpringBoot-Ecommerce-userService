package dev.eshan.userservice.services;

import dev.eshan.userservice.dtos.*;
import dev.eshan.userservice.models.Session;
import dev.eshan.userservice.models.SessionStatus;
import dev.eshan.userservice.models.User;
import dev.eshan.userservice.repositories.SessionRepository;
import dev.eshan.userservice.repositories.UserRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.util.MultiValueMapAdapter;

import java.util.HashMap;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final SessionRepository sessionRepository;

    public AuthServiceImpl(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder,
                           SessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.sessionRepository = sessionRepository;
    }

    @Override
    public ResponseEntity<UserDto> login(String email, String password) throws Exception {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
//            return ResponseEntity.ok(this.signUp(email, password));
            return null;
        }

        User user = userOptional.get();
//        if (!user.getPassword().equals(password)) {
//            return null;
//        }
        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new Exception("Wrong Password");
        }

        String token = RandomStringUtils.randomAlphabetic(30);

        Session session = new Session();
        session.setSessionStatus(SessionStatus.ACTIVE);
        session.setToken(token);
        session.setUser(user);
        sessionRepository.save(session);

        UserDto userDto = UserDto.from(user);


        MultiValueMapAdapter<String, String> headers = new MultiValueMapAdapter<>(new HashMap<>());
        headers.add(HttpHeaders.SET_COOKIE, "auth-token: " + token);

        ResponseEntity<UserDto> response = new ResponseEntity<>(userDto, headers, HttpStatus.OK);
//        response.getHeaders().add(HttpHeaders.SET_COOKIE, token);

        return response;
    }

    @Override
    public ResponseEntity<Void> logout(String token, Long userId) {
        return null;
    }

    @Override
    public UserDto signUp(String email, String password) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password));

        User savedUser = userRepository.save(user);

        return UserDto.from(savedUser);
    }

    @Override
    public SessionStatus validateToken(String token, Long userId) {
        return null;
    }
}
