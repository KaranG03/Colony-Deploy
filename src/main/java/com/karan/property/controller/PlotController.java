package com.karan.property.controller;


import com.karan.property.entity.Plot;
import com.karan.property.entity.User;
import com.karan.property.repository.UserRepo;
import com.karan.property.service.CustomerService;
import com.karan.property.service.PlotService;
import com.karan.property.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/plot")
public class PlotController {


    @Autowired
    private PlotService plotService;
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private CustomerService customerService;




    @PostMapping("/{colonyName}")
    public ResponseEntity<?> addPlot(@RequestBody Plot plot, @PathVariable String colonyName){

            // Fetch logged-in user's details
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
                String username = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();

                // Call your service method with the logged-in user's name
                    System.out.println(username);
                    plotService.addNewPlot(plot,colonyName, username);



                return new ResponseEntity<>(plot,HttpStatus.OK);
            }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // If user is not authenticated

    }


    @PutMapping("/edit-plot/{id}")
    public ResponseEntity<?> editPlotDetail(@RequestBody Plot newPlot, @PathVariable ObjectId id){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            String username = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();


            Optional<Plot> plotById = plotService.getPlotById(id);
            plotById.get().setPlotNo(newPlot.getPlotNo());
            plotById.get().setNote(newPlot.getNote());
            plotById.get().setPrice(newPlot.getPrice());
            plotById.get().setSold(newPlot.isSold());
            plotById.get().setRegTo(newPlot.getRegTo());
            plotById.get().setCustAadhar(newPlot.getCustAadhar());
            plotById.get().setCustPhoneNum(newPlot.getCustPhoneNum());
            plotById.get().setCustAdd(newPlot.getCustAdd());
            if (newPlot.getOwner() != null && newPlot.getOwner().length() != 0) {
                plotById.get().setOwner(newPlot.getOwner());
                customerService.addPlot(plotById.get(), newPlot.getOwner(),username);
            }
            plotService.save(plotById.get());
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    }

    @GetMapping("/{id}")

    public ResponseEntity<?> getPlot(@PathVariable ObjectId id){
        try{
            return new ResponseEntity<>(plotService.getPlotById(id),HttpStatus.OK);
        }
        catch (Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }


}
