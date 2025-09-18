package com.example.backend.repository;

import com.example.backend.model.Comentario;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ComentarioRepository extends MongoRepository<Comentario, String> {
    List<Comentario> findByPublicacionId(String publicacionId);
    List<Comentario> findByComentarioPadreId(String comentarioPadreId);
    List<Comentario> findByUsuarioId(String usuarioId);
}
