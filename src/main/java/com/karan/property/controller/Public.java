package com.karan.property.controller;


import com.karan.property.Utils.JwtUtil;
import com.karan.property.entity.Colony;
import com.karan.property.entity.User;
import com.karan.property.repository.UserRepo;
import com.karan.property.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/public")
@Slf4j
public class Public {

    @Autowired
    private UserService userService;



    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ColonyService colonyService;

    @Autowired
    private UserDetailServiceImpl userDetailService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;



    @GetMapping("/health")
    public String health(){
        return "ok";
    }


    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody User user){
        userService.saveNewUser(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }



    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody User user) {
        try {
            // Authenticate the user
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

            // Load user details
            UserDetails userDetails = userDetailService.loadUserByUsername(user.getUsername());

            // Generate JWT token
            String jwt = jwtUtil.generateToken(userDetails.getUsername());

            // Fetch all colonies (you can customize this logic if you want to return colonies specific to the user)
//            List<Colony> colonies = colonyService.getAllColoniesOfUser(user); // Replace with your logic to get colonies
//
//            // Create a response object containing both JWT and colonies
//            Map<String, Object> response = new HashMap<>();
//            response.put("token", jwt);
//            response.put("colonies", colonies);

            // Return response with status OK
            return new ResponseEntity<>(jwt, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception occurred while createAuthenticationToken", e);
            return new ResponseEntity<>("Incorrect username or password", HttpStatus.BAD_REQUEST);
        }
    }


    @Autowired
    EmailService emailService;

    @Autowired
    OTPService otpService;




    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgot(@RequestBody Map<String, String> request) {
        String user = request.get("username"); // Extract the "username" field
        System.out.println(user); // This will now print "krn"

        User user1 = userRepo.findByusername(user);
        if (user1 != null && user1.getMail().length() != 0) {
            String otp = otpService.generateOTP(user);

            // Send OTP to user's email
            emailService.sendMail(user1.getMail(), "Forgot Password OTP", "Your OTP for resetting the password is: " + otp);
            return new ResponseEntity<>("Password reset success", HttpStatus.OK);
        }

        return new ResponseEntity<>("Incorrect details", HttpStatus.NOT_FOUND);
    }



}


