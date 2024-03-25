package com.ImmunTrackApp.ImmunTrackApp.controller;

import com.ImmunTrackApp.ImmunTrackApp.dto.*;
import com.ImmunTrackApp.ImmunTrackApp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/health_care")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/add_child")
    public ResponseEntity<Response> addChild(@RequestBody AddChildDto addChildDto){
        return userService.addChild(addChildDto);
    }

    @PostMapping("/add_vaccine")
    public ResponseEntity<Response> addVaccine(@RequestBody AddVaccine addVaccine){
        return userService.addVaccine(addVaccine);
    }

    @GetMapping("/all_children")
    public ResponseEntity<AllChildrenInfoResponse> getAllChildren(){
        return userService.getAllChildren();
    }

    @GetMapping("/child/{childId}")
    public ResponseEntity<OneChildInfoResponse> getChildById(@PathVariable Integer childId){
        return userService.getChildById(childId);
    }

    @GetMapping("/vaccine/{vaccineId}")
    public ResponseEntity<OneVaccineInfoResponse> getVaccineById(@PathVariable Integer vaccineId){
        return userService.getVaccineById(vaccineId);
    }

    @GetMapping("/children_count")
    public ResponseEntity<Numbers> childrenCount(){
        return userService.childrenCount();
    }

    @GetMapping("/all_vaccines")
    public ResponseEntity<VaccineInfoResponse> getAllVaccines(){
        return userService.getAllVaccines();
    }

    @GetMapping("/vaccine_count")
    public ResponseEntity<Numbers> vaccineCount(){
        return userService.vaccineCount();
    }

    @DeleteMapping("/delete_child/{childId}")
    public ResponseEntity<Response> deleteOneChild(@PathVariable Integer childId){
        return userService.deleteOneChild(childId);
    }

    @DeleteMapping("/delete_vaccine/{vaccineId}")
    public ResponseEntity<Response> deleteOneVaccine(@PathVariable Integer vaccineId){
        return userService.deleteOneVaccine(vaccineId);
    }

    @GetMapping("/report_count")
    public ResponseEntity<Numbers> reportCount(){
        return userService.reportCount();
    }

    @PostMapping("/add_report")
    public ResponseEntity<Response> addReport(@RequestBody AddReport addReport, @RequestHeader(name = "Authorization") String token){
        return userService.addReport(addReport);
    }

    @PutMapping("/update_child_info/{child_id}")
    public ResponseEntity<Response> updateChildInfo(@PathVariable Integer child_id, @RequestBody UpdateChildInfo updateChildInfo){
        return userService.updateChildInfo(child_id, updateChildInfo);
    }

    @PutMapping("/update_vaccine_info/{vaccine_id}")
    public ResponseEntity<Response> updateVaccineInfo(@PathVariable Integer vaccine_id, @RequestBody UpdateVaccineInfo updateVaccineInfo){
        return userService.updateVaccineInfo(vaccine_id, updateVaccineInfo);
    }
}
