package com.ImmunTrackApp.ImmunTrackApp.controller;

import com.ImmunTrackApp.ImmunTrackApp.dto.Response;
import com.ImmunTrackApp.ImmunTrackApp.dto.UserLogin;
import com.ImmunTrackApp.ImmunTrackApp.dto.UserLoginResponse;
import com.ImmunTrackApp.ImmunTrackApp.dto.UserRegister;
import com.ImmunTrackApp.ImmunTrackApp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/user_register")
    public ResponseEntity<Response> register(@RequestBody UserRegister userRegisterDto){
        return userService.userRegister(userRegisterDto);
    }

    @PostMapping("/user_login")
    public ResponseEntity<UserLoginResponse> login(@RequestBody UserLogin userLoginDto){
        return userService.userLogin(userLoginDto);
    }
}
