package com.example.backend.service;

import com.example.backend.model.Usuario;
import com.example.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void testObtenerUsuarioPorId_usuarioExiste() {
        // Preparar datos
        String id = "67d64c8fedc9ad56eedd10a9";
        Usuario mockUsuario = new Usuario();
        mockUsuario.setId(id);
        mockUsuario.setNombre("Juan");

        when(usuarioRepository.findById(id)).thenReturn(Optional.of(mockUsuario));

        // Ejecutar
        Usuario resultado = usuarioService.obtenerUsuarioPorId(id);

        // Verificar
        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
        assertEquals("Juan", resultado.getNombre());

        verify(usuarioRepository).findById(id);
    }

    @Test
    void testObtenerUsuarioPorId_usuarioNoExiste() {
        // Preparar datos
        String id = "noexiste123";

        when(usuarioRepository.findById(id)).thenReturn(Optional.empty());

        // Ejecutar
        Usuario resultado = usuarioService.obtenerUsuarioPorId(id);

        // Verificar
        assertNull(resultado);
        verify(usuarioRepository).findById(id);
    }
}
