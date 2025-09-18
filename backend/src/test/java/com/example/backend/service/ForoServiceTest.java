package com.example.backend.service;

import com.example.backend.model.Publicacion;
import com.example.backend.model.Usuario;
import com.example.backend.repository.ComentarioRepository;
import com.example.backend.repository.PublicacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ForoServiceTest {

    @Mock
    private PublicacionRepository publicacionRepository;

    @Mock
    private ComentarioRepository comentarioRepository;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private ForoService foroService;

    private Usuario mockUsuario;

    @BeforeEach
    void setUp() {
        mockUsuario = new Usuario();
        mockUsuario.setId("67d64c8fedc9ad56eedd10a9");
        mockUsuario.setNombre("Juan");
        mockUsuario.setApellido("Perez");
    }

    @Test
    void testCrearPublicacion() {
        // Datos de entrada
        String usuarioId = "67d64c8fedc9ad56eedd10a9";
        String titulo = "Título de prueba";
        String contenido = "Contenido de prueba";
        String tema = "Tecnología";

        when(usuarioService.obtenerUsuarioPorId(usuarioId)).thenReturn(mockUsuario);
        Publicacion publicacionMock = new Publicacion(mockUsuario, titulo, contenido, tema);
        when(publicacionRepository.save(any(Publicacion.class))).thenReturn(publicacionMock);
        Publicacion resultado = foroService.crearPublicacion(usuarioId, titulo, contenido, tema);
        assertNotNull(resultado);
        assertEquals(titulo, resultado.getTitulo());
        assertEquals(contenido, resultado.getContenido());
        assertEquals(tema, resultado.getTema());
        assertEquals(mockUsuario, resultado.getUsuario());

        verify(usuarioService).obtenerUsuarioPorId(usuarioId);
        verify(publicacionRepository).save(any(Publicacion.class));
    }
}
