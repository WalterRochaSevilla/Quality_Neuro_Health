package com.example.backend.service;

import com.example.backend.model.Emocion;
import com.example.backend.model.Usuario;
import com.example.backend.repository.EmocionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.*;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class EmocionServiceJUnitPureTest {

    static class FakeUsuarioService extends UsuarioService {
        private final Map<String, Usuario> usuarios = new HashMap<>();

        public void agregarUsuario(Usuario usuario) {
            usuarios.put(usuario.getId(), usuario);
        }

        @Override
        public Usuario obtenerUsuarioPorId(String id) {
            Usuario usuario = usuarios.get(id);
            if (usuario == null) {
                throw new RuntimeException("Usuario no encontrado: " + id);
            }
            return usuario;
        }
    }

    static class FakeEmocionRepository implements EmocionRepository {
        private final Map<String, Emocion> emociones = new HashMap<>();
        private final Map<String, Emocion> emocionesPorUsuario = new HashMap<>();

        @Override
        public Optional<Emocion> findByUsuario_Id(String usuarioId) {
            return Optional.ofNullable(emocionesPorUsuario.get(usuarioId));
        }

        @Override
        public <S extends Emocion> S save(S emocion) {
            // DEBUG: Ver qué estamos guardando
            System.out.println("DEBUG: Guardando emoción - ID: " + emocion.getId() + 
                             ", Usuario ID: " + (emocion.getUsuario() != null ? emocion.getUsuario().getId() : "null"));
            
            if (emocion.getId() == null) {
                emocion.setId(UUID.randomUUID().toString());
            }
            
            emociones.put(emocion.getId(), emocion);
            if (emocion.getUsuario() != null) {
                emocionesPorUsuario.put(emocion.getUsuario().getId(), emocion);
            }
            
            return emocion;
        }

        // Implementaciones básicas para los otros métodos
        @Override public Optional<Emocion> findById(String id) { return Optional.ofNullable(emociones.get(id)); }
        @Override public boolean existsById(String id) { return emociones.containsKey(id); }
        @Override public List<Emocion> findAll() { return new ArrayList<>(emociones.values()); }
        @Override public List<Emocion> findAllById(Iterable<String> ids) {
            List<Emocion> result = new ArrayList<>();
            for (String id : ids) findById(id).ifPresent(result::add);
            return result;
        }
        @Override public long count() { return emociones.size(); }
        @Override public void deleteById(String id) { 
            Emocion emocion = emociones.get(id);
            if (emocion != null && emocion.getUsuario() != null) {
                emocionesPorUsuario.remove(emocion.getUsuario().getId());
            }
            emociones.remove(id); 
        }
        @Override public void delete(Emocion entity) { deleteById(entity.getId()); }
        @Override public void deleteAllById(Iterable<? extends String> ids) { for (String id : ids) deleteById(id); }
        @Override public void deleteAll(Iterable<? extends Emocion> entities) { for (Emocion entity : entities) delete(entity); }
        @Override public void deleteAll() { emociones.clear(); emocionesPorUsuario.clear(); }
        @Override public <S extends Emocion> List<S> saveAll(Iterable<S> entities) { 
            List<S> result = new ArrayList<>(); for (S entity : entities) result.add(save(entity)); return result; 
        }
        @Override public List<Emocion> findAll(Sort sort) { return findAll(); }
        @Override public Page<Emocion> findAll(Pageable pageable) { return new org.springframework.data.domain.PageImpl<>(findAll()); }
        @Override public <S extends Emocion> Optional<S> findOne(Example<S> example) { return Optional.empty(); }
        @Override public <S extends Emocion> List<S> findAll(Example<S> example) { return Collections.emptyList(); }
        @Override public <S extends Emocion> List<S> findAll(Example<S> example, Sort sort) { return Collections.emptyList(); }
        @Override public <S extends Emocion> Page<S> findAll(Example<S> example, Pageable pageable) { return new org.springframework.data.domain.PageImpl<>(Collections.emptyList()); }
        @Override public <S extends Emocion> long count(Example<S> example) { return 0; }
        @Override public <S extends Emocion> boolean exists(Example<S> example) { return false; }
        @Override public <S extends Emocion, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) { return null; }
        @Override public <S extends Emocion> S insert(S entity) { return save(entity); }
        @Override public <S extends Emocion> List<S> insert(Iterable<S> entities) { return saveAll(entities); }
    }

    @Test
    void testEscribirEnDiarioUnitario() {
        // Arrange
        FakeUsuarioService usuarioService = new FakeUsuarioService();
        FakeEmocionRepository emocionRepository = new FakeEmocionRepository();
        EmocionService emocionService = new EmocionService(emocionRepository, usuarioService);

        // Crear usuario completo
        Usuario usuario = new Usuario();
        usuario.setId("userSpring");
        usuario.setNombre("Test User");
        // Agrega otros campos necesarios según tu modelo de Usuario
        usuarioService.agregarUsuario(usuario);

        // DEBUG
        System.out.println("DEBUG: Antes de llamar escribirEnDiario");

        // Act
        Map<String, Object> respuesta = emocionService.escribirEnDiario(
            "userSpring", 
            "nota desde test unitario spring", 
            "alegre"
        );

        // DEBUG: Ver toda la respuesta
        System.out.println("DEBUG: Respuesta completa: " + respuesta);

        // Assert con más detalles
        assertNotNull(respuesta, "La respuesta completa no debería ser null");
        
        if (respuesta != null) {
            for (Map.Entry<String, Object> entry : respuesta.entrySet()) {
                System.out.println("DEBUG: " + entry.getKey() + " = " + entry.getValue());
            }
            
            assertNotNull(respuesta.get("id"), "El campo 'id' no debería ser null");
            assertEquals("userSpring", respuesta.get("usuarioId"), "El campo 'usuarioId' debería coincidir");
        }
    }

    // Test adicional para debuggear
    @Test
    void testServicioDirectamente() {
        // Arrange
        FakeUsuarioService usuarioService = new FakeUsuarioService();
        FakeEmocionRepository emocionRepository = new FakeEmocionRepository();
        EmocionService emocionService = new EmocionService(emocionRepository, usuarioService);

        Usuario usuario = new Usuario();
        usuario.setId("debugUser");
        usuario.setNombre("Debug User");
        usuarioService.agregarUsuario(usuario);

        // Verificar que el usuario se guardó correctamente
        Usuario usuarioRecuperado = usuarioService.obtenerUsuarioPorId("debugUser");
        assertNotNull(usuarioRecuperado, "El usuario debería existir en el servicio");

        // Act & Assert paso a paso
        try {
            Map<String, Object> respuesta = emocionService.escribirEnDiario("debugUser", "test debug", "alegre");
            System.out.println("DEBUG - Respuesta: " + respuesta);
            
            if (respuesta == null) {
                fail("El servicio retornó null completamente");
            } else {
                assertNotNull(respuesta.get("id"), "ID es null en: " + respuesta);
            }
        } catch (Exception e) {
            System.out.println("DEBUG - Excepción: " + e.getMessage());
            e.printStackTrace();
            fail("El método lanzó una excepción: " + e.getMessage());
        }
    }
}