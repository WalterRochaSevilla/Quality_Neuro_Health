package com.example.backend.repository;

import com.example.backend.model.Publicacion;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface PublicacionRepository extends MongoRepository<Publicacion, String> {
    List<Publicacion> findByUsuarioId(String usuarioId);
    List<Publicacion> findByTema(String tema);
    List<Publicacion> findByOrderByFechaPublicacionDesc();
}

