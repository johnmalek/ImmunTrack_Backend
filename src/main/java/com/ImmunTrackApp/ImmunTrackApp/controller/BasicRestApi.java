package com.ImmunTrackApp.ImmunTrackApp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public")
public class BasicRestApi {
    @GetMapping("/")
    public ResponseEntity<String> home(){
        return new ResponseEntity<String>("Hello from this endpoint", HttpStatus.OK);
    }
}
