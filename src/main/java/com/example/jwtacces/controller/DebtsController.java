package com.example.jwtacces.controller;

import com.example.jwtacces.DTO.UsernameDTO;
import com.example.jwtacces.models.debt.CreateDebtDTO;
import com.example.jwtacces.service.DebtsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/debt")
public class DebtsController {
    @Autowired
    private DebtsService debtsService;

    @PostMapping(path = "/getDebtsWithDebtor")
    public ResponseEntity<?> getDebtsWithDebtor(@Valid @RequestBody UsernameDTO usernameDTO){
        return debtsService.getDebtsWithDebtor(usernameDTO);
    }

    @PostMapping(path = "/saveDebt")
    public ResponseEntity<?> saveDebt(@Valid @RequestBody CreateDebtDTO createDebtDTO){
        return debtsService.saveDebt(createDebtDTO);
    }
}
