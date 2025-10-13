package com.example.backend.controller;

import com.example.backend.model.Especialista;
import com.example.backend.model.Usuario;
import com.example.backend.repository.EspecialistaRepository;
import com.example.backend.service.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EspecialistaControllerTest {

    @Mock
    private EspecialistaService especialistaService;
    @Mock
    private UsuarioService usuarioService;
    @Mock
    private EmailService emailService;
    @Mock
    private CitaService citaService;
    @Mock
    private EspecialistaRepository especialistaRepository;

    @InjectMocks
    private EspecialistaController especialistaController;

    private Usuario mockUsuario;
    private Especialista mockEspecialista;

    @BeforeEach
    void setUp() {
        mockUsuario = new Usuario();
        mockUsuario.setId("u1");
        mockUsuario.setNombre("Juan");
        mockUsuario.setEmail("juan@test.com");

        mockEspecialista = new Especialista();
        mockEspecialista.setEspecialistaId("e1");
        mockEspecialista.setName("Dr. López");
    }

    // ✅ Caso 1: Obtener todos los especialistas (éxito)
    @Test
    void getAllEspecialistas_exitoso() {
        List<Especialista> lista = List.of(mockEspecialista);
        when(especialistaService.getAllEspecialistas()).thenReturn(lista);

        ResponseEntity<List<Especialista>> response = especialistaController.getAllEspecialistas();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals("Dr. López", response.getBody().get(0).getName());
        verify(especialistaService).getAllEspecialistas();
    }

    // ✅ Caso 2: Obtener horarios de un especialista (éxito)
    @Test
    void getHorariosByEspecialistaId_exitoso() {
        Map<String, List<String>> horarios = new HashMap<>();
        horarios.put("lunes", List.of("10:00", "11:00"));
        when(especialistaService.getHorariosByEspecialistaId("e1", "2025-10-13")).thenReturn(horarios);

        ResponseEntity<Map<String, List<String>>> response =
                especialistaController.getHorariosByEspecialistaId("e1", "2025-10-13");

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().containsKey("lunes"));
        verify(especialistaService).getHorariosByEspecialistaId("e1", "2025-10-13");
    }

    // ✅ Caso 3: Ocupar hora (éxito total con correo enviado)
    @Test
    void ocuparHora_exitoso() throws Exception {
        Map<String, String> body = Map.of(
                "hour", "09:00",
                "fecha", "2025-10-15",
                "userId", "u1"
        );

        when(usuarioService.obtenerUsuarioPorId("u1")).thenReturn(mockUsuario);
        when(especialistaRepository.findById("e1")).thenReturn(Optional.of(mockEspecialista));
        when(especialistaService.addOccupiedHour("e1", "09:00", "2025-10-15")).thenReturn(true);

        ResponseEntity<Map<String, String>> response = especialistaController.ocuparHora("e1", body);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Hora ocupada con éxito", response.getBody().get("message"));

        verify(especialistaService).addOccupiedHour("e1", "09:00", "2025-10-15");
        verify(citaService).crearCita("u1", "e1", "2025-10-15", "09:00");
        verify(emailService).sendEmail(
                eq("juan@test.com"),
                eq("Cita Confirmada"),
                contains("Tu cita con Dr. López")
        );
    }

    // ⚠️ Caso 4: Ocupar hora fallido (no se pudo ocupar)
    @Test
    void ocuparHora_fallido() throws Exception {
        Map<String, String> body = Map.of(
                "hour", "15:00",
                "fecha", "2025-10-15",
                "userId", "u1"
        );

        when(usuarioService.obtenerUsuarioPorId("u1")).thenReturn(mockUsuario);
        when(especialistaRepository.findById("e1")).thenReturn(Optional.of(mockEspecialista));
        when(especialistaService.addOccupiedHour("e1", "15:00", "2025-10-15")).thenReturn(false);

        ResponseEntity<Map<String, String>> response = especialistaController.ocuparHora("e1", body);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("No se pudo ocupar la hora", response.getBody().get("message"));
        verify(especialistaService).addOccupiedHour("e1", "15:00", "2025-10-15");
    }

    // ⚠️ Caso 5: Error al enviar correo (no rompe el flujo)
    @Test
    void ocuparHora_errorEnvioCorreo_noRompeFlujo() throws Exception {
        Map<String, String> body = Map.of(
                "hour", "08:00",
                "fecha", "2025-10-15",
                "userId", "u1"
        );

        when(usuarioService.obtenerUsuarioPorId("u1")).thenReturn(mockUsuario);
        when(especialistaRepository.findById("e1")).thenReturn(Optional.of(mockEspecialista));
        when(especialistaService.addOccupiedHour("e1", "08:00", "2025-10-15")).thenReturn(true);
        doThrow(new Exception("Error SMTP")).when(emailService)
                .sendEmail(anyString(), anyString(), anyString());

        ResponseEntity<Map<String, String>> response = especialistaController.ocuparHora("e1", body);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Hora ocupada con éxito", response.getBody().get("message"));
        verify(emailService).sendEmail(anyString(), anyString(), anyString());
    }

    // ✅ Caso 6: Obtener pacientes por especialista (éxito)
    @Test
    void getPatientsByEspecialistaId_exitoso() {
        List<Map<String, String>> pacientes = List.of(
                Map.of("nombre", "Juan", "apellido", "Pérez")
        );

        when(especialistaService.getPatientsByEspecialistaId("e1")).thenReturn(pacientes);

        List<Map<String, String>> response = especialistaController.getPatientsByEspecialistaId("e1");

        assertEquals(1, response.size());
        assertEquals("Juan", response.get(0).get("nombre"));
        verify(especialistaService).getPatientsByEspecialistaId("e1");
    }
}
