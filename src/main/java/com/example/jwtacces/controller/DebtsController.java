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
    Obtiene el balance actual de deudas que tienes y que debes para mostrar en la pantalla home
     */
    @GetMapping(path = "/getBalance")
    public ResponseEntity<?> getBalance(){
        return debtsService.getBalance();
    }

    /*
     Obtiene las deudas entre un usuario y su deudor incluyendo las que ya han sido pagadas, para ver en historial
     */
    @GetMapping(path = "/getDebtsByCreditorAndDebtor")
    public ResponseEntity<?> getDebtByCreditorAndDebtor(@RequestParam String debtorUsername){
        return debtsService.getDebtByCreditorAndDebtor(debtorUsername);
    }

    /*
    Obtiene las deudas actuales que un usuario tiene con sus deudores y con sus acreedores
     */
    @GetMapping(path = "/getCurrentDebts")
    public ResponseEntity<?> getDebtsByCreditorNotPaid(){
        return debtsService.getCurrentDebts();
    }

    /*
    Deja la deuda saldada, en el caso de que seas el acreedor
     */
    @Transactional
    @GetMapping(path = "/payOffDebt")
    public ResponseEntity<?> payOffDebt(@RequestParam Long debtId){
        return debtsService.payOffDebt(debtId);
    }



    /*
    Guarda una nueva deuda, teniendo en cuenta las deudas anteriores que debias a tu nuevo deudor, para calcular la diferencia
    y de esta forma podre saldar cuentas de forma automaticas
     */
    @PostMapping(path = "/saveDebt")
    public ResponseEntity<?> saveDebt(@Valid @RequestBody CreateDebtDTO createDebtDTO){
        return debtsService.saveDebt(createDebtDTO);
    }
}
