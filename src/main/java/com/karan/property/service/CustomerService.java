package com.karan.property.service;


import com.karan.property.entity.Customer;
import com.karan.property.entity.Plot;
import com.karan.property.entity.User;
import com.karan.property.repository.CustomerRepo;
import com.karan.property.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {


    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private UserRepo userRepo;
    public void addNerCustomer(Customer customer){
        customerRepo.save(customer);
    }

    public Customer addPlot(Plot plot, String name,String username) {
        Customer toAdd = customerRepo.findBycustName(name);
        User user = userRepo.findByusername(username);
        if(toAdd==null){
            Customer customer = new Customer();
            customer.getCustPlots().add(plot);
            customer.setCustName(name);
            customer.setAddress(plot.getCustAdd());
            customer.setAadharNum(plot.getCustAadhar());
            customer.setPhNum(plot.getCustPhoneNum());
            customerRepo.save(customer);
            user.getCustomers().add(customer);
            userRepo.save(user);
            return customer;
        }
        else {
            toAdd.getCustPlots().add(plot);
            customerRepo.save(toAdd);
            return toAdd;
        }
    }

    public List<Customer> getAllCustomers(String userName){
        User user = userRepo.findByusername(userName);
        return user.getCustomers();
    }
}
