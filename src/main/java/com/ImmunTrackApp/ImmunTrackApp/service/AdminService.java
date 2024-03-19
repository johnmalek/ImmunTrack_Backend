package com.ImmunTrackApp.ImmunTrackApp.service;

import com.ImmunTrackApp.ImmunTrackApp.dto.Response;
import com.ImmunTrackApp.ImmunTrackApp.dto.UserLogin;
import com.ImmunTrackApp.ImmunTrackApp.dto.UserLoginResponse;
import com.ImmunTrackApp.ImmunTrackApp.dto.UserRegister;
import com.ImmunTrackApp.ImmunTrackApp.model.Role;
import com.ImmunTrackApp.ImmunTrackApp.model.Token;
import com.ImmunTrackApp.ImmunTrackApp.model.TokenType;
import com.ImmunTrackApp.ImmunTrackApp.model.UserEntity;
import com.ImmunTrackApp.ImmunTrackApp.repository.TokenRepo;
import com.ImmunTrackApp.ImmunTrackApp.repository.UserRepo;
import com.ImmunTrackApp.ImmunTrackApp.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TokenRepo tokenRepo;

    //REGISTER AS AN ADMIN
    public ResponseEntity<Response> adminRegister(UserRegister userRegisterDto){
        Response response = new Response();
        if(userRepo.existsByEmail(userRegisterDto.getEmail())){
            response.setSuccess(false);
            response.setMessage("email already exists, login instead");
            return new ResponseEntity<Response>(response, HttpStatus.BAD_REQUEST);
        }
        UserEntity admin = new UserEntity();
        admin.setFirstname(userRegisterDto.getFirstname());
        admin.setLastname(userRegisterDto.getLastname());
        admin.setEmail(userRegisterDto.getEmail());
        admin.setPassword(passwordEncoder.encode(userRegisterDto.getPassword()));
        admin.setRole(Role.ADMIN);
        userRepo.save(admin);
        response.setSuccess(true);
        response.setMessage("admin created successfully");
        return new ResponseEntity<Response>(response, HttpStatus.OK);
    }

    //LOGIN
    public ResponseEntity<UserLoginResponse> adminLogin(UserLogin userLoginDto){
        UserLoginResponse response = new UserLoginResponse();
        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userLoginDto.getEmail(),
                            userLoginDto.getPassword()
                    )
            );
            UserEntity user = (UserEntity) userRepo.findByEmail(userLoginDto.getEmail()).orElseThrow();
            String jwtToken = jwtService.generateToken(user);
            String encodedPassword = ((UserDetails) user).getPassword();
            String passedPassword = userLoginDto.getPassword();
            boolean passwordsMatch = passwordEncoder.matches(passedPassword, encodedPassword);
            if(passwordsMatch){
                response.setSuccess(true);
                response.setMessage("login successful");
                response.setToken(jwtToken);
                response.setUser(user.getId(), user.getFirstname(), user.getLastname(), user.getEmail());
                revokeAllUserTokens(user);
                saveUserToken(user, jwtToken);
                return new ResponseEntity<UserLoginResponse>(response, HttpStatus.OK);
            }
        } catch (AuthenticationException e){
            response.setSuccess(false);
            response.setMessage("incorrect email or password");
            return new ResponseEntity<UserLoginResponse>(response, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

    }

    // User methods to save and revoke all tokens
    private void saveUserToken(UserEntity user, String jwtToken){
        var token = Token.builder()
                .user(user)
                .expired(false)
                .revoked(false)
                .tokenType(TokenType.BEARER)
                .token(jwtToken)
                .build();
        tokenRepo.save(token);
    }

    private void revokeAllUserTokens(UserEntity user) {
        var validUserTokens = tokenRepo.findAllValidTokensByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepo.saveAll(validUserTokens);
    }
}
