package com.ImmunTrackApp.ImmunTrackApp.dto;

import com.ImmunTrackApp.ImmunTrackApp.model.Role;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRegister {
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private Role role;
}
