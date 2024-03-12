package com.ImmunTrackApp.ImmunTrackApp.controller;

import com.ImmunTrackApp.ImmunTrackApp.dto.*;
import com.ImmunTrackApp.ImmunTrackApp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/health_care")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/add_child")
    public ResponseEntity<Response> addChild(
            Principal principal,
            @RequestPart("firstname") String firstname,
            @RequestPart("lastname") String lastname,
            @RequestPart("sex") String sex,
            @RequestPart("mother_name") String mother_name,
            @RequestPart("mother_id_no") String mother_id_no,
            @RequestPart("mother_phone_no") String mother_phone_no,
            @RequestPart("location") String location,
            @RequestPart("dob") String dob
    ){
        return userService.addChild(principal, firstname, lastname, sex, mother_name, mother_id_no, mother_phone_no, location, dob);
    }

    @PostMapping("/add_vaccine")
    public ResponseEntity<Response> addVaccine(
            Principal principal,
            @RequestPart("vaccineName") String vaccineName,
            @RequestPart("manufacturer") String manufacturer,
            @RequestPart("batchNo") String batchNo,
            @RequestPart("expiryDate") String expiryDate,
            @RequestPart("quantity") String quantity
    ){
        return userService.addVaccine(principal, vaccineName, manufacturer, batchNo, expiryDate, quantity);
    }

    @GetMapping("/all_children")
    public ResponseEntity<ChildInfoResponse> getAllChildren(){
        return userService.getAllChildren();
    }

    @GetMapping("/all_vaccines")
    public ResponseEntity<VaccineInfoResponse> getAllVaccines(){
        return userService.getAllVaccines();
    }

    @PutMapping("/update_child_info/{child_id}")
    public ResponseEntity<Response> updateChildInfo(@RequestHeader(name = "Authorization") String token, @PathVariable Integer child_id, @RequestBody UpdateChildInfo updateChildInfo){
        return userService.updateChildInfo(child_id, updateChildInfo);
    }

    @PutMapping("/update_vaccine_info/{vaccine_id}")
    public ResponseEntity<Response> updateVaccineInfo(@RequestHeader(name = "Authorization") String token, @PathVariable Integer vaccine_id, @RequestBody UpdateVaccineInfo updateVaccineInfo){
        return userService.updateVaccineInfo(vaccine_id, updateVaccineInfo);
    }
}
