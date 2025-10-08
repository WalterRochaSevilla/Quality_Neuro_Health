package com.example.backend.controller;

import com.example.backend.model.Usuario;
import com.example.backend.service.UsuarioService;
import com.example.backend.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private EmailService emailService;

    @GetMapping
    public List<Usuario> obtenerUsuarios() {
        return usuarioService.obtenerTodos();
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registrarUsuario(@RequestBody Map<String, String> usuarioData) {
        try {
            String nombre = usuarioData.get("nombre");
            String apellido = usuarioData.get("apellido");
            String email = usuarioData.get("email");
            String contrasena = usuarioData.get("contrasena");
            String rol = usuarioData.getOrDefault("rol", "usuario");

            Usuario nuevoUsuario = usuarioService.registrarUsuario(nombre, apellido, email, contrasena, rol);

            // 🔹 Llamamos al nuevo método que maneja el envío de correo
            enviarCorreoBienvenida(nuevoUsuario);

            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 🔹 Método extraído para resolver el "nested try block"
    private void enviarCorreoBienvenida(Usuario usuario) {
        try {
            emailService.sendEmail(
                    usuario.getEmail(),
                    "Bienvenido a NeuroHealth",
                    "<h1>Hola " + usuario.getNombre() + "!</h1><p>Tu cuenta ha sido creada con éxito.</p>"
            );
        } catch (Exception e) {
            // 🔹 Reemplazo de System.err.println por logger
            logger.error("Error al enviar el correo: {}", e.getMessage(), e);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Usuario> iniciarSesion(@RequestParam String email, @RequestParam String contrasena) {
        Usuario user = usuarioService.iniciarSesion(email, contrasena);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        return ResponseEntity.ok(user);
    }

}
