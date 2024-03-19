package com.ImmunTrackApp.ImmunTrackApp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
class userDetails{
    private String firstname;
    private String lastname;
    private String email;
    private Integer id;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class UserLoginResponse {
    private boolean success;
    private String message;
    private String token;
    private userDetails user;

    public void setUser(Integer id, String firstname, String lastname, String email) {
        this.user = new userDetails(firstname, lastname, email, id);
    }
}
