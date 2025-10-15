package com.example.backend.service;

import com.example.backend.model.Especialista;
import com.example.backend.model.Usuario;
import com.example.backend.repository.EspecialistaRepository;
import com.example.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EspecialistaServiceTest {

    @Mock
    private EspecialistaRepository especialistaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private EspecialistaService especialistaService;

    // Test Case: Camino 1→1→2→F (Especialista no encontrado - retorna mapa vacío)
    @Test
    void getHorariosByEspecialistaId_WhenEspecialistaNotFound_ShouldReturnEmptyMap() {
        String id = "especialista-inexistente";
        String fecha = "2024-01-15";
        
        when(especialistaRepository.findById(id)).thenReturn(Optional.empty());

        Map<String, List<String>> resultado = especialistaService.getHorariosByEspecialistaId(id, fecha);

        assertThat(resultado).isNotNull();
        assertThat(resultado).isEmpty();

        verify(especialistaRepository).findById(id);
        verifyNoMoreInteractions(especialistaRepository);
        verifyNoInteractions(usuarioRepository);
    }
    // Test Case: Camino 1→1→2→3→F (Especialista encontrado - devuelve lista de citas de especialista)
@Test
void getHorariosByEspecialistaId_WhenEspecialistaExists_ShouldReturnHorarios() {
    String id = "1";
    String fecha = "2024-01-15";
    
    Especialista especialistaMock = new Especialista();
    especialistaMock.setId(id);
    especialistaMock.setHours(List.of("09:00", "10:00", "11:00"));
    
    Map<String, List<String>> occupiedHours = new HashMap<>();
    occupiedHours.put("2024-01-15", List.of("10:00"));
    especialistaMock.setOccupiedHours(occupiedHours);
    
    when(especialistaRepository.findById(id)).thenReturn(Optional.of(especialistaMock));

    Map<String, List<String>> resultado = especialistaService.getHorariosByEspecialistaId(id, fecha);

    assertThat(resultado).isNotNull();
    assertThat(resultado).containsKeys("hours", "occupiedHours");
    assertThat(resultado.get("hours")).isEqualTo(List.of("09:00", "10:00", "11:00"));
    assertThat(resultado.get("occupiedHours")).isEqualTo(List.of("10:00"));

    verify(especialistaRepository).findById(id);
}
// Test Case: Camino 1→2→9 (Especialista no encontrado - devuelve false)
@Test
void addOccupiedHour_WhenEspecialistaNotFound_ShouldReturnFalse() {
    String id = "NULL";
    String fecha = "2024-01-15";
    String hour = "10:00";
    
    when(especialistaRepository.findById(id)).thenReturn(Optional.empty());

    boolean resultado = especialistaService.addOccupiedHour(id, hour, fecha);

    assertThat(resultado).isFalse();

    verify(especialistaRepository).findById(id);
    verifyNoMoreInteractions(especialistaRepository);
    verifyNoInteractions(usuarioRepository);
}
// Test Case CORREGIDO: Camino I→1→2→3→4→5→6→7→8→F (Especialista existe, fecha NO existe - devuelve true)
@Test
void addOccupiedHour_WhenEspecialistaExistsAndFechaNotExists_ShouldReturnTrue() {
    String id = "1";
    String fecha = "20/10/2025";
    String hour = "10:00";
    
    Especialista especialistaMock = new Especialista();
    especialistaMock.setId(id);
    
    Map<String, List<String>> occupiedHours = new HashMap<>();
    occupiedHours.put("19/10/2025", List.of("09:00", "11:00")); // Otra fecha diferente
    especialistaMock.setOccupiedHours(occupiedHours);
    
    when(especialistaRepository.findById(id)).thenReturn(Optional.of(especialistaMock));
    when(especialistaRepository.save(any(Especialista.class))).thenReturn(especialistaMock);

    boolean resultado = especialistaService.addOccupiedHour(id, hour, fecha);

    assertThat(resultado).isTrue();

    verify(especialistaRepository).findById(id);
    verify(especialistaRepository).save(any(Especialista.class));
}

// Test Case NUEVO: Camino I→1→2→3→4→6→7→9→F (Especialista existe, fecha existe, hora YA ocupada - devuelve false)
@Test
void addOccupiedHour_WhenEspecialistaExistsAndFechaExistsAndHoraAlreadyOccupied_ShouldReturnFalse() {
    String id = "1";
    String fecha = "20/10/2025";
    String hour = "10:00"; 
    
    Especialista especialistaMock = new Especialista();
    especialistaMock.setId(id);
    
    Map<String, List<String>> occupiedHours = new HashMap<>();
    occupiedHours.put("20/10/2025", new ArrayList<>(List.of("09:00", "10:00", "11:00")));
    especialistaMock.setOccupiedHours(occupiedHours);
    
    when(especialistaRepository.findById(id)).thenReturn(Optional.of(especialistaMock));

    boolean resultado = especialistaService.addOccupiedHour(id, hour, fecha);

    assertThat(resultado).isFalse();

    verify(especialistaRepository).findById(id);
    verify(especialistaRepository, never()).save(any(Especialista.class));
}
// Test Case: Camino 1→1→2→F (Especialista no encontrado - devuelve false)
@Test
void addPatient_WhenEspecialistaNotFound_ShouldReturnFalse() {
    String especialistaId = "999";
    String pacienteId = "paciente123";
    
    when(especialistaRepository.findById(especialistaId)).thenReturn(Optional.empty());

    boolean resultado = especialistaService.addPatient(especialistaId, pacienteId);

    assertThat(resultado).isFalse();

    verify(especialistaRepository).findById(especialistaId);
    verify(especialistaRepository, never()).save(any(Especialista.class));
    verifyNoInteractions(usuarioRepository);
}
// Test Case: Camino I→1→2→3→4→5→F (Especialista existe, paciente NO registrado - devuelve true)
@Test
void addPatient_WhenEspecialistaExistsAndPatientNotRegistered_ShouldReturnTrue() {
    String especialistaId = "1";
    String pacienteId = "nuevo-paciente";
    
    Especialista especialistaMock = new Especialista();
    especialistaMock.setId(especialistaId);
    especialistaMock.setPatients(new ArrayList<>(List.of("paciente-existente"))); // Paciente diferente
    
    when(especialistaRepository.findById(especialistaId)).thenReturn(Optional.of(especialistaMock));
    when(especialistaRepository.save(any(Especialista.class))).thenReturn(especialistaMock);

    boolean resultado = especialistaService.addPatient(especialistaId, pacienteId);

    assertThat(resultado).isTrue();

    verify(especialistaRepository).findById(especialistaId);
    verify(especialistaRepository).save(any(Especialista.class));
    verifyNoInteractions(usuarioRepository);
}
// Test para addPatient - Camino I→1→2→3→4→F (Paciente YA registrado - devuelve false)
@Test
void addPatient_WhenEspecialistaExistsAndPatientAlreadyRegistered_ShouldReturnFalse() {
    String especialistaId = "1";
    String pacienteId = "paciente-existente";
    
    Especialista especialistaMock = new Especialista();
    especialistaMock.setId(especialistaId);
    especialistaMock.setPatients(new ArrayList<>(List.of("paciente-existente", "otro-paciente")));
    
    when(especialistaRepository.findById(especialistaId)).thenReturn(Optional.of(especialistaMock));

    boolean resultado = especialistaService.addPatient(especialistaId, pacienteId);

    assertThat(resultado).isFalse();

    verify(especialistaRepository).findById(especialistaId);
    verify(especialistaRepository, never()).save(any(Especialista.class));
    verifyNoInteractions(usuarioRepository);
}

// Test Case: Camino 1→1→2→3→4→6→F (Especialista no encontrado - devuelve lista vacía)
@Test
void getPatientsByEspecialistaId_WhenEspecialistaNotFound_ShouldReturnEmptyList() {
    String especialistaId = "999";
    
    when(especialistaRepository.findByEspecialistaId(especialistaId)).thenReturn(Optional.empty());

    List<Map<String, String>> resultado = especialistaService.getPatientsByEspecialistaId(especialistaId);

    assertThat(resultado).isNotNull();
    assertThat(resultado).isEmpty();

    verify(especialistaRepository).findByEspecialistaId(especialistaId);
    verifyNoInteractions(usuarioRepository);
}
// Test para getAllEspecialistas - Camino I→1→F
@Test
void getAllEspecialistas_ShouldReturnListOfEspecialistas() {
    Especialista especialista1 = new Especialista();
    especialista1.setId("1");
    especialista1.setName("Dr. Smith");
    
    Especialista especialista2 = new Especialista();
    especialista2.setId("2");
    especialista2.setName("Dr. Johnson");
    
    List<Especialista> especialistasMock = List.of(especialista1, especialista2);
    
    when(especialistaRepository.findAll()).thenReturn(especialistasMock);

    List<Especialista> resultado = especialistaService.getAllEspecialistas();

    assertThat(resultado).isNotNull();
    assertThat(resultado).hasSize(2);
    assertThat(resultado).containsExactly(especialista1, especialista2);

    verify(especialistaRepository).findAll();
    verifyNoInteractions(usuarioRepository);
}

// Test para getPatientsByEspecialistaId - Camino exitoso (Especialista encontrado con pacientes)
@Test
void getPatientsByEspecialistaId_WhenEspecialistaExistsWithPatients_ShouldReturnPatientsInfo() {
    String especialistaId = "1";
    
    Especialista especialistaMock = new Especialista();
    especialistaMock.setId(especialistaId);
    especialistaMock.setPatients(List.of("user1", "user2"));
    
    Usuario usuario1 = new Usuario();
    usuario1.setId("user1");
    usuario1.setNombre("Juan Perez");
    
    Usuario usuario2 = new Usuario();
    usuario2.setId("user2");
    usuario2.setNombre("Maria Garcia");
    
    when(especialistaRepository.findByEspecialistaId(especialistaId)).thenReturn(Optional.of(especialistaMock));
    when(usuarioRepository.findAllById(List.of("user1", "user2"))).thenReturn(List.of(usuario1, usuario2));

    List<Map<String, String>> resultado = especialistaService.getPatientsByEspecialistaId(especialistaId);

    assertThat(resultado).isNotNull();
    assertThat(resultado).hasSize(2);
    
    assertThat(resultado.get(0))
        .containsEntry("id", "user1")
        .containsEntry("nombre", "Juan Perez");
    
    assertThat(resultado.get(1))
        .containsEntry("id", "user2")
        .containsEntry("nombre", "Maria Garcia");

    verify(especialistaRepository).findByEspecialistaId(especialistaId);
    verify(usuarioRepository).findAllById(List.of("user1", "user2"));
}

// // Test para getPatientsByEspecialistaId - Especialista existe pero sin pacientes
// @Test
// void getPatientsByEspecialistaId_WhenEspecialistaExistsWithoutPatients_ShouldReturnEmptyList() {
//     // Arrange
//     String especialistaId = "1";
    
//     Especialista especialistaMock = new Especialista();
//     especialistaMock.setId(especialistaId);
//     especialistaMock.setPatients(new ArrayList<>()); // Lista vacía
    
//     when(especialistaRepository.findByEspecialistaId(especialistaId)).thenReturn(Optional.of(especialistaMock));

//     // Act
//     List<Map<String, String>> resultado = especialistaService.getPatientsByEspecialistaId(especialistaId);

//     // Assert
//     assertThat(resultado).isNotNull();
//     assertThat(resultado).isEmpty();

//     // Verify
//     verify(especialistaRepository).findByEspecialistaId(especialistaId);
//     verify(usuarioRepository, never()).findAllById(any());
// }

// // Test para getPatientsByEspecialistaId - Especialista existe pero usuarios no encontrados
// @Test
// void getPatientsByEspecialistaId_WhenEspecialistaExistsButUsersNotFound_ShouldReturnEmptyPatientInfo() {
//     // Arrange
//     String especialistaId = "1";
    
//     Especialista especialistaMock = new Especialista();
//     especialistaMock.setId(especialistaId);
//     especialistaMock.setPatients(List.of("user1", "user2"));
    
//     when(especialistaRepository.findByEspecialistaId(especialistaId)).thenReturn(Optional.of(especialistaMock));
//     when(usuarioRepository.findAllById(List.of("user1", "user2"))).thenReturn(List.of()); // Usuarios no encontrados

//     // Act
//     List<Map<String, String>> resultado = especialistaService.getPatientsByEspecialistaId(especialistaId);

//     // Assert
//     assertThat(resultado).isNotNull();
//     assertThat(resultado).isEmpty(); // Lista vacía porque no se encontraron usuarios

//     // Verify
//     verify(especialistaRepository).findByEspecialistaId(especialistaId);
//     verify(usuarioRepository).findAllById(List.of("user1", "user2"));
// }
}