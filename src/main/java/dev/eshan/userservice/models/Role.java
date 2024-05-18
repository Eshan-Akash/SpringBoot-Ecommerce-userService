package dev.eshan.userservice.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@JsonDeserialize(as = Role.class)
@AllArgsConstructor
@NoArgsConstructor
public class Role extends BaseModel {
    private String role;
}
