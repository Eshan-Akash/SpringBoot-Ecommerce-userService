package dev.eshan.userservice.services;

import dev.eshan.userservice.dtos.*;
import dev.eshan.userservice.models.*;
import dev.eshan.userservice.repositories.SessionRepository;
import dev.eshan.userservice.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMapAdapter;

import javax.crypto.SecretKey;
import java.time.LocalDate;
import java.util.*;

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

//        String token = RandomStringUtils.randomAlphabetic(30);



        // create a test key for this example:
        SecretKey key = Jwts.SIG.HS256.key().build();

//        String claimsString = new GsonBuilder().create().toJson(JWTPayLoad.builder().email("eshan@gmail.com").role(List.of("mantor", "ta")).build());
//
//        byte[] content = claimsString.getBytes(StandardCharsets.UTF_8);
//
//        String jws = Jwts.builder().content(content, "text/plain").signWith(key).compact();

        Map<String, Object> jsonForJwt = new HashMap<>();
        jsonForJwt.put("email", user.getEmail());
        jsonForJwt.put("roles", user.getRoles());
        jsonForJwt.put("createdAt", new Date());
        jsonForJwt.put("expiryAt", new Date(LocalDate.now().plusDays(3).toEpochDay()));

        String jws = Jwts.builder()
                .claims(jsonForJwt)
                .signWith(key)
                .compact();

        Session session = new Session();
        session.setSessionStatus(SessionStatus.ACTIVE);
        session.setToken(jws);
        session.setUser(user);
        sessionRepository.save(session);

        UserDto userDto = UserDto.from(user);


        MultiValueMapAdapter<String, String> headers = new MultiValueMapAdapter<>(new HashMap<>());
        headers.add(HttpHeaders.SET_COOKIE, "auth-token: " + jws);

        ResponseEntity<UserDto> response = new ResponseEntity<>(userDto, headers, HttpStatus.OK);
//        response.getHeaders().add(HttpHeaders.SET_COOKIE, token);

        return response;
    }

//    @Data
//    @Builder
//    static class JWTPayLoad {
//        String email;
//        List<String> role;
//    }

    @Override
    public ResponseEntity<Void> logout(String token, Long userId) {
        Optional<Session> sessionOptional = sessionRepository.findByTokenAndUser_Id(token, userId);

        if (sessionOptional.isEmpty()) {
            return null;
        }

        Session session = sessionOptional.get();

        session.setSessionStatus(SessionStatus.ENDED);

        sessionRepository.save(session);

        return ResponseEntity.ok().build();
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
        Optional<Session> sessionOptional = sessionRepository.findByTokenAndUser_Id(token, userId);
        if (sessionOptional.isEmpty()) {
            return SessionStatus.ENDED;
//            return null;
        }
        Session session = sessionOptional.get();
        if (!session.getSessionStatus().equals(SessionStatus.ACTIVE)) {
            return SessionStatus.ENDED;
        }

        Jws<Claims> claimsJws = Jwts.parser().build().parseSignedClaims(token);
        String email = claimsJws.getPayload().get("email", String.class);
        List<String> roles = (List<String>) claimsJws.getPayload().get("roles");
        Date createdAt = (Date) claimsJws.getPayload().get("createdAt");

        if (createdAt.before(new Date())) {
            return SessionStatus.ENDED;
        }

        return SessionStatus.ACTIVE;
    }

    public JwtObject getJwtDetails(String token, Long userId) {
        SessionStatus sessionStatus = validateToken(token, userId);
        if (sessionStatus.equals(SessionStatus.ENDED)) {
            return null;
        }
        Jws<Claims> claimsJws = Jwts.parser().build().parseSignedClaims(token);
        String email = claimsJws.getPayload().get("email", String.class);
        List<String> roles = (List<String>) claimsJws.getPayload().get("roles");
        Date createdAt = (Date) claimsJws.getPayload().get("createdAt");
        Date expiresAt = (Date) claimsJws.getPayload().get("expiresAt");
        JwtObject jwtObject = new JwtObject();
        jwtObject.setEmail(email);
        jwtObject.setUserId(userId);
        jwtObject.setRoles(roles.stream().map(Role::new).toList());
        jwtObject.setCreatedAt(createdAt);
        jwtObject.setExpiresAt(expiresAt);
        jwtObject.setToken(token);
        return jwtObject;
    }
}
