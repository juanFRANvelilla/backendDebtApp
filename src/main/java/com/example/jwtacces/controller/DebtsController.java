package com.example.jwtacces.controller;

import com.example.jwtacces.models.debt.CreateDebtDTO;
import com.example.jwtacces.service.DebtsService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/debt")
public class DebtsController {
    @Autowired
    private DebtsService debtsService;

    /*
     Obtiene las deudas entre un usuario y su deudor incluyendo las que ya han sido pagadas, para ver en historial
     */
    @GetMapping(path = "/getDebtsByCreditorAndDebtor")
    public ResponseEntity<?> getDebtByCreditorAndDebtor(@RequestParam String debtorUsername){
        return debtsService.getDebtByCreditorAndDebtor(debtorUsername);
    }

    /*
    Obtiene las deudas que un usuario tiene con sus deudores en general, y estan pendientes de pagar
    se necesita devolver un objeto con informacion sobre quien es
     */
    @GetMapping(path = "/getDebtsByCreditorNotPaid")
    public ResponseEntity<?> getDebtsByCreditorNotPaid(){
        return debtsService.getDebtsByCreditorNotPaid();
    }

    /*
    Deja la deuda saldada, en el caso de que seas el acreedor
     */
    @Transactional
    @GetMapping(path = "/payOffDebt")
    public ResponseEntity<?> payOffDebt(@RequestParam Long debtId){
        return debtsService.payOffDebt(debtId);
    }


    @PostMapping(path = "/saveDebt")
    public ResponseEntity<?> saveDebt(@Valid @RequestBody CreateDebtDTO createDebtDTO){
        return debtsService.saveDebt(createDebtDTO);
    }
}
