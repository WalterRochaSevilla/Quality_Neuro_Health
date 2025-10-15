package com.example.backend.model;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import static org.assertj.core.api.Assertions.*;

class ComentarioTest {

    @Test
    void constructorVacio() {
        Comentario comentario = new Comentario();
        
        assertThat(comentario).isNotNull();
    }

    @Test
    void constructorConParametros() {
        Publicacion publicacion = new Publicacion();
        Usuario usuario = new Usuario();
        String contenido = "Test content";
        String comentarioPadreId = "parent123";
        
        Comentario comentario = new Comentario(publicacion, usuario, contenido, comentarioPadreId);
        
        assertThat(comentario.getPublicacion()).isEqualTo(publicacion);
        assertThat(comentario.getUsuario()).isEqualTo(usuario);
        assertThat(comentario.getContenido()).isEqualTo(contenido);
        assertThat(comentario.getComentarioPadreId()).isEqualTo(comentarioPadreId);
        assertThat(comentario.getFechaComentario()).isNotNull();
    }

    @Test
    void getIdAndSetId() {
        Comentario comentario = new Comentario();
        
        comentario.setId("test-id");
        
        assertThat(comentario.getId()).isEqualTo("test-id");
    }

    @Test
    void getPublicacionAndSetPublicacion() {
        Comentario comentario = new Comentario();
        Publicacion publicacion = new Publicacion();
        
        comentario.setPublicacion(publicacion);
        
        assertThat(comentario.getPublicacion()).isEqualTo(publicacion);
    }

    @Test
    void getUsuarioAndSetUsuario() {
        Comentario comentario = new Comentario();
        Usuario usuario = new Usuario();
        
        comentario.setUsuario(usuario);
        
        assertThat(comentario.getUsuario()).isEqualTo(usuario);
    }

    @Test
    void getContenidoAndSetContenido() {
        Comentario comentario = new Comentario();
        
        comentario.setContenido("Test content");
        
        assertThat(comentario.getContenido()).isEqualTo("Test content");
    }

    @Test
    void getFechaComentarioAndSetFechaComentario() {
        Comentario comentario = new Comentario();
        Instant fecha = Instant.now();
        
        comentario.setFechaComentario(fecha);
        
        assertThat(comentario.getFechaComentario()).isEqualTo(fecha);
    }

    @Test
    void getComentarioPadreIdAndSetComentarioPadreId() {
        Comentario comentario = new Comentario();
        
        comentario.setComentarioPadreId("parent123");
        
        assertThat(comentario.getComentarioPadreId()).isEqualTo("parent123");
    }
}