package dev.eshan.userservice.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class JwtObject {
    private String email;
    private Long userId;
    List<Role> roles = new ArrayList<>();
    private Date createdAt;
    private Date expiresAt;
    private String token;
}