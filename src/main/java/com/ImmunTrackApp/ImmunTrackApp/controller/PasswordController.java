package com.ImmunTrackApp.ImmunTrackApp.controller;

import com.ImmunTrackApp.ImmunTrackApp.dto.ChangePasswordRequest;
import com.ImmunTrackApp.ImmunTrackApp.dto.Response;
import com.ImmunTrackApp.ImmunTrackApp.service.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class PasswordController {
    private final PasswordService passwordService;

    @PatchMapping
    public ResponseEntity<Response> changePassword(@RequestHeader(name = "Authorization") String token, @RequestBody ChangePasswordRequest request){
        return passwordService.changePassword(request);
    }
}
