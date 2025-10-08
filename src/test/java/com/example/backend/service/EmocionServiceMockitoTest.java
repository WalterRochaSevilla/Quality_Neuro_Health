package com.example.backend.service;

import com.example.backend.model.Emocion;
import com.example.backend.model.Usuario;
import com.example.backend.repository.EmocionRepository;
import com.example.backend.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class EmocionServiceMockitoTest {

    @Mock
    private EmocionRepository emocionRepository;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private EmocionService emocionService;

    @Test
    void testEscribirEnDiarioUsuarioNoEncontrado() {
        MockitoAnnotations.openMocks(this);
        when(usuarioService.obtenerUsuarioPorId(anyString())).thenReturn(null);

        assertThrows(RuntimeException.class, () -> {
            emocionService.escribirEnDiario("no-existe", "contenido", "feliz");
        });
    }

    @Test
    void testEscribirEnDiarioUsuarioEncontrado() {
        MockitoAnnotations.openMocks(this);
        Usuario usuario = new Usuario(); // Completa según tu modelo
        usuario.setId("user1");
        when(usuarioService.obtenerUsuarioPorId("user1")).thenReturn(usuario);

        Emocion emocion = new Emocion(usuario);
        when(emocionRepository.findByUsuario_Id("user1")).thenReturn(Optional.of(emocion));
        when(emocionRepository.save(any(Emocion.class))).thenReturn(emocion);

        var respuesta = emocionService.escribirEnDiario("user1", "contenido", "feliz");
        assertEquals("user1", respuesta.get("usuarioId"));
    }
}