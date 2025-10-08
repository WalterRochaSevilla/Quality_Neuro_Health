package com.example.backend.service;

import com.example.backend.model.Emocion;
import com.example.backend.repository.EmocionRepository;
import org.springframework.data.domain.*;
import org.springframework.data.repository.query.FluentQuery;

import java.util.*;
import java.util.function.Function;

public class FakeEmocionRepository implements EmocionRepository {
    private final Map<String, Emocion> emociones = new HashMap<>();

    @Override
    public Optional<Emocion> findByUsuario_Id(String id) {
        return Optional.ofNullable(emociones.get(id));
    }

    @Override
    public <S extends Emocion> S save(S emocion) {
        if (emocion.getId() == null) {
            emocion.setId(UUID.randomUUID().toString());
        }
        emociones.put(emocion.getUsuario().getId(), emocion);
        return emocion;
    }

    @Override
    public <S extends Emocion> List<S> saveAll(Iterable<S> entities) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Emocion> findById(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean existsById(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Emocion> findAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Emocion> findAllById(Iterable<String> strings) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long count() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteById(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(Emocion entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteAllById(Iterable<? extends String> strings) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteAll(Iterable<? extends Emocion> entities) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteAll() {
        throw new UnsupportedOperationException();
    }

    // PagingAndSortingRepository
    @Override
    public List<Emocion> findAll(Sort sort) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Page<Emocion> findAll(Pageable pageable) {
        throw new UnsupportedOperationException();
    }

    // QueryByExampleExecutor
    @Override
    public <S extends Emocion> Optional<S> findOne(Example<S> example) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <S extends Emocion> List<S> findAll(Example<S> example) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <S extends Emocion> List<S> findAll(Example<S> example, Sort sort) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <S extends Emocion> Page<S> findAll(Example<S> example, Pageable pageable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <S extends Emocion> long count(Example<S> example) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <S extends Emocion> boolean exists(Example<S> example) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <S extends Emocion, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        throw new UnsupportedOperationException();
    }

    // Métodos de MongoRepository
    @Override
    public <S extends Emocion> S insert(S entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <S extends Emocion> List<S> insert(Iterable<S> entities) {
        throw new UnsupportedOperationException();
    }
}