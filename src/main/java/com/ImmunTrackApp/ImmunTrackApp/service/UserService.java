package com.ImmunTrackApp.ImmunTrackApp.service;

import com.ImmunTrackApp.ImmunTrackApp.dto.*;
import com.ImmunTrackApp.ImmunTrackApp.model.*;
import com.ImmunTrackApp.ImmunTrackApp.repository.ChildRepo;
import com.ImmunTrackApp.ImmunTrackApp.repository.TokenRepo;
import com.ImmunTrackApp.ImmunTrackApp.repository.UserRepo;
import com.ImmunTrackApp.ImmunTrackApp.repository.VaccineRepo;
import com.ImmunTrackApp.ImmunTrackApp.security.JwtService;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.hibernate.engine.spi.Resolution;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestPart;

import java.security.Principal;
import java.time.LocalDate;
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

    //REGISTER AS A USER
    public ResponseEntity<Response> userRegister(UserRegister userRegisterDto){
        Response response = new Response();
        if(userRepo.existsByEmail(userRegisterDto.getEmail())){
            response.setSuccess(false);
            response.setMessage("User already exists");
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
        response.setMessage("Health worker Created Successfully");
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
        Response response = new Response();
        String userEmail = principal.getName();
        UserEntity user = (UserEntity) userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User Email " + userEmail + " nt found"));

        if(childRepo.existsByLastname(lastname)){
            response.setSuccess(false);
            response.setMessage("Child already exists");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        ChildEntity child = new ChildEntity();
        child.setFirstname(firstname);
        child.setLastname(lastname);
        child.setSex(sex);
        child.setMother_name(mother_name);
        child.setMother_id_no(mother_id_no);
        child.setMother_phone_no(mother_phone_no);
        child.setLocation(location);
        child.setDob(dob);

        childRepo.save(child);
        response.setSuccess(true);
        response.setMessage("Child added successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //ADD VACCINE
    public ResponseEntity<Response> addVaccine(
            Principal principal,
            @RequestPart("vaccineName") String vaccineName,
            @RequestPart("manufacturer") String manufacturer,
            @RequestPart("batchNo") String batchNo,
            @RequestPart("expiryDate") String expiryDate,
            @RequestPart("quantity") String quantity
    ){
        Response response = new Response();
        String userEmail = principal.getName();
        UserEntity user = (UserEntity) userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User Email " + userEmail + " nt found"));

        if(vaccineRepo.existsByVaccineName(vaccineName)){
            response.setSuccess(false);
            response.setMessage("vaccine already exists");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        VaccineEntity vaccine = new VaccineEntity();
        vaccine.setVaccineName(vaccineName);
        vaccine.setManufacturer(manufacturer);
        vaccine.setBatchNo(batchNo);
        vaccine.setExpiryDate(expiryDate);
        vaccine.setQuantity(quantity);

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
