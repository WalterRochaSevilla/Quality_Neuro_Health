package com.example.backend.service;

import com.example.backend.model.Comentario;
import com.example.backend.model.Publicacion;
import com.example.backend.model.Usuario;
import com.example.backend.repository.ComentarioRepository;
import com.example.backend.repository.PublicacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
public class ForoService {

    @Autowired
    private PublicacionRepository publicacionRepository;

    @Autowired
    private ComentarioRepository comentarioRepository;

    @Autowired
    private UsuarioService usuarioService;


    public Publicacion crearPublicacion(String usuarioId, String titulo, String contenido, String tema) {
        Usuario usuario = usuarioService.obtenerUsuarioPorId(usuarioId);
        Publicacion publicacion = new Publicacion(usuario, titulo, contenido, tema);
        return publicacionRepository.save(publicacion);
    }

    public List<Map<String, Object>> obtenerTodasPublicaciones() {
        return publicacionRepository.findByOrderByFechaPublicacionDesc()
                .stream()
                .map(p -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("id", p.getId());
                    response.put("titulo", p.getTitulo());
                    response.put("contenido", p.getContenido());
                    response.put("fechaPublicacion", p.getFechaPublicacion());
                    response.put("tema", p.getTema());

                    // Manejo profesional de nulos
                    String nombre = p.getUsuario().getNombre() != null ? p.getUsuario().getNombre() : "";
                    String apellido = p.getUsuario().getApellido() != null ? " " + p.getUsuario().getApellido() : "";
                    response.put("usuario", nombre + apellido);

                    return response;
                })
                .collect(Collectors.toList());
    }
    public Publicacion obtenerPublicacionPorId(String id) {
        return publicacionRepository.findById(id).orElse(null);
    }


    public Comentario crearComentario(Publicacion publicacion, Usuario usuario, String contenido, String comentarioPadreId) {
        Comentario comentario = new Comentario(publicacion, usuario, contenido, comentarioPadreId);
        Comentario comentarioGuardado = comentarioRepository.save(comentario);

        if (comentarioPadreId == null) {
            publicacion.getComentarioIds().add(comentarioGuardado.getId());
            publicacionRepository.save(publicacion);
        }

        return comentarioGuardado;
    }

}