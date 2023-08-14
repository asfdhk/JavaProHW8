package com.example.bank;

import static com.example.bank.Main.*;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Web {

    @GetMapping("listOperations")
    public String listOperations(){
        return spring;
    }

    @GetMapping("totalBalance")
    public String ds(){
        return totalBalance();
    }

    @GetMapping("amountOperations")
    public int ollOperations(){
        return amountOperations();
    }
}
