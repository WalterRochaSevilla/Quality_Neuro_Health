// package com.example.backend.service;

// import com.example.backend.model.Emocion;
// import com.example.backend.model.Usuario;
// import com.example.backend.repository.EmocionRepository;
// import org.junit.jupiter.api.Test;

// import java.util.*;

// import static org.junit.jupiter.api.Assertions.*;

// class EmocionServiceSpringPureTest {

//     // Fake implementación de UsuarioService
//     static class FakeUsuarioService extends UsuarioService {
//         private final Map<String, Usuario> usuarios = new HashMap<>();
//         public void agregarUsuario(Usuario usuario) { usuarios.put(usuario.getId(), usuario); }
//         @Override
//         public Usuario obtenerUsuarioPorId(String id) { return usuarios.get(id); }

        
//     }

//     // Fake implementación de EmocionRepository
//     static class FakeEmocionRepository implements EmocionRepository {
//         private final Map<String, Emocion> emociones = new HashMap<>();
//         private final Map<String, Emocion> emocionesPorUsuario = new HashMap<>(); 

//         @Override public Optional<Emocion> findByUsuario_Id(String id) { return Optional.ofNullable(emociones.get(id)); }
//         @Override
//         public <S extends Emocion> S save(S emocion) {
//             // DEBUG: Ver qué estamos guardando
//             System.out.println("DEBUG - Guardando emoción:");
//             System.out.println("  - ID antes: " + emocion.getId());
//             System.out.println("  - Usuario: " + (emocion.getUsuario() != null ? emocion.getUsuario().getId() : "null"));
            
//             // Asigna ID si es null (IMPORTANTE)
//             if (emocion.getId() == null) {
//                 String nuevoId = UUID.randomUUID().toString();
//                 emocion.setId(nuevoId);
//                 System.out.println("  - ID asignado: " + nuevoId);
//             }
            
//             // Guarda en ambos mapas
//             emociones.put(emocion.getId(), emocion);
//             if (emocion.getUsuario() != null) {
//                 emocionesPorUsuario.put(emocion.getUsuario().getId(), emocion);
//             }
            
//             System.out.println("  - ID después: " + emocion.getId());
//             System.out.println("  - Total emociones: " + emociones.size());
            
//             return emocion;
//     }        // Métodos requeridos, igual que en tus otros fakes:
//         @Override public <S extends Emocion> List<S> saveAll(Iterable<S> entities) { throw new UnsupportedOperationException(); }
//         @Override public Optional<Emocion> findById(String s) { throw new UnsupportedOperationException(); }
//         @Override public boolean existsById(String s) { throw new UnsupportedOperationException(); }
//         @Override public List<Emocion> findAll() { throw new UnsupportedOperationException(); }
//         @Override public List<Emocion> findAllById(Iterable<String> strings) { throw new UnsupportedOperationException(); }
//         @Override public long count() { throw new UnsupportedOperationException(); }
//         @Override public void deleteById(String s) { throw new UnsupportedOperationException(); }
//         @Override public void delete(Emocion entity) { throw new UnsupportedOperationException(); }
//         @Override public void deleteAllById(Iterable<? extends String> strings) { throw new UnsupportedOperationException(); }
//         @Override public void deleteAll(Iterable<? extends Emocion> entities) { throw new UnsupportedOperationException(); }
//         @Override public void deleteAll() { throw new UnsupportedOperationException(); }
//         @Override public List<Emocion> findAll(org.springframework.data.domain.Sort sort) { throw new UnsupportedOperationException(); }
//         @Override public org.springframework.data.domain.Page<Emocion> findAll(org.springframework.data.domain.Pageable pageable) { throw new UnsupportedOperationException(); }
//         @Override public <S extends Emocion> Optional<S> findOne(org.springframework.data.domain.Example<S> example) { throw new UnsupportedOperationException(); }
//         @Override public <S extends Emocion> List<S> findAll(org.springframework.data.domain.Example<S> example) { throw new UnsupportedOperationException(); }
//         @Override public <S extends Emocion> List<S> findAll(org.springframework.data.domain.Example<S> example, org.springframework.data.domain.Sort sort) { throw new UnsupportedOperationException(); }
//         @Override public <S extends Emocion> org.springframework.data.domain.Page<S> findAll(org.springframework.data.domain.Example<S> example, org.springframework.data.domain.Pageable pageable) { throw new UnsupportedOperationException(); }
//         @Override public <S extends Emocion> long count(org.springframework.data.domain.Example<S> example) { throw new UnsupportedOperationException(); }
//         @Override public <S extends Emocion> boolean exists(org.springframework.data.domain.Example<S> example) { throw new UnsupportedOperationException(); }
//         @Override public <S extends Emocion, R> R findBy(org.springframework.data.domain.Example<S> example, java.util.function.Function<org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery<S>, R> queryFunction) { throw new UnsupportedOperationException(); }
//         @Override public <S extends Emocion> S insert(S entity) { throw new UnsupportedOperationException(); }
//         @Override public <S extends Emocion> List<S> insert(Iterable<S> entities) { throw new UnsupportedOperationException(); }
//     }

//     @Test
//     void testEscribirEnDiarioUnitario() {
//         FakeUsuarioService usuarioService = new FakeUsuarioService();
//         FakeEmocionRepository emocionRepository = new FakeEmocionRepository();
//         EmocionService emocionService = new EmocionService(emocionRepository, usuarioService);

//         Usuario usuario = new Usuario();
//         usuario.setId("userU");
//         usuarioService.agregarUsuario(usuario);

//         var respuesta = emocionService.escribirEnDiario("userU", "nota test", "alegre");

//         assertEquals("userU", respuesta.get("usuarioId"));
//         assertNotNull(respuesta.get("id"));
//     }
// }