package com.example.backend.service;

import com.example.backend.model.Emocion;
import com.example.backend.model.Usuario;
import com.example.backend.repository.EmocionRepository;
import com.example.backend.exception.UsuarioNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class EmocionServiceTest {

    @Test
    void shouldEscribirEnDiarioWhenUsuarioExisteAndNoDiario() {
        EmocionRepository emocionRepository = Mockito.mock(EmocionRepository.class);
        UsuarioService usuarioService = Mockito.mock(UsuarioService.class);

        Usuario usuario = new Usuario();
        usuario.setId("userU");
        Mockito.when(usuarioService.obtenerUsuarioPorId("userU")).thenReturn(usuario);

        // Simular que no existe diario previo
        Mockito.when(emocionRepository.findByUsuario_Id("userU")).thenReturn(Optional.empty());
        Mockito.when(emocionRepository.save(Mockito.any(Emocion.class))).thenAnswer(invocation -> {
            Emocion emocion = invocation.getArgument(0);
            emocion.setId(UUID.randomUUID().toString());
            return emocion;
        });

        EmocionService emocionService = new EmocionService(emocionRepository, usuarioService);

        var resultado = emocionService.escribirEnDiario("userU", "nota test", "alegre");

        assertThat(resultado)
            .isNotNull()
            .containsKeys("usuarioId", "id", "emocion", "nota");
        assertThat(resultado.get("usuarioId")).isEqualTo("userU");
        assertThat(resultado.get("id")).isNotNull();
        assertThat(resultado.get("emocion")).isEqualTo("alegre");
        assertThat(resultado.get("nota")).isEqualTo("nota test");
    }

    @Test
    void shouldThrowExceptionWhenUsuarioNoExiste() {
        EmocionRepository emocionRepository = Mockito.mock(EmocionRepository.class);
        UsuarioService usuarioService = Mockito.mock(UsuarioService.class);

        Mockito.when(usuarioService.obtenerUsuarioPorId("userX")).thenReturn(null);

        EmocionService emocionService = new EmocionService(emocionRepository, usuarioService);

        assertThatThrownBy(() ->
            emocionService.escribirEnDiario("userX", "nota", "alegre")
        ).isInstanceOf(UsuarioNotFoundException.class)
         .hasMessageContaining("Usuario con id userX no encontrado");
    }

    @Test
    void shouldEscribirEnDiarioWhenDiarioYaExiste() {
        EmocionRepository emocionRepository = Mockito.mock(EmocionRepository.class);
        UsuarioService usuarioService = Mockito.mock(UsuarioService.class);

        Usuario usuario = new Usuario();
        usuario.setId("userU");

        Emocion diarioExistente = new Emocion(usuario);
        diarioExistente.setId("abc123");

        Mockito.when(usuarioService.obtenerUsuarioPorId("userU")).thenReturn(usuario);
        Mockito.when(emocionRepository.findByUsuario_Id("userU")).thenReturn(Optional.of(diarioExistente));
        Mockito.when(emocionRepository.save(Mockito.any(Emocion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EmocionService emocionService = new EmocionService(emocionRepository, usuarioService);

        var resultado = emocionService.escribirEnDiario("userU", "nota test", "alegre");

        assertThat(resultado.get("id")).isEqualTo("abc123");
    }
}