package com.example.zgzemergencymapback.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.example.zgzemergencymapback.model.*;
import com.example.zgzemergencymapback.model.incident.Incident;
import com.example.zgzemergencymapback.model.incident.IncidentStatusEnum;
import com.example.zgzemergencymapback.service.impl.ResourceServiceImpl;
import com.example.zgzemergencymapback.utils.impl.JsonConverterServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class JsonConverterServiceTest {
    @Mock
    private IncidentService incidentService;
    @Mock
    private IncidentResourceService incidentResourceService;
    @Mock
    private GoogleMapsService googleMapsService;
    @Mock
    private ResourceServiceImpl resourceService;
    @InjectMocks
    private JsonConverterServiceImpl jsonConverterService;


    /*
     * Test para comprobar si se guarda en la db un incident nuevo
     */
    @Test
    void testGetNewCloseIncidentInfoFromJson() throws IOException {
        // Dado este objeto json
        String json = "{\n" +
                "  \"totalCount\": 1,\n" +
                "  \"start\": 0,\n" +
                "  \"rows\": 1,\n" +
                "  \"fecha\": \"2024-08-14T00:00:00\",\n" +
                "  \"tipo\": \"10\",\n" +
                "  \"result\": [\n" +
                "    {\n" +
                "      \"fecha\": \"2024-08-13T22:24:04\",\n" +
                "      \"tipoSiniestro\": \"Accidente de tráfico\",\n" +
                "      \"direccion\": \"camino monzalbarba (Zaragoza)\",\n" +
                "      \"duracion\": \"0 h  55 m\",\n" +
                "      \"recursos\": [\n" +
                "        \"Bomba pesada mixta\",\n" +
                "        \"Bomba nodriza pesada\",\n" +
                "        \"Autoescala automática 30 m.\"\n" +
                "      ],\n" +
                "      \"tipo\": \"10\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        IncidentStatusEnum status = IncidentStatusEnum.CLOSED;

        // Mocking
        // No existe un incidente con esa fecha y hora
        when(incidentService.getIncidentByDateAndTime(any(LocalDate.class), any(LocalTime.class))).thenReturn(Optional.empty());
        when(googleMapsService.getcoordinates(anyString())).thenReturn("{\"results\": [{\"geometry\": {\"location\": {\"lat\": 40.7128, \"lng\": -74.0060}}, \"address_components\": [{\"long_name\": \"123 Test St\"}]}]}");
        // Crear lista con 3 IncidentResource simulando que son las clases que se encuentrar al buscar en la base de datos
        List<IncidentResource> incidentResourceList = Arrays.asList(
                new IncidentResource(),
                new IncidentResource(),
                new IncidentResource()
        );

        when(incidentResourceService.findIncidentResourceByIncident(any(Incident.class)))
                .thenReturn(incidentResourceList);



        // Llamamos al método a testear
        List<Incident> result = jsonConverterService.getIncidentInfoFromJson(json, status);

        assertEquals(0, result.size());

        Incident firstIncident = result.get(0);
        assertEquals("Accidente de tráfico", firstIncident.getIncidentType());
        assertEquals(IncidentStatusEnum.CLOSED, firstIncident.getStatus());
        assertEquals(40.7128, firstIncident.getLatitude());
        assertEquals(-74.0060, firstIncident.getLongitude());
        assertEquals(status, firstIncident.getStatus());


        // LLamada a los métodos de los mocks
        verify(incidentService, times(2)).saveIncident(any(Incident.class));
        verify(incidentResourceService, times(1)).addResourceToIncident(any(Incident.class), anyList());
    }

    /*
     * Test para comprobar si se actualiza un incidente abierto, que se ha cerrado
     */
    @Test
    void testUpdateOpenIncidentFromJson() throws IOException {
        // Dado este objeto json
        String json = "{\n" +
                "  \"totalCount\": 1,\n" +
                "  \"start\": 0,\n" +
                "  \"rows\": 1,\n" +
                "  \"fecha\": \"2024-08-14T00:00:00\",\n" +
                "  \"tipo\": \"10\",\n" +
                "  \"result\": [\n" +
                "    {\n" +
                "      \"fecha\": \"2024-08-13T22:24:04\",\n" +
                "      \"tipoSiniestro\": \"Accidente de tráfico\",\n" +
                "      \"direccion\": \"camino monzalbarba (Zaragoza)\",\n" +
                "      \"duracion\": \"0 h  55 m\",\n" +
                "      \"recursos\": [\n" +
                "        \"Bomba pesada mixta\",\n" +
                "        \"Bomba nodriza pesada\",\n" +
                "        \"Autoescala automática 30 m.\"\n" +
                "      ],\n" +
                "      \"tipo\": \"20\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        Incident incidentDb = Incident.builder()
                .date(LocalDate.of(2024, 8, 14)) // Fecha actual o una fecha específica
                .time(LocalTime.of(0, 0)) // Hora específica
                .status(IncidentStatusEnum.OPEN) // Enum de estado, ajusta según el enum disponible
                .incidentType("Accidente de tráfico") // Tipo de incidente
                .address("camino monzalbarba (Zaragoza)") // Dirección del incidente
                .duration("0 h  55 m") // Duración estimada
                .latitude(40.7128) // Latitud del incidente
                .longitude(-74.0060) // Longitud del incidente
                .build();


        IncidentStatusEnum status = IncidentStatusEnum.CLOSED;

        // Mocking
        // Si existe un incidente con esa fecha y hora, ademas esta OPEN
        when(incidentService.getIncidentByDateAndTime(any(LocalDate.class), any(LocalTime.class))).thenReturn(Optional.of(incidentDb));
        // Crear lista con 3 IncidentResource simulando que son las clases que se encuentrar al buscar en la base de datos
        List<IncidentResource> incidentResourceList = Arrays.asList(
                new IncidentResource(),
                new IncidentResource(),
                new IncidentResource()
        );

        // Llamamos al método a testear
        List<Incident> result = jsonConverterService.getIncidentInfoFromJson(json, status);

        assertEquals(1, result.size());

        Incident firstIncident = result.get(0);
        assertEquals("Accidente de tráfico", firstIncident.getIncidentType());
        assertEquals(IncidentStatusEnum.CLOSED, firstIncident.getStatus());
        assertEquals(40.7128, firstIncident.getLatitude());
        assertEquals(-74.0060, firstIncident.getLongitude());


        // LLamada a los métodos de los mocks
        verify(incidentService, times(1)).saveIncident(any(Incident.class));
        verify(incidentResourceService, times(0)).addResourceToIncident(any(Incident.class), anyList());
    }

    /*
     * Test para comprobar si se actualiza un incidente abierto, que aun no se ha cerrado
     */
    @Test
    void testNotUpdateOpenIncidentFromJson() throws IOException {
        // Dado este objeto json
        String json = "{\n" +
                "  \"totalCount\": 1,\n" +
                "  \"start\": 0,\n" +
                "  \"rows\": 1,\n" +
                "  \"fecha\": \"2024-08-14T00:00:00\",\n" +
                "  \"tipo\": \"10\",\n" +
                "  \"result\": [\n" +
                "    {\n" +
                "      \"fecha\": \"2024-08-13T22:24:04\",\n" +
                "      \"tipoSiniestro\": \"Accidente de tráfico\",\n" +
                "      \"direccion\": \"camino monzalbarba (Zaragoza)\",\n" +
                "      \"duracion\": \"0 h  55 m\",\n" +
                "      \"recursos\": [\n" +
                "        \"Bomba pesada mixta\",\n" +
                "        \"Bomba nodriza pesada\",\n" +
                "        \"Autoescala automática 30 m.\"\n" +
                "      ],\n" +
                "      \"tipo\": \"20\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        Incident incidentDb = Incident.builder()
                .date(LocalDate.of(2024, 8, 14))
                .time(LocalTime.of(0, 0))
                .status(IncidentStatusEnum.OPEN)
                .incidentType("Accidente de tráfico")
                .address("camino monzalbarba (Zaragoza)")
                .duration("0 h  55 m")
                .latitude(40.7128)
                .longitude(-74.0060)
                .build();


        IncidentStatusEnum status = IncidentStatusEnum.OPEN;

        // Mocking
        // Si existe un incidente con esa fecha y hora, ademas esta OPEN
        when(incidentService.getIncidentByDateAndTime(any(LocalDate.class), any(LocalTime.class))).thenReturn(Optional.of(incidentDb));
        // Crear lista con 3 IncidentResource simulando que son las clases que se encuentrar al buscar en la base de datos
        List<IncidentResource> incidentResourceList = Arrays.asList(
                new IncidentResource(),
                new IncidentResource(),
                new IncidentResource()
        );

        // Llamamos al método a testear
        List<Incident> result = jsonConverterService.getIncidentInfoFromJson(json, status);

        assertEquals(1, result.size());


        // LLamada a los métodos de los mocks
        verify(incidentService, times(0)).saveIncident(any(Incident.class));
        verify(incidentResourceService, times(0)).addResourceToIncident(any(Incident.class), anyList());
    }

}
