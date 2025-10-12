package com.example.backend.service;

import com.example.backend.model.Cita;
import com.example.backend.model.Usuario;
import com.example.backend.model.Especialista;
import com.example.backend.repository.CitaRepository;
import com.example.backend.repository.EspecialistaRepository;
import com.example.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.lang.reflect.Method;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CitaServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private EspecialistaRepository especialistaRepository;

    @Mock
    private CitaRepository citaRepository;

    @Mock
    private RecordatorioService reminderService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private CitaService citaService;

    private Usuario usuarioMock;
    private Especialista especialistaMock;
    private Cita citaMock;

    @BeforeEach
    void setUp() {
        usuarioMock = new Usuario();
        usuarioMock.setId("1");
        usuarioMock.setNombre("Carlos");
        usuarioMock.setApellido("Pérez");
        usuarioMock.setEmail("carlos@example.com");

        especialistaMock = new Especialista();
        especialistaMock.setId("2");
        especialistaMock.setName("Dr. López");

        citaMock = new Cita("1", "2", "2025-10-15", "10:30");
        citaMock.setId("100");
    }
    //crear cita
    // ✅ Camino 1: I -> 1 -> 2 -> 3 -> F (flujo normal)
    @Test
    void cuandoUsuarioYEspecialistaExisten_entoncesCitaEsCreada() throws Exception {
        // Given
        when(usuarioRepository.findById("1")).thenReturn(Optional.of(usuarioMock));
        when(especialistaRepository.findByEspecialistaId("2")).thenReturn(Optional.of(especialistaMock));
        when(citaRepository.save(any(Cita.class))).thenReturn(citaMock);

        doNothing().when(reminderService).scheduleReminderForAppointment(any(Cita.class));
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString());

        // When
        Cita resultado = citaService.crearCita("1", "2", "2025-10-15", "10:30");

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo("100");

        verify(usuarioRepository).findById("1");
        verify(especialistaRepository).findByEspecialistaId("2");
        verify(citaRepository).save(any(Cita.class));
        verify(reminderService).scheduleReminderForAppointment(any(Cita.class));
        verify(emailService).sendEmail(anyString(), anyString(), anyString());
    }

    // Camino 2: I -> 1 -> 2 -> 5 -> F (especialista no encontrado)
    @Test
    void cuandoUsuarioExistePeroEspecialistaNo_entoncesLanzaExcepcion() throws Exception {
        // Given
        when(usuarioRepository.findById("1")).thenReturn(Optional.of(usuarioMock));
        when(especialistaRepository.findByEspecialistaId("99")).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> citaService.crearCita("1", "99", "2025-10-15", "10:30"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Especialista no encontrado con ID: 99");

        verify(citaRepository, never()).save(any(Cita.class));
        verify(reminderService, never()).scheduleReminderForAppointment(any());
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    // Camino 3: I -> 1 -> 4 -> F (usuario no encontrado)
    @Test
    void cuandoUsuarioNoExiste_entoncesLanzaExcepcion() throws Exception {
        // Given
        when(usuarioRepository.findById("99")).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> citaService.crearCita("99", "2", "2025-10-15", "10:30"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Usuario no encontrado con ID: 99");

        verify(especialistaRepository, never()).findByEspecialistaId(anyString());
        verify(citaRepository, never()).save(any(Cita.class));
        verify(reminderService, never()).scheduleReminderForAppointment(any());
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    // enviarConfirmacionCita

    @Test
    void cuandoEnvioCorreoExitoso_entoncesNoLanzaExcepcion() throws Exception {
        // Given - Camino I→1→2→F (sin excepciones)
        Usuario especialistaUsuario = new Usuario();
        especialistaUsuario.setId("2");
        especialistaUsuario.setNombre("Ana");
        especialistaUsuario.setApellido("García");
        
        when(usuarioRepository.findById("2")).thenReturn(Optional.of(especialistaUsuario));
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString());

        // When & Then - Usar Reflection para método privado
        Method method = CitaService.class.getDeclaredMethod("enviarConfirmacionCita", 
            Usuario.class, String.class, String.class, String.class, String.class);
        method.setAccessible(true);
        
        assertThatCode(() ->
            method.invoke(citaService, usuarioMock, "2", "2025-10-15", "10:30", "100")
        ).doesNotThrowAnyException();

        verify(usuarioRepository).findById("2");
        verify(emailService).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void cuandoRepositoryLanzaExcepcion_entoncesSeCapturaExcepcion() throws Exception {
        // Given - Camino I→1→3→F (excepción en repository)
        when(usuarioRepository.findById("99"))
            .thenThrow(new RuntimeException("Database error"));

        // When & Then
        Method method = CitaService.class.getDeclaredMethod("enviarConfirmacionCita", 
            Usuario.class, String.class, String.class, String.class, String.class);
        method.setAccessible(true);
        
        assertThatCode(() ->
            method.invoke(citaService, usuarioMock, "99", "2025-10-15", "10:30", "100")
        ).doesNotThrowAnyException();

        verify(usuarioRepository).findById("99");
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

}
