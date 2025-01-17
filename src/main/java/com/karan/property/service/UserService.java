package com.karan.property.service;


import com.karan.property.entity.Colony;
import com.karan.property.entity.Plot;
import com.karan.property.entity.User;
import com.karan.property.repository.ColonyRepo;
import com.karan.property.repository.PlotRepo;
import com.karan.property.repository.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Service
@Slf4j
public class UserService {
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PlotRepo plotRepo;

    @Autowired
    private ColonyRepo colonyRepo;

    public boolean saveNewUser(User user){
        try{

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepo.save(user);
            return true;
        }
        catch (Exception e){
            log.info("error occured for {}" ,user.getUsername());
            return false;
        }

    }

    public void save(User user){
        userRepo.save(user);
    }


    //much faster then loop, my methode:
//    public void addNewCol(Colony colony, String name) {
//        for(int i =1;i<=colony.getNumPlots();i++){
//            Plot plot = new Plot();
//            plot.setPlotNo(i);
//            plot.setColonyName(colony.getName());
//            plotRepo.save(plot);
//            colony.getPlots().add(plot);
//        }
//        colonyRepo.save(colony); // Save the colony to the colony repository
//        Optional<User> user = Optional.ofNullable(userRepo.findByusername(name)); // Find the user by ID
//        user.ifPresent(value -> {
//            value.getColonies().add(colony); // Add the colony to the user's colonies
//            int plots = colony.getNumPlots();
//
//            userRepo.save(value); // Save the updated user to the user repository
//        });
//    }
    @Transactional
    public void addNewCol(Colony colony, String username) {
        // Generate and set plots for the colony
        List<Plot> plots = IntStream.rangeClosed(1, colony.getNumPlots())
                .mapToObj(i -> {
                    Plot plot = new Plot();
                    plot.setPlotNo(i);
                    plot.setColonyName(colony.getName());
                    return plot;
                })
                .collect(Collectors.toList());

        // Save all plots in bulk to the repository
        plotRepo.saveAll(plots);
        colony.setPlots(plots);

        // Save the colony
        colonyRepo.save(colony);

        // Update the user's colonies
        User user = userRepo.findByusername(username);
        if (user != null) {
            user.getColonies().add(colony);
            userRepo.save(user);
        }
    }



}
