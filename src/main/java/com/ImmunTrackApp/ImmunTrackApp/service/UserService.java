package com.ImmunTrackApp.ImmunTrackApp.service;

import com.ImmunTrackApp.ImmunTrackApp.dto.*;
import com.ImmunTrackApp.ImmunTrackApp.model.*;
import com.ImmunTrackApp.ImmunTrackApp.repository.*;
import com.ImmunTrackApp.ImmunTrackApp.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TokenRepo tokenRepo;
    private final ChildRepo childRepo;
    private final VaccineRepo vaccineRepo;
    private final ReportsRepo reportsRepo;

    //REGISTER AS A USER
    public ResponseEntity<Response> userRegister(UserRegister userRegisterDto){
        Response response = new Response();
        if(userRepo.existsByEmail(userRegisterDto.getEmail())){
            response.setSuccess(false);
            response.setMessage("User already exists, login instead");
            return new ResponseEntity<Response>(response, HttpStatus.BAD_REQUEST);
        }
        UserEntity healthWorker = new UserEntity();
        healthWorker.setFirstname(userRegisterDto.getFirstname());
        healthWorker.setLastname(userRegisterDto.getLastname());
        healthWorker.setEmail(userRegisterDto.getEmail());
        healthWorker.setPassword(passwordEncoder.encode(userRegisterDto.getPassword()));
        healthWorker.setRole(Role.HEALTHCARE);
        userRepo.save(healthWorker);
        response.setSuccess(true);
        response.setMessage("User Created Successfully");
        return new ResponseEntity<Response>(response, HttpStatus.OK);
    }

    //LOGIN
    public ResponseEntity<UserLoginResponse> userLogin(UserLogin userLoginDto){
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

    //ADD CHILD
    public ResponseEntity<Response> addChild(AddChildDto addChildDto){
        Response response = new Response();

        if (childRepo.existsByLastname(addChildDto.getLastname())){
            response.setSuccess(false);
            response.setMessage("Child already exists");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        ChildEntity child = new ChildEntity();
        child.setFirstname(addChildDto.getFirstname());
        child.setLastname(addChildDto.getLastname());
        child.setSex(addChildDto.getSex());
        child.setMother_name(addChildDto.getMother_name());
        child.setMother_id_no(addChildDto.getMother_id_no());
        child.setMother_phone_no(addChildDto.getMother_phone_no());
        child.setLocation(addChildDto.getLocation());
        child.setDob(addChildDto.getDob());

        childRepo.save(child);

        response.setSuccess(true);
        response.setMessage("Child added successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //ADD VACCINE
    public ResponseEntity<Response> addVaccine(AddVaccine addVaccine){
        Response response = new Response();

        if(vaccineRepo.existsByVaccineName(addVaccine.getVaccineName())){
            response.setSuccess(false);
            response.setMessage("vaccine already exists");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        VaccineEntity vaccine = new VaccineEntity();
        vaccine.setVaccineName(addVaccine.getVaccineName());
        vaccine.setManufacturer(addVaccine.getManufacturer());
        vaccine.setBatchNo(addVaccine.getBatchNo());
        vaccine.setExpiryDate(addVaccine.getExpiryDate());
        vaccine.setQuantity(addVaccine.getQuantity());

        vaccineRepo.save(vaccine);

        response.setSuccess(true);
        response.setMessage("vaccine added successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //GET ALL CHILDREN
    public ResponseEntity<ChildInfoResponse> getAllChildren(){
        ArrayList<ChildEntity> children = new ArrayList<>(childRepo.findAll());
        ChildInfoResponse childInfoResponse = new ChildInfoResponse();
        ArrayList<ChildDetails> childDetails = new ArrayList<>();
        if(!children.isEmpty()){
            childInfoResponse.setSuccess(true);
            childInfoResponse.setMessage("All Children");
            ChildDetails childDetail;
            for(ChildEntity child: children){
                childDetail = new ChildDetails();
                childDetail.setId(child.getId());
                childDetail.setFirstname(child.getFirstname());
                childDetail.setLastname(child.getLastname());
                childDetail.setSex(child.getSex());
                childDetail.setMother_name(child.getMother_name());
                childDetail.setMother_id_no(child.getMother_id_no());
                childDetail.setMother_phone_no(child.getMother_phone_no());
                childDetail.setLocation(child.getLocation());
                childDetail.setDob(child.getDob());
                childDetails.add(childDetail);
            }
            childInfoResponse.setChildren(childDetails);
            return ResponseEntity.ok().body(childInfoResponse);
        }
        childInfoResponse.setSuccess(false);
        childInfoResponse.setMessage("No children found");
        return ResponseEntity.badRequest().body(childInfoResponse);
    }

    // RETURN NUMBER OF CHILDREN
    public ResponseEntity<Numbers> childrenCount(){
        Numbers response = new Numbers();
        if(childRepo.findAll().isEmpty()){
            response.setSuccess(String.valueOf(false));
            response.setMessage("No children found");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        long children_count = childRepo.count();
        response.setSuccess(String.valueOf(true));
        response.setMessage("Children found");
        response.setNum(children_count);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // RETURN NUMBER OF VACCINES
    public ResponseEntity<Numbers> vaccineCount() {
        Numbers response = new Numbers();
        if(vaccineRepo.findAll().isEmpty()){
            response.setSuccess(String.valueOf(false));
            response.setMessage("No vaccines found");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        long vaccine_count = vaccineRepo.count();
        response.setSuccess(String.valueOf(true));
        response.setMessage("Vaccines found");
        response.setNum(vaccine_count);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //GET ALL VACCINES
    public ResponseEntity<VaccineInfoResponse> getAllVaccines(){
        ArrayList<VaccineEntity> vaccines = new ArrayList<>(vaccineRepo.findAll());
        VaccineInfoResponse vaccineInfoResponse = new VaccineInfoResponse();
        ArrayList<VaccineDetails> vaccineDetails = new ArrayList<>();
        if(!vaccines.isEmpty()){
            vaccineInfoResponse.setSuccess(true);
            vaccineInfoResponse.setMessage("All Vaccines");
            VaccineDetails vaccineDetail;
            for(VaccineEntity vaccine: vaccines){
                vaccineDetail = new VaccineDetails();
                vaccineDetail.setId(vaccine.getId());
                vaccineDetail.setVaccineName(vaccine.getVaccineName());
                vaccineDetail.setManufacturer(vaccine.getManufacturer());
                vaccineDetail.setBatchNo(vaccine.getBatchNo());
                vaccineDetail.setExpiryDate(vaccine.getExpiryDate());
                vaccineDetail.setQuantity(vaccine.getQuantity());
                vaccineDetails.add(vaccineDetail);
            }
            vaccineInfoResponse.setVaccines(vaccineDetails);
            return ResponseEntity.ok().body(vaccineInfoResponse);
        }
        vaccineInfoResponse.setSuccess(false);
        vaccineInfoResponse.setMessage("No vaccines found");
        return ResponseEntity.badRequest().body(vaccineInfoResponse);
    }

    //UPDATE CHILD INFO
    public ResponseEntity<Response> updateChildInfo(Integer childId, UpdateChildInfo updateChildInfo){
        Response response = new Response();
        Optional<ChildEntity> child = childRepo.findById(childId);
        if(child.isEmpty()){
            response.setSuccess(false);
            response.setMessage("Child not found");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        ChildEntity child1 = child.get();
        child1.setFirstname(updateChildInfo.getFirstname());
        child1.setLastname(updateChildInfo.getLastname());
        child1.setSex(updateChildInfo.getSex());
        child1.setMother_name(updateChildInfo.getMother_name());
        child1.setMother_id_no(updateChildInfo.getMother_id_no());
        child1.setMother_phone_no(updateChildInfo.getMother_phone_no());
        child1.setLocation(updateChildInfo.getLocation());
        child1.setDob(updateChildInfo.getDob());

        childRepo.save(child1);

        response.setSuccess(true);
        response.setMessage("Child details updated successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //UPDATE VACCINE INFO
    public ResponseEntity<Response> updateVaccineInfo(Integer vaccineId, UpdateVaccineInfo updateVaccineInfo){
        Response response = new Response();
        Optional<VaccineEntity> vaccine = vaccineRepo.findById(vaccineId);
        if(vaccine.isEmpty()){
            response.setSuccess(false);
            response.setMessage("Vaccine not found");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        VaccineEntity vaccine1 = vaccine.get();
        vaccine1.setVaccineName(updateVaccineInfo.getVaccineName());
        vaccine1.setManufacturer(updateVaccineInfo.getManufacturer());
        vaccine1.setBatchNo(updateVaccineInfo.getBatchNo());
        vaccine1.setExpiryDate(updateVaccineInfo.getExpiryDate());
        vaccine1.setQuantity(updateVaccineInfo.getQuantity());

        vaccineRepo.save(vaccine1);

        response.setSuccess(true);
        response.setMessage("Vaccine details updated successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // ADD REPORT
    public ResponseEntity<Response> addReport(@RequestBody AddReport addReport) {
        Response response = new Response();

        try{
            // Get the authenticated user's email from the SecurityContextHolder
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();

            // Find the user by email
            UserEntity user = (UserEntity) userRepo.findByEmail(userEmail)
                    .orElseThrow(() -> new UsernameNotFoundException("User with email " + userEmail + " not found"));

            // Create a new report
            Reports report = new Reports();
            report.setChild_name(addReport.getChild_name());
            report.setVaccine_administered(addReport.getVaccine_administered());
            report.setDate_administered(addReport.getDate_administered());
            report.setNext_dose_date(addReport.getNext_dose_date());
            report.setUser(user);
            report.setCompiled_by(user.getLastname());
            report.setRemarks(addReport.getRemarks());

            // Save the report
            reportsRepo.save(report);

            response.setSuccess(true);
            response.setMessage("Report added successfully");

            // Return a ResponseEntity with HTTP status OK and the response body
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch(UsernameNotFoundException e){
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

    // RETURN NUMBER OF Reports
    public ResponseEntity<Numbers> reportCount () {
        Numbers response = new Numbers();
        if (reportsRepo.findAll().isEmpty()) {
            response.setSuccess(String.valueOf(false));
            response.setMessage("No reports found");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        long report_count = reportsRepo.count();
        response.setSuccess(String.valueOf(true));
        response.setMessage("Reports found");
        response.setNum(report_count);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // User methods to save and revoke all tokens
    private void saveUserToken (UserEntity user, String jwtToken){
        var token = Token.builder()
                .user(user)
                .expired(false)
                .revoked(false)
                .tokenType(TokenType.BEARER)
                .token(jwtToken)
                .build();
        tokenRepo.save(token);
    }

    private void revokeAllUserTokens (UserEntity user){
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
