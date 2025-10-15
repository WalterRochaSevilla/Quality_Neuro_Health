package com.example.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.backend.exception.UsuarioNotFoundException;
import com.example.backend.model.Examen;
import com.example.backend.model.Usuario;
import com.example.backend.repository.ExamenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.assertj.core.api.InstanceOfAssertFactories;

import java.util.Optional;
import java.util.Map;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class ExamenServiceTest {

    @Mock
    private ExamenRepository examenRepository;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private ExamenService examenService;

    // Tests para aumentarExamen
    @Test
    void aumentarExamen_CuandoUsuarioIdEsNull_DeberiaLanzarExcepcion() {
        // Configuración - Camino: I -> 1 -> 2 -> 3 -> 4 -> F
        String usuarioId = null;
        when(usuarioService.obtenerUsuarioPorId(usuarioId))
            .thenReturn(null);

        // Ejecución y Verificación
        assertThatThrownBy(() -> 
            examenService.aumentarExamen(usuarioId, "Desc", "Nombre", "Resultado"))
            .isInstanceOf(UsuarioNotFoundException.class)
            .hasMessage("Usuario con ID null no encontrado");
    }

    @Test
    void aumentarExamen_CuandoUsuarioExisteYExamenExiste_DeberiaActualizarExamenExistente() {
        // Configuración - Camino: I -> 1 -> 2 -> 3 -> 5 -> 6 -> 8 -> F
        String usuarioId = "1";
        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        
        Examen examenExistente = new Examen(usuario);
        examenExistente.setId("exam-456");

        when(usuarioService.obtenerUsuarioPorId(usuarioId))
            .thenReturn(usuario);
        when(examenRepository.findByUsuario_Id(usuarioId))
            .thenReturn(Optional.of(examenExistente));
        when(examenRepository.save(any(Examen.class)))
            .thenReturn(examenExistente);

        // Ejecución
        Map<String, Object> resultado = examenService.aumentarExamen(
            usuarioId, "Nueva Desc", "Nuevo Nombre", "Nuevo Result");

        // Verificación
        assertThat(resultado)
            .containsKeys("id", "usuarioId")
            .containsEntry("usuarioId", usuarioId)
            .containsEntry("id", "exam-456");
    }

    @Test
    void aumentarExamen_CuandoUsuarioExisteYExamenNoExiste_DeberiaCrearNuevoExamen() {
        // Configuración - Camino: I -> 1 -> 2 -> 3 -> 5 -> 7 -> 8 -> F
        String usuarioId = "1";
        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        
        Examen nuevoExamen = new Examen(usuario);
        nuevoExamen.setId("exam-123");

        when(usuarioService.obtenerUsuarioPorId(usuarioId))
            .thenReturn(usuario);
        when(examenRepository.findByUsuario_Id(usuarioId))
            .thenReturn(Optional.empty());
        when(examenRepository.save(any(Examen.class)))
            .thenReturn(nuevoExamen);

        // Ejecución
        Map<String, Object> resultado = examenService.aumentarExamen(
            usuarioId, "Descripción", "Nombre", "Resultado");

        // Verificación
        assertThat(resultado)
            .containsKeys("id", "usuarioId")
            .containsEntry("usuarioId", usuarioId)
            .containsEntry("id", "exam-123");
    }

    // Tests para obtenerExamenes
    @Test
    void obtenerExamenes_CuandoExamenExiste_DeberiaRetornarExamenesOrdenados() {
        // Configuración - Camino: I -> 1 -> 2 -> 4 -> 5 -> 6 -> 7 -> F
        String usuarioId = "1";
        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        Examen examen = new Examen(usuario);
        examen.setId("exam-1");
        Examen.Examenes entrada1 = new Examen.Examenes("Descripción1", "Nombre1", "Resultado1");
        Examen.Examenes entrada2 = new Examen.Examenes("Descripción2", "Nombre2", "Resultado2");
        examen.getExamenes().add(entrada1);
        examen.getExamenes().add(entrada2);
        when(examenRepository.findByUsuario_Id(usuarioId))
                .thenReturn(Optional.of(examen));
        Map<String, Object> resultado = examenService.obtenerExamenes(usuarioId);
        assertThat(resultado).containsKeys("id", "usuarioId", "entries");
        assertThat(resultado).containsEntry("id", "exam-1");
        assertThat(resultado).containsEntry("usuarioId", usuarioId);
        assertThat(resultado.get("entries"))
                .isInstanceOf(List.class)
                .asInstanceOf(InstanceOfAssertFactories.LIST)
                .hasSize(2)
                .allSatisfy(entry -> {
                    assertThat(entry).isInstanceOf(Map.class);
                    assertThat((Map<String, Object>) entry).containsKeys("type", "description", "name", "result", "date");
                    assertThat((Map<String, Object>) entry).containsEntry("type", "exam");
                });
        assertThat(resultado.get("entries"))
                .asInstanceOf(InstanceOfAssertFactories.LIST)
                .extracting(entry -> ((Map<String, Object>) entry).get("name"))
                .containsExactlyInAnyOrder("Nombre1", "Nombre2");
    }

    @Test
    void obtenerExamenes_CuandoExamenNoExiste_DeberiaLanzarExcepcion() {
        // Configuración - Camino: I -> 1 -> 3 -> F
        String usuarioId="999";
        when(examenRepository.findByUsuario_Id(usuarioId))
                .thenReturn(Optional.empty());
        assertThatThrownBy(() -> examenService.obtenerExamenes(usuarioId))
                .isInstanceOf(UsuarioNotFoundException.class)
                .hasMessage("Exámenes del usuario con ID 999 no encontrados");
    }
}