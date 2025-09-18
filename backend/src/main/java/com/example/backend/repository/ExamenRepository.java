package com.example.backend.repository;


import com.example.backend.model.Examen;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface ExamenRepository extends MongoRepository<Examen, String> {
    Optional<Examen> findByUsuario_Id(String usuarioId);

}