package com.example.backend.service;

import com.example.backend.model.Usuario;
import com.example.backend.repository.UsuarioRepository;
import com.example.backend.exception.UsuarioAlreadyExistsException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

class UsuarioServiceTest1 {

    @Test
    void shouldReturnAllUsuarios() {
        // Arrange
        UsuarioRepository usuarioRepository = Mockito.mock(UsuarioRepository.class);
        Usuario usuario1 = new Usuario();
        usuario1.setId("id1");
        Usuario usuario2 = new Usuario();
        usuario2.setId("id2");
        List<Usuario> usuarios = Arrays.asList(usuario1, usuario2);

        Mockito.when(usuarioRepository.findAll()).thenReturn(usuarios);

        // Usar ReflectionTestUtils para inyectar dependencias
        UsuarioService usuarioService = new UsuarioService();
        ReflectionTestUtils.setField(usuarioService, "usuarioRepository", usuarioRepository);

        // Act
        List<Usuario> resultado = usuarioService.obtenerTodos();

        // Assert
        assertThat(resultado).hasSize(2).contains(usuario1, usuario2);
    }

    @Test
    void shouldRegisterUsuarioWhenEmailNotExists() {
        // Arrange
        UsuarioRepository usuarioRepository = Mockito.mock(UsuarioRepository.class);
        Mockito.when(usuarioRepository.findByEmail("nuevo@email.com")).thenReturn(null);

        Usuario usuarioGuardado = new Usuario();
        usuarioGuardado.setId("id3");
        usuarioGuardado.setEmail("nuevo@email.com");
        Mockito.when(usuarioRepository.save(Mockito.any(Usuario.class))).thenReturn(usuarioGuardado);

        UsuarioService usuarioService = new UsuarioService();
        ReflectionTestUtils.setField(usuarioService, "usuarioRepository", usuarioRepository);

        // Act
        Usuario resultado = usuarioService.registrarUsuario("Juan", "Perez", "nuevo@email.com", "1234", "usuario");

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo("id3");
        assertThat(resultado.getEmail()).isEqualTo("nuevo@email.com");
        assertThat(resultado.getNombre()).isEqualTo("Juan Perez");
        assertThat(resultado.getRol()).isEqualTo("usuario");
        assertThat(resultado.getContrasena()).isEqualTo("1234");
        assertThat(resultado.getFechaRegistro()).isNotNull();
    }

    @Test
    void shouldThrowExceptionWhenRegisterUsuarioWithExistingEmail() {
        // Arrange
        UsuarioRepository usuarioRepository = Mockito.mock(UsuarioRepository.class);
        Usuario existente = new Usuario();
        existente.setEmail("existe@email.com");
        Mockito.when(usuarioRepository.findByEmail("existe@email.com")).thenReturn(existente);

        UsuarioService usuarioService = new UsuarioService();
        ReflectionTestUtils.setField(usuarioService, "usuarioRepository", usuarioRepository);

        // Act & Assert
        assertThatThrownBy(() ->
            usuarioService.registrarUsuario("Ana", "Lopez", "existe@email.com", "1234", "usuario")
        ).isInstanceOf(UsuarioAlreadyExistsException.class)
         .hasMessageContaining("El correo ya está registrado.");
    }

    @Test
    void shouldLoginSuccessfullyWithCorrectCredentials() {
        // Arrange
        UsuarioRepository usuarioRepository = Mockito.mock(UsuarioRepository.class);
        Usuario usuario = new Usuario();
        usuario.setEmail("test@email.com");
        usuario.setContrasena("pass123");
        Mockito.when(usuarioRepository.findByEmail("test@email.com")).thenReturn(usuario);

        UsuarioService usuarioService = new UsuarioService();
        ReflectionTestUtils.setField(usuarioService, "usuarioRepository", usuarioRepository);

        // Act
        Usuario resultado = usuarioService.iniciarSesion("test@email.com", "pass123");

        // Assert
        assertThat(resultado).isEqualTo(usuario);
    }

    @Test
    void shouldReturnNullWhenLoginWithIncorrectPassword() {
        // Arrange
        UsuarioRepository usuarioRepository = Mockito.mock(UsuarioRepository.class);
        Usuario usuario = new Usuario();
        usuario.setEmail("test@email.com");
        usuario.setContrasena("pass123");
        Mockito.when(usuarioRepository.findByEmail("test@email.com")).thenReturn(usuario);

        UsuarioService usuarioService = new UsuarioService();
        ReflectionTestUtils.setField(usuarioService, "usuarioRepository", usuarioRepository);

        // Act
        Usuario resultado = usuarioService.iniciarSesion("test@email.com", "wrongpass");

        // Assert
        assertThat(resultado).isNull();
    }

    @Test
    void shouldReturnNullWhenLoginWithNonexistentEmail() {
        // Arrange
        UsuarioRepository usuarioRepository = Mockito.mock(UsuarioRepository.class);
        Mockito.when(usuarioRepository.findByEmail("noexiste@email.com")).thenReturn(null);

        UsuarioService usuarioService = new UsuarioService();
        ReflectionTestUtils.setField(usuarioService, "usuarioRepository", usuarioRepository);

        // Act
        Usuario resultado = usuarioService.iniciarSesion("noexiste@email.com", "1234");

        // Assert
        assertThat(resultado).isNull();
    }

    @Test
    void shouldReturnUsuarioWhenIdExists() {
        // Arrange
        UsuarioRepository usuarioRepository = Mockito.mock(UsuarioRepository.class);
        Usuario usuario = new Usuario();
        usuario.setId("id123");
        Mockito.when(usuarioRepository.findById("id123")).thenReturn(Optional.of(usuario));

        UsuarioService usuarioService = new UsuarioService();
        ReflectionTestUtils.setField(usuarioService, "usuarioRepository", usuarioRepository);

        // Act
        Usuario resultado = usuarioService.obtenerUsuarioPorId("id123");

        // Assert
        assertThat(resultado).isEqualTo(usuario);
    }

    @Test
    void shouldReturnNullWhenIdDoesNotExist() {
        // Arrange
        UsuarioRepository usuarioRepository = Mockito.mock(UsuarioRepository.class);
        Mockito.when(usuarioRepository.findById("noexiste")).thenReturn(Optional.empty());

        UsuarioService usuarioService = new UsuarioService();
        ReflectionTestUtils.setField(usuarioService, "usuarioRepository", usuarioRepository);

        // Act
        Usuario resultado = usuarioService.obtenerUsuarioPorId("noexiste");

        // Assert
        assertThat(resultado).isNull();
    }

    // ELIMINAR esta prueba si el método guardarUsuario no existe
    // @Test
    // void shouldGuardarUsuarioDirectly() {
    //     // Solo si tienes este método en tu clase
    //     UsuarioRepository usuarioRepository = Mockito.mock(UsuarioRepository.class);
    //     Usuario usuario = new Usuario();
    //     usuario.setId("id999");
    //     Mockito.when(usuarioRepository.save(usuario)).thenReturn(usuario);
    // 
    //     UsuarioService usuarioService = new UsuarioService();
    //     ReflectionTestUtils.setField(usuarioService, "usuarioRepository", usuarioRepository);
    // 
    //     Usuario resultado = usuarioService.guardarUsuario(usuario);
    // 
    //     assertThat(resultado).isEqualTo(usuario);
    // }
}