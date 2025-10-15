package com.example.backend.controller;

import com.example.backend.service.ExamenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ExamenControllerTest {

    @Mock
    private ExamenService examenService;

    @InjectMocks
    private ExamenController examenController;

    private Map<String, String> requestMock;

    @BeforeEach
    void setUp() {
        requestMock = new HashMap<>();
        requestMock.put("usuarioId", "user123");
        requestMock.put("description", "Análisis de sangre completo");
        requestMock.put("name", "Hemograma");
        requestMock.put("result", "Normal");
    }

    // ✅ Caso exitoso al añadir un examen
    @Test
    void escribirEnDiario_exitoso() throws Exception {
        Map<String, Object> respuestaEsperada = new HashMap<>();
        respuestaEsperada.put("mensaje", "Examen añadido correctamente");

        when(examenService.aumentarExamen(
                "user123", "Análisis de sangre completo", "Hemograma", "Normal"
        )).thenReturn(respuestaEsperada);

        ResponseEntity<?> response = examenController.escribirEnDiario(requestMock);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(respuestaEsperada, response.getBody());
        verify(examenService, times(1)).aumentarExamen("user123", "Análisis de sangre completo", "Hemograma", "Normal");
    }

    // ❌ Caso de error al añadir examen (excepción lanzada)
    @Test
    void escribirEnDiario_error() throws Exception {
        when(examenService.aumentarExamen(anyString(), anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("Fallo en base de datos"));

        ResponseEntity<?> response = examenController.escribirEnDiario(requestMock);

        assertEquals(500, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Fallo en base de datos"));
        verify(examenService, times(1)).aumentarExamen(anyString(), anyString(), anyString(), anyString());
    }

    // ✅ Caso exitoso al obtener exámenes por usuario
    @Test
    void obtenerDiarioPorUsuario_exitoso() throws Exception {
        Map<String, Object> examenMock = new HashMap<>();
        examenMock.put("usuarioId", "user123");
        examenMock.put("nombreExamen", "Hemograma");

        when(examenService.obtenerExamenes("user123")).thenReturn(examenMock);

        ResponseEntity<?> response = examenController.obtenerDiarioPorUsuario("user123");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(examenMock, response.getBody());
        verify(examenService, times(1)).obtenerExamenes("user123");
    }

    // ❌ Caso en el que no se encuentra examen o hay error
    @Test
    void obtenerDiarioPorUsuario_error() throws Exception {
        when(examenService.obtenerExamenes("user404"))
                .thenThrow(new RuntimeException("Usuario no encontrado"));

        ResponseEntity<?> response = examenController.obtenerDiarioPorUsuario("user404");

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Usuario no encontrado", response.getBody());
        verify(examenService, times(1)).obtenerExamenes("user404");
    }
}
