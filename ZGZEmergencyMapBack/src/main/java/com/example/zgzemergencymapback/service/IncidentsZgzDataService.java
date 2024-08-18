package com.example.zgzemergencymapback.service;

import com.example.zgzemergencymapback.model.CoordinatesAndAddress;
import com.example.zgzemergencymapback.model.incident.Incident;
import com.example.zgzemergencymapback.model.incident.IncidentStatusEnum;
import com.example.zgzemergencymapback.utils.JsonConverterService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class IncidentsZgzDataService {
    private final RestTemplate restTemplate;

    @Autowired
    private JsonConverterService jsonConverterService;

    @Autowired
    IncidentService incidentService;

    private Set<CoordinatesAndAddress> coordinatesAndAddressSet = new HashSet<>();

    public IncidentsZgzDataService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /*
      * Método que obtiene los datos de los incidentes de la API de Zaragoza
      * y los guarda en la base de datos, devuelve la lista de incident que se han
      * introducido en la base de datos o incidentes que se han cerrado
     */
    public List<Incident> reloadTodayEmergency() {
        // Vaciar el test de coordenadas y direcciones
        coordinatesAndAddressSet.clear();
        String url = "https://www.zaragoza.es/sede/servicio/bomberos?tipo=20&rf=markdown";
        List<Incident> incidentList = new ArrayList<>();
        try {
            String jsonResponse = restTemplate.getForObject(url, String.class);
            // Convertir json a objetos incident cerrados, y llevar a cabo la logica para
            // guardar en la base de datos, devuelve la lista de incident que se han decodificado
            incidentList = jsonConverterService.getIncidentInfoFromJson(jsonResponse, IncidentStatusEnum.CLOSED, coordinatesAndAddressSet);


            url = "https://www.zaragoza.es/sede/servicio/bomberos?tipo=10&rf=markdown";
            jsonResponse = restTemplate.getForObject(url, String.class);
            // Convertir el JSON a objetos Incident abiertos, y llevar a cabo la logica para
            // guardar en la base de datos, devuelve la lista de incident que se han decodificado
            List<Incident> openIncidentList = jsonConverterService.getIncidentInfoFromJson(jsonResponse, IncidentStatusEnum.OPEN, coordinatesAndAddressSet);
            incidentList.addAll(openIncidentList);

            incidentService.handleLostIncidents(openIncidentList);

        } catch (RestClientException e) {
            System.err.println("Error al hacer la llamada a la API: " + e.getMessage());
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            // Maneja errores relacionados con la conversión del JSON
            System.err.println("Error al procesar el JSON: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            // Maneja cualquier otro tipo de error no previsto
            System.err.println("Ocurrió un error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
        return incidentList;
    }



    public List<Incident> reloadYesterdayEmergency() {
        // Vaciar el test de coordenadas y direcciones
        coordinatesAndAddressSet.clear();
        String url = "https://www.zaragoza.es/sede/servicio/bomberos?tipo=21&rf=markdown";
        List<Incident> incidentList = new ArrayList<>();
        try {
            String jsonResponse = restTemplate.getForObject(url, String.class);
            incidentList = jsonConverterService.getIncidentInfoFromJson(jsonResponse, IncidentStatusEnum.CLOSED, coordinatesAndAddressSet);

        } catch (RestClientException e) {
            System.err.println("Error al hacer la llamada a la API: " + e.getMessage());
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            // Maneja errores relacionados con la conversión del JSON
            System.err.println("Error al procesar el JSON: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            // Maneja cualquier otro tipo de error no previsto
            System.err.println("Ocurrió un error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
        return incidentList;
    }

}
