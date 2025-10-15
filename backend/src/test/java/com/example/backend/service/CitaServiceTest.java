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
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
    // Test UNITARIO de obtenerCitas 
    @Test
    void cuandoObtenerCitas_entoncesRetornaListaDeCitasMapeadas() throws Exception {
        Cita cita = new Cita("1", "2", "2025-10-15", "10:30");
        cita.setId("100");
        when(citaRepository.findAll()).thenReturn(List.of(cita));
        Usuario usuario = new Usuario();
        usuario.setId("1");
        usuario.setNombre("Carlos");
        usuario.setApellido("Pérez");
        Usuario especialista = new Usuario();
        especialista.setId("2");
        especialista.setNombre("Ana");
        especialista.setApellido("López");
        when(usuarioRepository.findById("1")).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findById("2")).thenReturn(Optional.of(especialista));
        List<Map<String, Object>> resultado = citaService.obtenerCitas();
        assertThat(resultado).hasSize(1);
        Map<String, Object> citaMapeada = resultado.get(0);
        assertThat(citaMapeada)
            .containsEntry("id", "100")
            .containsEntry("usuarioId", "1")
            .containsEntry("especialistaId", "2")
            .containsEntry("fecha", "2025-10-15")
            .containsEntry("hora", "10:30")
            .containsEntry("especialistaNombre", "Ana López")
            .containsEntry("estado", "Activo")
            .containsEntry("usuario", "Carlos Pérez");

        verify(citaRepository).findAll();
        verify(usuarioRepository, times(2)).findById(anyString());
    }
    //Test UNITARIO de obtenerCitasUsuario
    @Test
    void cuandoObtenerCitasPorUsuario_entoncesRetornaListaDeCitas() {
        // Given
        String usuarioId = "1";
        when(citaRepository.findByUsuarioId(usuarioId)).thenReturn(List.of(citaMock));

        // When
        List<Cita> resultado = citaService.obtenerCitasPorUsuario(usuarioId);

        // Then
        assertThat(resultado).containsExactly(citaMock);
        verify(citaRepository).findByUsuarioId(usuarioId);
    }
    //obtenerCitasEspecialista
    @Test
    void cuandoObtenerCitasPorEspecialista_entoncesLlamaRepositorioYRetornaStream() {
        // Given
        String especialistaId = "2";
        List<Cita> citas = List.of(citaMock);
        when(citaRepository.findByEspecialistaId(especialistaId)).thenReturn(citas);

        // When
        List<Map<String, Object>> resultado = citaService.obtenerCitasPorEspecialista(especialistaId);

        // Then
        assertThat(resultado).isNotNull();
        verify(citaRepository).findByEspecialistaId(especialistaId);
    }
    // Test específico para mapearCitaBasica - Caso normal
    // Test 1: Happypath I → 1 → 2 → 3 → 4 → 5a → 6
    @Test
    void cuandoMapearCitaBasicaConEspecialistaId_entoncesMapaContieneClaveUsuario() throws Exception {
        // Given: Cita CON especialistaId
        Cita cita = new Cita("1", "2", "2025-10-15", "10:30"); // especialistaId = "2"
        cita.setId("100");
        
        Usuario usuario = new Usuario();
        usuario.setId("1");
        usuario.setNombre("Carlos");
        usuario.setApellido("Pérez");
        
        when(usuarioRepository.findById("1")).thenReturn(Optional.of(usuario));

        // When & Then: Debe usar clave "usuario"
        Method method = CitaService.class.getDeclaredMethod("mapearCitaBasica", Cita.class);
        method.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<String, Object> resultado = (Map<String, Object>) method.invoke(citaService, cita);

        assertThat(resultado).containsEntry("usuario", "Carlos Pérez");
        assertThat(resultado).doesNotContainKey("pacienteNombre");
    }

    // Test 2: Cuando especialistaId ES null → usa "pacienteNombre"  I → 1 → 2 → 3 → 4 → 5b → 6
    @Test
    void cuandoMapearCitaBasicaSinEspecialistaId_entoncesMapaContieneClavePacienteNombre() throws Exception {
        // Given: Cita SIN especialistaId
        Cita cita = new Cita("1", null, "2025-10-15", "10:30"); // especialistaId = null
        cita.setId("100");
        
        Usuario usuario = new Usuario();
        usuario.setId("1");
        usuario.setNombre("Carlos");
        usuario.setApellido("Pérez");
        
        when(usuarioRepository.findById("1")).thenReturn(Optional.of(usuario));

        // When & Then: Debe usar clave "pacienteNombre"
        Method method = CitaService.class.getDeclaredMethod("mapearCitaBasica", Cita.class);
        method.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<String, Object> resultado = (Map<String, Object>) method.invoke(citaService, cita);

        assertThat(resultado).containsEntry("pacienteNombre", "Carlos Pérez");
        assertThat(resultado).doesNotContainKey("usuario");
    }

    // Test específico para mapearCitaConDetalles - Solo estructura básica
    @Test
    void cuandoMapearCitaConDetalles_entoncesRetornaMapaConCamposAdicionales() throws Exception {
        // Given
        Cita cita = new Cita("1", "2", "2025-10-15", "10:30");
        cita.setId("100");
        
        Usuario usuario = new Usuario();
        usuario.setId("1");
        usuario.setNombre("Carlos");
        usuario.setApellido("Pérez");
        
        Usuario especialista = new Usuario();
        especialista.setId("2");
        especialista.setNombre("Ana");
        especialista.setApellido("García");
        
        when(usuarioRepository.findById("1")).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findById("2")).thenReturn(Optional.of(especialista));

        // When
        Method method = CitaService.class.getDeclaredMethod("mapearCitaConDetalles", Cita.class);
        method.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<String, Object> resultado = (Map<String, Object>) method.invoke(citaService, cita);

        // Then - Solo verifica que tiene los campos adicionales
        assertThat(resultado)
            .containsKeys("especialistaNombre", "estado");
    }

}
