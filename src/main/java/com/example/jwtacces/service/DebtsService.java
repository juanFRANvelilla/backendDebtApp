package com.example.jwtacces.service;

import com.example.jwtacces.models.UserEntity;
import com.example.jwtacces.models.debt.CreateDebtDTO;
import com.example.jwtacces.models.debt.Debt;
import com.example.jwtacces.repository.debt.DebtRepository;
import com.example.jwtacces.service.utils.ServiceUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class DebtsService {
    @Autowired
    private DebtRepository debtRepository;

    @Autowired
    private ServiceUtils serviceUtils;

    public ResponseEntity<?> getDebtByCreditorAndDebtor(@Valid @RequestBody String debtorUsername){
        Map<String, Object> httpResponse = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity creditor = serviceUtils.getUserFromAuthentification(authentication);
        UserEntity debtor;

        try{
            debtor = serviceUtils.getUserFromUsername(debtorUsername);
        } catch (UsernameNotFoundException e) {
            httpResponse.put("error","No puedes mandar solicitud a ese numero");
            return ResponseEntity.badRequest().body(httpResponse);
        }

        if(debtor == null || creditor.getId() == debtor.getId()){
            httpResponse.put("error","No puedes mandar solicitud a ese numero");
            return ResponseEntity.badRequest().body(httpResponse);
        }

        else {
            List<Debt> debtsAsCreditor = debtRepository.getDebtByCreditorAndDebtor(creditor, debtor);
            List<Debt> debtsAsDebtor = debtRepository.getDebtByCreditorAndDebtor(debtor, creditor);
            debtsAsCreditor.addAll(debtsAsDebtor);
            debtsAsCreditor.sort(Comparator.comparing(Debt::getDate).reversed());
            httpResponse.put("debts", debtsAsCreditor);
        }
        return ResponseEntity.ok().body(httpResponse);
    }



    public ResponseEntity<?> getDebtsByCreditorNotPaid(){
        Map<String, Object> httpResponse = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity creditor = serviceUtils.getUserFromAuthentification(authentication);

        List<Debt> debtsAsCreditor = debtRepository.getDebtByCreditorNotPaid(creditor);
        List<Debt> debtsAsDebtor = debtRepository.getDebtByDebtorNotPaid(creditor);

        debtsAsCreditor.addAll(debtsAsDebtor);
        debtsAsCreditor.sort(Comparator.comparing(Debt::getDate).reversed());

        httpResponse.put("debts", debtsAsCreditor);
        return ResponseEntity.ok().body(httpResponse);
    }

    public ResponseEntity<?> saveDebt(@Valid @RequestBody CreateDebtDTO newDebt){
        Map<String, Object> httpResponse = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity creditor = serviceUtils.getUserFromAuthentification(authentication);
        UserEntity debtor;

        try{
            debtor = serviceUtils.getUserFromUsername(newDebt.getDebtorUsername());
        } catch (UsernameNotFoundException e) {
            httpResponse.put("error","No puedes crear una deuda con ese deudor");
            return ResponseEntity.badRequest().body(httpResponse);
        }

        if(debtor == null || creditor.getId() == debtor.getId()){
            httpResponse.put("error","No puedes crear una deuda con ese deudor");
            return ResponseEntity.badRequest().body(httpResponse);
        }

        else {
            LocalDateTime currentDateTime = LocalDateTime.now();
            Debt debt = Debt.builder()
                    .creditor(creditor)
                    .debtor(debtor)
                    .amount(newDebt.getAmount())
                    .date(currentDateTime)
                    .description(newDebt.getDescription())
                    .isPaid(false)
                    .build();
            debtRepository.save(debt);
            httpResponse.put("response","Deuda guardada en la base de datos");

            return ResponseEntity.ok().body(httpResponse);
        }
    }

    public ResponseEntity<?> payOffDebt(Long debtId){
        Map<String, Object> httpResponse = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity creditor = serviceUtils.getUserFromAuthentification(authentication);
        Optional<UserEntity> creditorOfDebt = debtRepository.getCreditorOfDebt(debtId);

        if(creditorOfDebt.isPresent()){
            if(creditorOfDebt.get() == creditor){
                debtRepository.setDebtPaid(debtId);
                httpResponse.put("response", "Deuda pagada");
                return ResponseEntity.ok().body(httpResponse);
            } else {
                httpResponse.put("error", "No eres el acreedor de esta deuda");
                return ResponseEntity.badRequest().body(httpResponse);
            }

        } else {
            httpResponse.put("error", "No existe la deuda");
            return ResponseEntity.badRequest().body(httpResponse);
        }
    }
}
