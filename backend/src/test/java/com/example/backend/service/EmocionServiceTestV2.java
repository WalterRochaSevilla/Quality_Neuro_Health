package com.example.backend.service;

import com.example.backend.model.Emocion;
import com.example.backend.model.Usuario;
import com.example.backend.repository.EmocionRepository;
import com.example.backend.exception.UsuarioNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class EmocionServiceTest {

    @Mock
    private EmocionRepository emocionRepository;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private EmocionService emocionService;

    // Test Case 1: Camino 1-2-3-F (Usuario no existe)
    @Test
    void escribirEnDiario_WhenUsuarioNotFound_ShouldThrowUsuarioNotFoundException() {
        String usuarioId = "999"; 
        String contenido = "Contenido de prueba";
        String emocion = "Feliz";
        
        when(usuarioService.obtenerUsuarioPorId(usuarioId)).thenReturn(null);

        assertThatThrownBy(() -> 
            emocionService.escribirEnDiario(usuarioId, contenido, emocion)
        )
        .isInstanceOf(UsuarioNotFoundException.class)
        .hasMessage("Usuario con id " + usuarioId + " no encontrado");

        verify(usuarioService).obtenerUsuarioPorId(usuarioId);
        verifyNoInteractions(emocionRepository);
    }

    // Test Case 2: Camino 1-1-2-4-F (Usuario existe, diario NO existe - se crea nuevo)
    @Test
    void escribirEnDiario_WhenUsuarioExistsAndNoDiario_ShouldCreateNewDiarioAndReturnResponse() {
        String usuarioId = "1"; 
        String contenido = "Contenido de prueba";
        String emocion = "Feliz";
        
        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        
        Emocion nuevoDiario = new Emocion(usuario);
        nuevoDiario.setId("nuevoDiarioId");
        
        when(usuarioService.obtenerUsuarioPorId(usuarioId)).thenReturn(usuario);
        when(emocionRepository.findByUsuario_Id(usuarioId)).thenReturn(Optional.empty());
        when(emocionRepository.save(any(Emocion.class))).thenReturn(nuevoDiario);

        Map<String, Object> resultado = emocionService.escribirEnDiario(usuarioId, contenido, emocion);

        assertThat(resultado).isNotNull();
        assertThat(resultado.get("id")).isEqualTo("nuevoDiarioId");
        assertThat(resultado.get("usuarioId")).isEqualTo(usuarioId);
        
        verify(usuarioService).obtenerUsuarioPorId(usuarioId);
        verify(emocionRepository).findByUsuario_Id(usuarioId);
        verify(emocionRepository, times(2)).save(any(Emocion.class));
    }

        // Test Case 4: Camino 1-3 (Diario no encontrado)
    @Test
    void obtenerDiarioCompleto_WhenDiarioNotFound_ShouldThrowRuntimeException() {
        String usuarioId = "999";
        
        when(emocionRepository.findByUsuario_Id(usuarioId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> 
            emocionService.obtenerDiarioCompleto(usuarioId)
        )
        .isInstanceOf(RuntimeException.class)
        .hasMessage("Diario no encontrado");

        verify(emocionRepository).findByUsuario_Id(usuarioId);
    }

        // Test Case 5: Camino 1-3 (Diario no encontrado)
    @Test
    void obtenerDiarioCompleto_HappyPath_WhenUsuarioHasDiarioWithEntries_ShouldReturnCompleteMap() {
        String usuarioId = "1";
        
        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        
        Emocion diario = new Emocion(usuario);
        diario.setId("diarioHappyPath123");
        
        diario.setListaDiario("Contenido de prueba Happy Path 1", "Feliz");
        diario.setListaDiario("Contenido de prueba Happy Path 2", "Triste");
        
        when(emocionRepository.findByUsuario_Id(usuarioId)).thenReturn(Optional.of(diario));

        Map<String, Object> resultado = emocionService.obtenerDiarioCompleto(usuarioId);

        assertThat(resultado).isNotNull();
        assertThat(resultado).containsKeys("id", "usuarioId", "entries");
        assertThat(resultado.get("id")).isEqualTo("diarioHappyPath123");
        assertThat(resultado.get("usuarioId")).isEqualTo(usuarioId);
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> entries = (List<Map<String, Object>>) resultado.get("entries");
        assertThat(entries).isNotNull().isNotEmpty();
        
        Map<String, Object> primeraEntrada = entries.get(0);
        assertThat(primeraEntrada).containsKeys("type", "emotion", "notes", "date");
        
        assertThat(primeraEntrada.get("type")).isNotNull();
        assertThat(primeraEntrada.get("emotion")).isNotNull();
        assertThat(primeraEntrada.get("notes")).isNotNull();
        assertThat(primeraEntrada.get("date")).isNotNull();

        // Verify - Verificar que se siguió el flujo correcto I→1→2→F
        verify(emocionRepository).findByUsuario_Id(usuarioId);
    }

    
}