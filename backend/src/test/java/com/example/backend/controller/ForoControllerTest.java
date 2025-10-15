package com.example.backend.controller;

import com.example.backend.model.Publicacion;
import com.example.backend.model.Usuario;
import com.example.backend.service.ForoService;
import com.example.backend.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.*;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ForoControllerTest {

    @Mock
    private ForoService foroService;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private ForoController foroController;

    private Usuario mockUsuario;
    private Publicacion mockPublicacion;

    @BeforeEach
    void setUp() {
        mockUsuario = new Usuario();
        mockUsuario.setId("user001");
        mockUsuario.setNombre("Juan");
        mockUsuario.setApellido("Perez");

        mockPublicacion = new Publicacion();
        mockPublicacion.setId("pub001");
        mockPublicacion.setTitulo("Título de prueba");
        mockPublicacion.setContenido("Contenido de prueba");
        mockPublicacion.setTema("Tecnología");
        mockPublicacion.setUsuario(mockUsuario);
        mockPublicacion.setFechaPublicacion(java.time.Instant.now());

    }

    // ✅ Caso 1: Crear publicación exitosamente
    @Test
    void testCrearPublicacion_Exito() {
        Map<String, String> request = new HashMap<>();
        request.put("usuarioId", "user001");
        request.put("titulo", "Título de prueba");
        request.put("contenido", "Contenido de prueba");
        request.put("tema", "Tecnología");

        when(usuarioService.obtenerUsuarioPorId("user001")).thenReturn(mockUsuario);
        when(foroService.crearPublicacion("user001", "Título de prueba", "Contenido de prueba", "Tecnología"))
                .thenReturn(mockPublicacion);

        ResponseEntity<?> response = foroController.crearPublicacion(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);

        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("pub001", body.get("id"));
        assertEquals("Título de prueba", body.get("titulo"));
        assertEquals("Contenido de prueba", body.get("contenido"));
        assertEquals("Juan", body.get("usuario"));

        verify(usuarioService).obtenerUsuarioPorId("user001");
        verify(foroService).crearPublicacion("user001", "Título de prueba", "Contenido de prueba", "Tecnología");
    }

    // ⚠️ Caso 2: Usuario no encontrado
    @Test
    void testCrearPublicacion_UsuarioNoEncontrado() {
        Map<String, String> request = new HashMap<>();
        request.put("usuarioId", "user999");
        request.put("titulo", "Título de prueba");
        request.put("contenido", "Contenido de prueba");
        request.put("tema", "Tecnología");

        when(usuarioService.obtenerUsuarioPorId("user999")).thenReturn(null);

        ResponseEntity<?> response = foroController.crearPublicacion(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Usuario no encontrado con ID: user999", response.getBody());

        verify(usuarioService).obtenerUsuarioPorId("user999");
        verifyNoInteractions(foroService);
    }

    // 💥 Caso 3: Error interno del servicio
    @Test
    void testCrearPublicacion_ErrorInterno() {
        Map<String, String> request = new HashMap<>();
        request.put("usuarioId", "user001");
        request.put("titulo", "Título error");
        request.put("contenido", "Contenido error");
        request.put("tema", "Pruebas");

        when(usuarioService.obtenerUsuarioPorId("user001")).thenReturn(mockUsuario);
        when(foroService.crearPublicacion(anyString(), anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("Falla en la base de datos"));

        ResponseEntity<?> response = foroController.crearPublicacion(request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Error al crear publicación"));

        verify(usuarioService).obtenerUsuarioPorId("user001");
        verify(foroService).crearPublicacion("user001", "Título error", "Contenido error", "Pruebas");
    }

    // 📋 Caso 4: Obtener todas las publicaciones
    @Test
    void testObtenerTodasPublicaciones() {
        List<Map<String, Object>> publicaciones = new ArrayList<>();
        Map<String, Object> pub1 = new HashMap<>();
        pub1.put("titulo", "Post 1");
        pub1.put("tema", "Tecnología");
        publicaciones.add(pub1);

        when(foroService.obtenerTodasPublicaciones()).thenReturn(publicaciones);

        ResponseEntity<List<Map<String, Object>>> response = foroController.obtenerTodasPublicaciones();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Post 1", response.getBody().get(0).get("titulo"));

        verify(foroService).obtenerTodasPublicaciones();
    }
}
