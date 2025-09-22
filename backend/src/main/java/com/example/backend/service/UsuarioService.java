package com.example.backend.service;

import com.example.backend.model.Usuario;
import com.example.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.backend.exception.UsuarioAlreadyExistsException;

import java.util.List;
import java.time.Instant;
import java.util.Optional;


@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario registrarUsuario(String nombre, String apellido, String email, String contrasena, String rol) {
        if (usuarioRepository.findByEmail(email) != null) {
            throw new UsuarioAlreadyExistsException("El correo ya está registrado.");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(nombre + " " + apellido); // Concatenar nombre y apellido
        usuario.setEmail(email);
        usuario.setContrasena(contrasena);
        usuario.setFechaRegistro(Instant.now().toString());
        usuario.setRol(rol);

        return usuarioRepository.save(usuario);
    }

    public Usuario iniciarSesion(String email, String contrasena) {
        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario != null && usuario.getContrasena().equals(contrasena)) {
            return usuario;
        }
        return null;
    }

    public Usuario obtenerUsuarioPorId(String id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        return usuario.orElse(null);
    }

}