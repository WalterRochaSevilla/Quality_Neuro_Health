package com.example.backend.controller;

import com.example.backend.service.EmocionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EmocionControllerTest {

    @Mock
    private EmocionService emocionService;

    @InjectMocks
    private EmocionController emocionController;

    private Map<String, String> requestMock;

    @BeforeEach
    void setUp() {
        requestMock = new HashMap<>();
        requestMock.put("usuarioId", "user001");
        requestMock.put("contenido", "Hoy fue un día excelente");
        requestMock.put("emocion", "Felicidad");
    }

    // ✅ Caso 1: escribirEnDiario() exitoso
    @Test
    void testEscribirEnDiario_Exito() {
        Map<String, Object> respuestaMock = new HashMap<>();
        respuestaMock.put("mensaje", "Entrada guardada con éxito");
        respuestaMock.put("emocion", "Felicidad");

        when(emocionService.escribirEnDiario("user001", "Hoy fue un día excelente", "Felicidad"))
                .thenReturn(respuestaMock);

        ResponseEntity<?> response = emocionController.escribirEnDiario(requestMock);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);

        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("Entrada guardada con éxito", body.get("mensaje"));
        assertEquals("Felicidad", body.get("emocion"));

        verify(emocionService).escribirEnDiario("user001", "Hoy fue un día excelente", "Felicidad");
    }

    // 💥 Caso 2: escribirEnDiario() lanza excepción
    @Test
    void testEscribirEnDiario_Error() {
        when(emocionService.escribirEnDiario(anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("Error al guardar la entrada"));

        ResponseEntity<?> response = emocionController.escribirEnDiario(requestMock);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Error al guardar la entrada"));

        verify(emocionService).escribirEnDiario("user001", "Hoy fue un día excelente", "Felicidad");
    }

    // ✅ Caso 3: obtenerDiarioPorUsuario() exitoso
    @Test
    void testObtenerDiarioPorUsuario_Exito() {
        Map<String, Object> diarioMock = new HashMap<>();
        diarioMock.put("usuarioId", "user001");
        diarioMock.put("entradas", List.of(
                Map.of("fecha", "2025-10-13", "emocion", "Felicidad", "contenido", "Hoy fue un gran día")
        ));

        when(emocionService.obtenerDiarioCompleto("user001")).thenReturn(diarioMock);

        ResponseEntity<?> response = emocionController.obtenerDiarioPorUsuario("user001");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);

        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("user001", body.get("usuarioId"));
        assertNotNull(body.get("entradas"));

        verify(emocionService).obtenerDiarioCompleto("user001");
    }

    // ⚠️ Caso 4: obtenerDiarioPorUsuario() lanza excepción (usuario no tiene diario)
    @Test
    void testObtenerDiarioPorUsuario_NoEncontrado() {
        when(emocionService.obtenerDiarioCompleto("user999"))
                .thenThrow(new RuntimeException("No se encontró diario para el usuario con ID user999"));

        ResponseEntity<?> response = emocionController.obtenerDiarioPorUsuario("user999");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("No se encontró diario para el usuario con ID user999", response.getBody());

        verify(emocionService).obtenerDiarioCompleto("user999");
    }
}
