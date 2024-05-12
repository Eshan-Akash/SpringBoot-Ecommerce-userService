package dev.eshan.userservice.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.Entity;
import lombok.Data;

@Data
@Entity
@JsonDeserialize(as = Role.class)
public class Role extends BaseModel {
    private String role;
}
