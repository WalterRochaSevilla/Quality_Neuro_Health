package com.example.backend.repository;

import com.example.backend.model.Cita;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CitaRepository extends MongoRepository<Cita, String> {
    // Métodos adicionales si los necesitas
    List<Cita> findByUsuarioId(String usuarioId);
    List<Cita> findByEspecialistaId(String especialistaId);
}