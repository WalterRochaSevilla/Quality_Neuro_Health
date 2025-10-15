package com.example.backend.service;

import com.example.backend.model.Usuario;
import com.example.backend.repository.UsuarioRepository;
import com.example.backend.exception.UsuarioAlreadyExistsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    // Test Case: Camino I→1→3→F (Usuario ya existe - lanza excepción)
    @Test
    void registrarUsuario_WhenEmailAlreadyExists_ShouldThrowUsuarioAlreadyExistsException() {
        String email = "carlos@example.com";
        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setEmail(email);
        
        when(usuarioRepository.findByEmail(email)).thenReturn(usuarioExistente);

        assertThatThrownBy(() -> 
            usuarioService.registrarUsuario("Carlos", "Lopez", email, "password123", "USER")
        )
        .isInstanceOf(UsuarioAlreadyExistsException.class)
        .hasMessage("El correo ya está registrado.");

        verify(usuarioRepository).findByEmail(email);
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }
     // Test Case: Camino I→1→2→F (Usuario no existe - se crea nuevo usuario)
    @Test
    void registrarUsuario_WhenEmailDoesNotExist_ShouldCreateNewUser() {
        String nombre = "Marlon";
        String apellido = "Garcia";
        String email = "marlon@example.com";
        String contrasena = "password123";
        String rol = "USER";
        
        when(usuarioRepository.findByEmail(email)).thenReturn(null);
        
        Usuario usuarioGuardado = new Usuario();
        usuarioGuardado.setId("1");
        usuarioGuardado.setNombre(nombre + " " + apellido);
        usuarioGuardado.setEmail(email);
        usuarioGuardado.setContrasena(contrasena);
        usuarioGuardado.setRol(rol);
        
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioGuardado);

        Usuario resultado = usuarioService.registrarUsuario(nombre, apellido, email, contrasena, rol);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getEmail()).isEqualTo("marlon@example.com");
        assertThat(resultado.getNombre()).isEqualTo("Marlon Garcia");

        verify(usuarioRepository).findByEmail("marlon@example.com");
        verify(usuarioRepository).save(any(Usuario.class));
    }

        // Test Case: Camino 1→1→2→4→F (Usuario no existe - retorna null)
    @Test
    void iniciarSesion_WhenUserNotFound_ShouldReturnNull() {
        String email = "usuario@example.com";
        String contrasena = "password123";
        
        when(usuarioRepository.findByEmail(email)).thenReturn(null);

        Usuario resultado = usuarioService.iniciarSesion(email, contrasena);

        assertThat(resultado).isNull();

        verify(usuarioRepository).findByEmail(email);
    }

        // Test Case: Camino 1→1→2→3→5→F (Usuario existe pero contraseña incorrecta - retorna null)
    @Test
    void iniciarSesion_WhenUserExistsButWrongPassword_ShouldReturnNull() {
        String email = "juan@example.com";
        String contrasenaCorrecta = "passwordCorrecta";
        String contrasenaIncorrecta = "cualquierOtra";
        
        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setEmail(email);
        usuarioExistente.setContrasena(contrasenaCorrecta);
        
        when(usuarioRepository.findByEmail(email)).thenReturn(usuarioExistente);

        Usuario resultado = usuarioService.iniciarSesion(email, contrasenaIncorrecta);

        assertThat(resultado).isNull();

        verify(usuarioRepository).findByEmail(email);
    }
        // Test Case: Camino I→1→2→3→4→F (Usuario existe y contraseña correcta - retorna usuario)
    @Test
    void iniciarSesion_WhenUserExistsAndCorrectPassword_ShouldReturnUser() {
        String email = "juan@example.com";
        String contrasenaCorrecta = "contraseñaCorrecta";
        
        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setEmail(email);
        usuarioExistente.setContrasena(contrasenaCorrecta);
        usuarioExistente.setNombre("Juan");
        usuarioExistente.setRol("USER");
        
        when(usuarioRepository.findByEmail(email)).thenReturn(usuarioExistente);

        Usuario resultado = usuarioService.iniciarSesion(email, contrasenaCorrecta);

        assertThat(resultado).isNotNull();
        assertThat(resultado).isEqualTo(usuarioExistente);
        assertThat(resultado.getEmail()).isEqualTo(email);
        assertThat(resultado.getContrasena()).isEqualTo(contrasenaCorrecta);

        verify(usuarioRepository).findByEmail(email);
    }
    
    @Test
    void obtenerUsuarioPorId_WhenUserExists_ShouldReturnUser() {
        String usuarioId = "1";
        Usuario usuarioMock = new Usuario();
        usuarioMock.setId(usuarioId);
        usuarioMock.setNombre("Juan Perez");
        usuarioMock.setEmail("juan@example.com");
        
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuarioMock));

        Usuario resultado = usuarioService.obtenerUsuarioPorId(usuarioId);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(usuarioId);
        assertThat(resultado.getNombre()).isEqualTo("Juan Perez");
        
        verify(usuarioRepository).findById(usuarioId);
    }
    
    @Test
    void obtenerTodos_WhenUsersExist_ShouldReturnUserList() {
        // Arrange
        Usuario usuario1 = new Usuario();
        usuario1.setId("1");
        usuario1.setNombre("Juan Perez");
        usuario1.setEmail("juan@example.com");
        
        Usuario usuario2 = new Usuario();
        usuario2.setId("2");
        usuario2.setNombre("Maria Garcia");
        usuario2.setEmail("maria@example.com");
        
        List<Usuario> usuariosMock = Arrays.asList(usuario1, usuario2);
        
        when(usuarioRepository.findAll()).thenReturn(usuariosMock);

        // Act
        List<Usuario> resultado = usuarioService.obtenerTodos();

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado).hasSize(2);
        assertThat(resultado).containsExactly(usuario1, usuario2);
        
        // Verify
        verify(usuarioRepository).findAll();
    }

}