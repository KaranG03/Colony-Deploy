package com.karan.property.controller;


import com.karan.property.entity.Colony;
import com.karan.property.entity.Customer;
import com.karan.property.entity.User;
import com.karan.property.repository.UserRepo;
import com.karan.property.service.CustomerService;
import com.karan.property.service.EmailService;
import com.karan.property.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    //adduser = sign-up

    //load all colonies of this user = login

    @Autowired
    UserService userService;

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @Autowired
    UserRepo userRepo;

    @Autowired
    private CustomerService customerService;

    //add colony in this user
    @PostMapping
    public ResponseEntity<?> addNewColony(@RequestBody Colony colony) {
        try {
            // Fetch logged-in user's details
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
                String username = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();

                // Call your service with the logged-in user's identifier (username, email, etc.)
                userService.addNewCol(colony, username);

                return new ResponseEntity<>(HttpStatus.OK);
            }

            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // If user is not authenticated
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllColony() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            String username = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();

            User user = userRepo.findByusername(username);
            return new ResponseEntity<>(user.getColonies(),HttpStatus.OK);

        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/all-cust")
    public ResponseEntity<?> seeAllCustomers() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            String username = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();

            User user = userRepo.findByusername(username);
            List<Customer> cc = customerService.getAllCustomers(user.getUsername());
            return new ResponseEntity<>(cc, HttpStatus.OK);
        }
        return new ResponseEntity<>("Not Fount", HttpStatus.NOT_FOUND);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> reset(@RequestParam String username, @RequestParam String newP){
        User user = userRepo.findByusername(username);

        System.out.println(newP);
        System.out.println(user);

        if(user!=null){
            user.setPassword(passwordEncoder.encode(newP));
            userRepo.save(user);
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>("invalid reques", HttpStatus.BAD_REQUEST);
    }


    @GetMapping("/prime-status")
    public ResponseEntity<?> getPrimeStatus() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            User user = userRepo.findByusername(username);
            return ResponseEntity.ok(Collections.singletonMap("primeStatus", user.getPrimeStatus()));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
    }



}
