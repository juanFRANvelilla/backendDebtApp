package com.example.zgzemergencymapback.task;

import com.example.zgzemergencymapback.service.IncidentsZgzDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTask {

    @Autowired
    private IncidentsZgzDataService incidentsZgzDataService;

    // Este método se ejecutará cada 15 minutos (900,000 ms)
    @Scheduled(fixedRate = 900000)
    public void executeTask() {
        System.out.println("Ejecutar tarea programada");
        incidentsZgzDataService.reloadTodayEmergency();
    }
}
