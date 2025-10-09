// package com.example.backend.service;

// import com.example.backend.model.Usuario;
// import com.example.backend.repository.UsuarioRepository;
// import com.example.backend.exception.UsuarioAlreadyExistsException;
// import org.junit.jupiter.api.Test;
// import org.mockito.Mockito;

// import java.util.Arrays;
// import java.util.Optional;
// import java.util.List;

// import static org.assertj.core.api.Assertions.*;

// class UsuarioServiceTest1 {

//     @Test
//     void shouldReturnAllUsuarios() {
//         UsuarioRepository usuarioRepository = Mockito.mock(UsuarioRepository.class);
//         Usuario usuario1 = new Usuario();
//         usuario1.setId("id1");
//         Usuario usuario2 = new Usuario();
//         usuario2.setId("id2");
//         List<Usuario> usuarios = Arrays.asList(usuario1, usuario2);

//         Mockito.when(usuarioRepository.findAll()).thenReturn(usuarios);

//         UsuarioService usuarioService = new UsuarioService(usuarioRepository);

//         List<Usuario> resultado = usuarioService.obtenerTodos();

//         assertThat(resultado).hasSize(2).contains(usuario1, usuario2);
//     }

//     @Test
//     void shouldRegisterUsuarioWhenEmailNotExists() {
//         UsuarioRepository usuarioRepository = Mockito.mock(UsuarioRepository.class);
//         Mockito.when(usuarioRepository.findByEmail("nuevo@email.com")).thenReturn(null);

//         Usuario usuarioGuardado = new Usuario();
//         usuarioGuardado.setId("id3");
//         usuarioGuardado.setEmail("nuevo@email.com");
//         Mockito.when(usuarioRepository.save(Mockito.any(Usuario.class))).thenReturn(usuarioGuardado);

//         UsuarioService usuarioService = new UsuarioService(usuarioRepository);

//         Usuario resultado = usuarioService.registrarUsuario("Juan", "Perez", "nuevo@email.com", "1234", "usuario");

//         assertThat(resultado).isNotNull();
//         assertThat(resultado.getId()).isEqualTo("id3");
//         assertThat(resultado.getEmail()).isEqualTo("nuevo@email.com");
//         assertThat(resultado.getNombre()).isEqualTo("Juan Perez");
//         assertThat(resultado.getRol()).isEqualTo("usuario");
//         assertThat(resultado.getContrasena()).isEqualTo("1234");
//         assertThat(resultado.getFechaRegistro()).isNotNull();
//     }

//     @Test
//     void shouldThrowExceptionWhenRegisterUsuarioWithExistingEmail() {
//         UsuarioRepository usuarioRepository = Mockito.mock(UsuarioRepository.class);
//         Usuario existente = new Usuario();
//         existente.setEmail("existe@email.com");
//         Mockito.when(usuarioRepository.findByEmail("existe@email.com")).thenReturn(existente);

//         UsuarioService usuarioService = new UsuarioService(usuarioRepository);

//         assertThatThrownBy(() ->
//             usuarioService.registrarUsuario("Ana", "Lopez", "existe@email.com", "1234", "usuario")
//         ).isInstanceOf(UsuarioAlreadyExistsException.class)
//          .hasMessageContaining("El correo ya está registrado.");
//     }

//     @Test
//     void shouldLoginSuccessfullyWithCorrectCredentials() {
//         UsuarioRepository usuarioRepository = Mockito.mock(UsuarioRepository.class);
//         Usuario usuario = new Usuario();
//         usuario.setEmail("test@email.com");
//         usuario.setContrasena("pass123");
//         Mockito.when(usuarioRepository.findByEmail("test@email.com")).thenReturn(usuario);

//         UsuarioService usuarioService = new UsuarioService(usuarioRepository);

//         Usuario resultado = usuarioService.iniciarSesion("test@email.com", "pass123");

//         assertThat(resultado).isEqualTo(usuario);
//     }

//     @Test
//     void shouldReturnNullWhenLoginWithIncorrectPassword() {
//         UsuarioRepository usuarioRepository = Mockito.mock(UsuarioRepository.class);
//         Usuario usuario = new Usuario();
//         usuario.setEmail("test@email.com");
//         usuario.setContrasena("pass123");
//         Mockito.when(usuarioRepository.findByEmail("test@email.com")).thenReturn(usuario);

//         UsuarioService usuarioService = new UsuarioService(usuarioRepository);

