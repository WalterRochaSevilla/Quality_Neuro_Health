package com.example.backend.repository;


import com.example.backend.model.Emocion;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface EmocionRepository extends MongoRepository<Emocion, String> {
    Optional<Emocion> findByUsuario_Id(String usuarioId);

}