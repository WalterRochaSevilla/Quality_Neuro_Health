package com.example.backend.model;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import static org.assertj.core.api.Assertions.*;

class PublicacionTest {

    @Test
    void constructorVacio() {
        Publicacion publicacion = new Publicacion();
        
        assertThat(publicacion).isNotNull();
        assertThat(publicacion.getComentarioIds()).isEmpty();
    }

    @Test
    void constructorConParametros() {
        Usuario usuario = new Usuario();
        String titulo = "Mi primera publicación";
        String contenido = "Contenido de la publicación";
        String tema = "Salud Mental";
        
        Publicacion publicacion = new Publicacion(usuario, titulo, contenido, tema);
        
        assertThat(publicacion.getUsuario()).isEqualTo(usuario);
        assertThat(publicacion.getTitulo()).isEqualTo(titulo);
        assertThat(publicacion.getContenido()).isEqualTo(contenido);
        assertThat(publicacion.getTema()).isEqualTo(tema);
        assertThat(publicacion.getFechaPublicacion()).isNotNull();
        assertThat(publicacion.getComentarioIds()).isEmpty();
    }

    @Test
    void getIdAndSetId() {
        Publicacion publicacion = new Publicacion();
        
        publicacion.setId("test-id");
        
        assertThat(publicacion.getId()).isEqualTo("test-id");
    }

    @Test
    void getUsuarioAndSetUsuario() {
        Publicacion publicacion = new Publicacion();
        Usuario usuario = new Usuario();
        
        publicacion.setUsuario(usuario);
        assertThat(publicacion.getUsuario()).isEqualTo(usuario);
    }

    @Test
    void getTituloAndSetTitulo() {
        Publicacion publicacion = new Publicacion();
        
        publicacion.setTitulo("Nuevo título");
        
        assertThat(publicacion.getTitulo()).isEqualTo("Nuevo título");
    }

    @Test
    void getContenidoAndSetContenido() {
        Publicacion publicacion = new Publicacion();
        
        publicacion.setContenido("Nuevo contenido");
        
        assertThat(publicacion.getContenido()).isEqualTo("Nuevo contenido");
    }

    @Test
    void getFechaPublicacionAndSetFechaPublicacion() {
        Publicacion publicacion = new Publicacion();
        Instant fecha = Instant.now();
        
        publicacion.setFechaPublicacion(fecha);
        
        assertThat(publicacion.getFechaPublicacion()).isEqualTo(fecha);
    }

    @Test
    void getTemaAndSetTema() {
        Publicacion publicacion = new Publicacion();
        
        publicacion.setTema("Ansiedad");
        
        assertThat(publicacion.getTema()).isEqualTo("Ansiedad");
    }

    @Test
    void getComentarioIdsAndSetComentarioIds() {
        Publicacion publicacion = new Publicacion();
        List<String> comentarioIds = Arrays.asList("com1", "com2", "com3");
        
        publicacion.setComentarioIds(comentarioIds);
        
        assertThat(publicacion.getComentarioIds()).isEqualTo(comentarioIds);
    }
}