//         Usuario resultado = usuarioService.iniciarSesion("test@email.com", "wrongpass");

//         assertThat(resultado).isNull();
//     }

//     @Test
//     void shouldReturnNullWhenLoginWithNonexistentEmail() {
//         UsuarioRepository usuarioRepository = Mockito.mock(UsuarioRepository.class);
//         Mockito.when(usuarioRepository.findByEmail("noexiste@email.com")).thenReturn(null);

//         UsuarioService usuarioService = new UsuarioService(usuarioRepository);

//         Usuario resultado = usuarioService.iniciarSesion("noexiste@email.com", "1234");

//         assertThat(resultado).isNull();
//     }

//     @Test
//     void shouldReturnUsuarioWhenIdExists() {
//         UsuarioRepository usuarioRepository = Mockito.mock(UsuarioRepository.class);
//         Usuario usuario = new Usuario();
//         usuario.setId("id123");
//         Mockito.when(usuarioRepository.findById("id123")).thenReturn(Optional.of(usuario));

//         UsuarioService usuarioService = new UsuarioService(usuarioRepository);

//         Usuario resultado = usuarioService.obtenerUsuarioPorId("id123");

//         assertThat(resultado).isEqualTo(usuario);
//     }

//     @Test
//     void shouldReturnNullWhenIdDoesNotExist() {
//         UsuarioRepository usuarioRepository = Mockito.mock(UsuarioRepository.class);
//         Mockito.when(usuarioRepository.findById("noexiste")).thenReturn(Optional.empty());

//         UsuarioService usuarioService = new UsuarioService(usuarioRepository);

//         Usuario resultado = usuarioService.obtenerUsuarioPorId("noexiste");

//         assertThat(resultado).isNull();
//     }

//     @Test
//     void shouldGuardarUsuarioDirectly() {
//         // Solo si tienes este método en tu clase
//         UsuarioRepository usuarioRepository = Mockito.mock(UsuarioRepository.class);
//         Usuario usuario = new Usuario();
//         usuario.setId("id999");
//         Mockito.when(usuarioRepository.save(usuario)).thenReturn(usuario);

//         UsuarioService usuarioService = new UsuarioService(usuarioRepository);

//         Usuario resultado = usuarioService.guardarUsuario(usuario);

//         assertThat(resultado).isEqualTo(usuario);
//     }
//     @Test
//     void shouldCreateUsuarioWithAllProperties() {
//         // Arrange & Act
//         Usuario usuario = new Usuario();
//         usuario.setId("test-id");
//         usuario.setNombre("Test User");
//         usuario.setApellido("Apellido Test");
//         usuario.setEmail("test@email.com");
//         usuario.setContrasena("password123");
//         usuario.setFechaRegistro("2023-01-01T00:00:00Z");
//         usuario.setRol("admin");

//         // Assert
//         assertThat(usuario.getId()).isEqualTo("test-id");
//         assertThat(usuario.getNombre()).isEqualTo("Test User");
//         assertThat(usuario.getApellido()).isEqualTo("Apellido Test");
//         assertThat(usuario.getEmail()).isEqualTo("test@email.com");
//         assertThat(usuario.getContrasena()).isEqualTo("password123");
//         assertThat(usuario.getFechaRegistro()).isEqualTo("2023-01-01T00:00:00Z");
//         assertThat(usuario.getRol()).isEqualTo("admin");
//     }

//     @Test
//     void shouldCreateUsuarioWithConstructor() {
//         // Arrange & Act
//         Usuario usuario = new Usuario("Juan", "Perez", "juan@email.com", "pass123", "user");

//         // Assert
//         assertThat(usuario.getNombre()).isEqualTo("Juan");
//         assertThat(usuario.getApellido()).isEqualTo("Perez");
//         assertThat(usuario.getEmail()).isEqualTo("juan@email.com");
//         assertThat(usuario.getContrasena()).isEqualTo("pass123");
//     }

//     @Test
//     void shouldHandleNullValuesInUsuario() {
//         // Arrange & Act
//         Usuario usuario = new Usuario();
//         usuario.setNombre(null);
//         usuario.setEmail(null);
//         usuario.setContrasena(null);
//         usuario.setRol(null);

//         // Assert
//         assertThat(usuario.getNombre()).isNull();
//         assertThat(usuario.getEmail()).isNull();
//         assertThat(usuario.getContrasena()).isNull();
//         assertThat(usuario.getRol()).isNull();
//     }
// }

