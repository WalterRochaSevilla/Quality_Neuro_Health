package com.example.backend.controller;

import com.example.backend.model.Usuario;
import com.example.backend.service.UsuarioService;
import com.example.backend.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

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

            try {
                emailService.sendEmail(nuevoUsuario.getEmail(),
                        "Bienvenido a NeuroHealth",
                        "<h1>Hola " + nuevoUsuario.getNombre() + "!</h1><p>Tu cuenta ha sido creada con éxito.</p>");
            } catch (Exception e) {
                // Si el correo falla, el usuario aún se registra
                System.err.println("Error al enviar el correo: " + e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    @PostMapping("/login")
    public ResponseEntity<Usuario> iniciarSesion(@RequestParam String email, @RequestParam String contrasena) {
        Usuario user = usuarioService.iniciarSesion(email, contrasena);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // Retorna un 401 si no se encuentra el usuario
        }

        return ResponseEntity.ok(user); // Retorna el usuario si es válido
    }

}

