package com.example.backend.controller;

import com.example.backend.model.Cita;
import com.example.backend.service.CitaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CitaControllerTest {

    @Mock
    private CitaService citaService;

    @InjectMocks
    private CitaController citaController;

    private Cita citaMock;

    @BeforeEach
    void setUp() {
        citaMock = new Cita();
        citaMock.setId("cita123");
        citaMock.setUsuarioId("user001");
        citaMock.setEspecialistaId("esp001");
        citaMock.setFecha("2025-10-15");
        citaMock.setHora("10:00");
    }

    @Test
    void testCrearCita() {
        // Datos de entrada simulados
        Map<String, String> datosCita = new HashMap<>();
        datosCita.put("usuarioId", "user001");
        datosCita.put("especialistaId", "esp001");
        datosCita.put("fecha", "2025-10-15");
        datosCita.put("hora", "10:00");

        when(citaService.crearCita("user001", "esp001", "2025-10-15", "10:00")).thenReturn(citaMock);

        Cita resultado = citaController.crearCita(datosCita);

        assertNotNull(resultado);
        assertEquals("cita123", resultado.getId());
        assertEquals("user001", resultado.getUsuarioId());
        assertEquals("esp001", resultado.getEspecialistaId());
        assertEquals("2025-10-15", resultado.getFecha());
        assertEquals("10:00", resultado.getHora());

        verify(citaService).crearCita("user001", "esp001", "2025-10-15", "10:00");
    }

    @Test
    void testObtenerCitas() {
        List<Map<String, Object>> listaCitas = new ArrayList<>();
        Map<String, Object> cita1 = new HashMap<>();
        cita1.put("usuarioId", "user001");
        cita1.put("fecha", "2025-10-15");
        listaCitas.add(cita1);

        when(citaService.obtenerCitas()).thenReturn(listaCitas);

        List<Map<String, Object>> resultado = citaController.obtenerCitas();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("user001", resultado.get(0).get("usuarioId"));
        verify(citaService).obtenerCitas();
    }

    @Test
    void testObtenerCitasPorUsuario() {
        List<Cita> citasUsuario = Collections.singletonList(citaMock);
        when(citaService.obtenerCitasPorUsuario("user001")).thenReturn(citasUsuario);

        List<Cita> resultado = citaController.obtenerCitasPorUsuario("user001");

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("user001", resultado.get(0).getUsuarioId());
        verify(citaService).obtenerCitasPorUsuario("user001");
    }

    @Test
    void testObtenerCitasPorEspecialista() {
        List<Map<String, Object>> citasEspecialista = new ArrayList<>();
        Map<String, Object> citaEsp = new HashMap<>();
        citaEsp.put("especialistaId", "esp001");
        citaEsp.put("hora", "10:00");
        citasEspecialista.add(citaEsp);

        when(citaService.obtenerCitasPorEspecialista("esp001")).thenReturn(citasEspecialista);

        List<Map<String, Object>> resultado = citaController.obtenerCitasPorEspecialista("esp001");

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("esp001", resultado.get(0).get("especialistaId"));
        verify(citaService).obtenerCitasPorEspecialista("esp001");
    }
}
