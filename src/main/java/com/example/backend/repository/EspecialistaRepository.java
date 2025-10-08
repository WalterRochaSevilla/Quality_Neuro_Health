package com.example.backend.repository;

import com.example.backend.model.Especialista;
import com.example.backend.model.Usuario;
import java.util.Optional;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EspecialistaRepository extends MongoRepository<Especialista, String> {
    Usuario getEspecialistaById(String id);
    Optional<Especialista> findByEspecialistaId(String especialistaId);
}