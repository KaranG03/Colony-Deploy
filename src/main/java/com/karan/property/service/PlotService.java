package com.karan.property.service;

import com.karan.property.entity.Colony;
import com.karan.property.entity.Customer;
import com.karan.property.entity.Plot;
import com.karan.property.repository.PlotRepo;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PlotService {

    @Autowired
    private ColonyService colonyService;

    @Autowired
    private  PlotRepo plotRepo;

    @Autowired
    private CustomerService customerService;

    public void addNewPlot(Plot plot, String colonyName, String userName){
        plotRepo.save(plot);
        Optional<Colony> colony = Optional.ofNullable(colonyService.getByName(colonyName));
        colony.ifPresent(value -> value.getPlots().add(plot));
        if(plot.getOwner()!=null && plot.getOwner().length()!=0) {
            Customer cust = customerService.addPlot(plot, plot.getOwner(),userName);
            customerService.addNerCustomer(cust);
        }
        colonyService.saveColony(colony.get(),userName);
    }

    public Optional<Plot> getPlotById(ObjectId id){
        return plotRepo.findById(id);
    }

    public void save(Plot plot) {
        plotRepo.save(plot);
    }
}
