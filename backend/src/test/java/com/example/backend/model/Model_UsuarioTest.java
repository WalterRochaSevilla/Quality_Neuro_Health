package com.example.backend.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class UsuarioTest {

    @Test
    void constructorVacio() {
        Usuario usuario = new Usuario();
        
        assertThat(usuario).isNotNull();
    }

    @Test
    void constructorCompleto() {
        String nombre = "Juan";
        String apellido = "Perez";
        String email = "juan@example.com";
        String contrasena = "password123";
        String fechaRegistro = "2024-01-15";
        String rol = "USER";
        
        Usuario usuario = new Usuario(nombre, apellido, email, contrasena, fechaRegistro, rol);
        
        assertThat(usuario.getNombre()).isEqualTo(nombre);
        assertThat(usuario.getApellido()).isEqualTo(apellido);
        assertThat(usuario.getEmail()).isEqualTo(email);
        assertThat(usuario.getContrasena()).isEqualTo(contrasena);
        assertThat(usuario.getFechaRegistro()).isEqualTo(fechaRegistro);
        assertThat(usuario.getRol()).isEqualTo(rol);
    }

    @Test
    void constructorEmailContrasena() {
        String email = "test@example.com";
        String contrasena = "testpass";
        
        Usuario usuario = new Usuario(email, contrasena);
        
        assertThat(usuario.getEmail()).isEqualTo(email);
        assertThat(usuario.getContrasena()).isEqualTo(contrasena);
        assertThat(usuario.getNombre()).isNull();
        assertThat(usuario.getApellido()).isNull();
        assertThat(usuario.getFechaRegistro()).isNull();
        assertThat(usuario.getRol()).isNull();
    }

    @Test
    void getIdAndSetId() {
        Usuario usuario = new Usuario();
        
        usuario.setId("user123");
        
        assertThat(usuario.getId()).isEqualTo("user123");
    }

    @Test
    void getNombreAndSetNombre() {
        Usuario usuario = new Usuario();
        
        usuario.setNombre("Maria");
        
        assertThat(usuario.getNombre()).isEqualTo("Maria");
    }

    @Test
    void getApellidoAndSetApellido() {
        Usuario usuario = new Usuario();
        
        usuario.setApellido("Garcia");
        
        assertThat(usuario.getApellido()).isEqualTo("Garcia");
    }

    @Test
    void getEmailAndSetEmail() {
        Usuario usuario = new Usuario();
        
        usuario.setEmail("test@example.com");
        
        assertThat(usuario.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void getContrasenaAndSetContrasena() {
        Usuario usuario = new Usuario();
        
        usuario.setContrasena("newpassword");
        
        assertThat(usuario.getContrasena()).isEqualTo("newpassword");
    }

    @Test
    void getFechaRegistroAndSetFechaRegistro() {
        Usuario usuario = new Usuario();
        
        usuario.setFechaRegistro("2024-01-15");
        
        assertThat(usuario.getFechaRegistro()).isEqualTo("2024-01-15");
    }

    @Test
    void getRolAndSetRol() {
        Usuario usuario = new Usuario();
        
        usuario.setRol("ADMIN");
        
        assertThat(usuario.getRol()).isEqualTo("ADMIN");
    }
}