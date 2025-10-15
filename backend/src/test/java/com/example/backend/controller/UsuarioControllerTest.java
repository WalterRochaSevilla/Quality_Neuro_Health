package com.example.backend.controller;

import com.example.backend.model.Usuario;
import com.example.backend.service.UsuarioService;
import com.example.backend.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UsuarioController usuarioController;

    private Usuario mockUsuario;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockUsuario = new Usuario();
        mockUsuario.setId("1");
        mockUsuario.setNombre("Fernando");
        mockUsuario.setApellido("Rodriguez");
        mockUsuario.setEmail("fernando@example.com");
        mockUsuario.setContrasena("1234");
        mockUsuario.setRol("usuario");
    }

    // ✅ Caso exitoso: obtener lista de usuarios
    @Test
    void obtenerUsuarios_exitoso() {
        List<Usuario> usuarios = Arrays.asList(mockUsuario);
        when(usuarioService.obtenerTodos()).thenReturn(usuarios);

        List<Usuario> resultado = usuarioController.obtenerUsuarios();

        assertEquals(1, resultado.size());
        assertEquals("Fernando", resultado.get(0).getNombre());
        verify(usuarioService, times(1)).obtenerTodos();
    }

    // ✅ Caso exitoso: registrar usuario correctamente
    @Test
    void registrarUsuario_exitoso() throws Exception{
        Map<String, String> datos = new HashMap<>();
        datos.put("nombre", "Fernando");
        datos.put("apellido", "Rodriguez");
        datos.put("email", "fernando@example.com");
        datos.put("contrasena", "1234");

        when(usuarioService.registrarUsuario(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(mockUsuario);

        ResponseEntity<?> respuesta = usuarioController.registrarUsuario(datos);

        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Usuario);

        verify(usuarioService, times(1)).registrarUsuario(anyString(), anyString(), anyString(), anyString(), anyString());
        verify(emailService, times(1)).sendEmail(
                eq(mockUsuario.getEmail()),
                anyString(),
                contains("Hola Fernando")
        );
    }

    // ❌ Caso fallido: error en registro (por ejemplo, usuario duplicado)
    @Test
    void registrarUsuario_error() throws Exception {
        Map<String, String> datos = new HashMap<>();
        datos.put("nombre", "Fernando");
        datos.put("email", "fernando@example.com");

        // 👇 Usamos any() en lugar de anyString() para permitir valores null
        when(usuarioService.registrarUsuario(any(), any(), any(), any(), any()))
                .thenThrow(new RuntimeException("Email ya registrado"));

        ResponseEntity<?> respuesta = usuarioController.registrarUsuario(datos);

        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        assertEquals("Email ya registrado", respuesta.getBody());

        // Verificamos que no se intentó enviar correo
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    // ✅ Caso exitoso: login correcto
    @Test
    void iniciarSesion_exitoso() {
        when(usuarioService.iniciarSesion("fernando@example.com", "1234")).thenReturn(mockUsuario);

        ResponseEntity<Usuario> respuesta = usuarioController.iniciarSesion("fernando@example.com", "1234");

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(mockUsuario, respuesta.getBody());
        verify(usuarioService, times(1)).iniciarSesion("fernando@example.com", "1234");
    }

    // ❌ Caso fallido: credenciales incorrectas
    @Test
    void iniciarSesion_noAutorizado() {
        when(usuarioService.iniciarSesion("mal@example.com", "wrong")).thenReturn(null);

        ResponseEntity<Usuario> respuesta = usuarioController.iniciarSesion("mal@example.com", "wrong");

        assertEquals(HttpStatus.UNAUTHORIZED, respuesta.getStatusCode());
        assertNull(respuesta.getBody());
        verify(usuarioService, times(1)).iniciarSesion("mal@example.com", "wrong");
    }

    // ⚠️ Caso extra: error al enviar correo (no afecta creación)
    @Test
    void registrarUsuario_errorEnEnvioCorreo_noRompeFlujo() throws Exception {
        Map<String, String> datos = new HashMap<>();
        datos.put("nombre", "Fernando");
        datos.put("apellido", "Rodriguez");
        datos.put("email", "fernando@example.com");
        datos.put("contrasena", "1234");

        when(usuarioService.registrarUsuario(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(mockUsuario);

        doThrow(new RuntimeException("Error SMTP")).when(emailService)
                .sendEmail(anyString(), anyString(), anyString());

        ResponseEntity<?> respuesta = usuarioController.registrarUsuario(datos);

        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Usuario);
        verify(emailService, times(1)).sendEmail(anyString(), anyString(), anyString());
    }
    @Test
    void registrarUsuario_usuarioSinEmail_noEnviaCorreo() throws Exception {
        Map<String, String> datos = new HashMap<>();
        datos.put("nombre", "Fernando");
        datos.put("apellido", "Rodriguez");
        datos.put("email", null); // 👈 simulamos email nulo
        datos.put("contrasena", "1234");

        // Retornamos un usuario con email nulo
        Usuario usuarioSinEmail = new Usuario();
        usuarioSinEmail.setId("2");
        usuarioSinEmail.setNombre("Fernando");
        usuarioSinEmail.setEmail(null);

        when(usuarioService.registrarUsuario(any(), any(), any(), any(), any()))
                .thenReturn(usuarioSinEmail);

        ResponseEntity<?> respuesta = usuarioController.registrarUsuario(datos);

        // Debe retornar 201 CREATED igual, porque el catch no se activa
        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertTrue(respuesta.getBody() instanceof Usuario);

        // Pero no se debe intentar enviar el correo
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }
    // ⚠️ Caso: usuario nulo → no se envía correo y no lanza excepción
    @Test
    void registrarUsuario_usuarioNulo_noEnviaCorreo() throws Exception {
        Map<String, String> datos = new HashMap<>();
        datos.put("nombre", "Fernando");
        datos.put("apellido", "Rodriguez");
        datos.put("email", "fernando@example.com");
        datos.put("contrasena", "1234");

        // Simulamos que el servicio devuelve null
        when(usuarioService.registrarUsuario(any(), any(), any(), any(), any()))
                .thenReturn(null);

        ResponseEntity<?> respuesta = usuarioController.registrarUsuario(datos);

        // El método igual debería responder 201 (porque no lanza excepción)
        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertNull(respuesta.getBody()); // El usuario devuelto es null

        // Verificamos que no se intentó enviar correo
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }
}
