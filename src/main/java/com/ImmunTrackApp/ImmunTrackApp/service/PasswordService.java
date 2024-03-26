package com.ImmunTrackApp.ImmunTrackApp.service;

import com.ImmunTrackApp.ImmunTrackApp.dto.ChangePasswordRequest;
import com.ImmunTrackApp.ImmunTrackApp.dto.Response;
import com.ImmunTrackApp.ImmunTrackApp.model.UserEntity;
import com.ImmunTrackApp.ImmunTrackApp.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class PasswordService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepo userRepo;

    public ResponseEntity<Response> changePassword(ChangePasswordRequest request){
        Response response = new Response();
        // Get the authenticated user's email from the SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        // Find the user by email
        UserEntity user = (UserEntity) userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User with email " + userEmail + " not found"));

        // check if the current password is correct
        if(!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())){
            response.setSuccess(false);
            response.setMessage("wrong password");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        // check if the two passwords are the same
        if(!request.getNewPassword().equals(request.getConfirmPassword())){
            response.setSuccess(false);
            response.setMessage("passwords do not match");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepo.save(user);
        response.setSuccess(true);
        response.setMessage("password changed");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
