package com.example.backend.model;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.List;
import static org.assertj.core.api.Assertions.*;

class EmocionTest {

    @Test
    void constructorVacio() {
        Emocion emocion = new Emocion();
        
        assertThat(emocion).isNotNull();
        assertThat(emocion.getListaDiario()).isEmpty();
    }

    @Test
    void constructorConUsuario() {
        Usuario usuario = new Usuario();
        
        Emocion emocion = new Emocion(usuario);
        
        assertThat(emocion.getUsuario()).isEqualTo(usuario);
        assertThat(emocion.getListaDiario()).isEmpty();
    }

    @Test
    void getIdAndSetId() {
        Emocion emocion = new Emocion();
        
        emocion.setId("test-id");
        
        assertThat(emocion.getId()).isEqualTo("test-id");
    }

    @Test
    void getUsuarioAndSetUsuario() {
        Emocion emocion = new Emocion();
        Usuario usuario = new Usuario();
        
        emocion.setUsuario(usuario);
        
        assertThat(emocion.getUsuario()).isEqualTo(usuario);
    }

    @Test
    void getListaDiario() {
        Emocion emocion = new Emocion();
        
        List<Emocion.ListaDiario> lista = emocion.getListaDiario();
        
        assertThat(lista).isNotNull();
        assertThat(lista).isEmpty();
    }

    @Test
    void setListaDiario() {
        Emocion emocion = new Emocion();
        String contenido = "Test content";
        String emocionTipo = "Feliz";
        
        emocion.setListaDiario(contenido, emocionTipo);
        
        assertThat(emocion.getListaDiario()).hasSize(1);
        Emocion.ListaDiario item = emocion.getListaDiario().get(0);
        assertThat(item.getType()).isEqualTo("emotion");
        assertThat(item.getEmocion()).isEqualTo(emocionTipo);
        assertThat(item.getContenido()).isEqualTo(contenido);
        assertThat(item.getFechaPublicacion()).isNotNull();
    }

    @Test
    void listaDiarioConstructor() {
        Emocion.ListaDiario listaDiario = new Emocion.ListaDiario("Test content", "Triste");
        
        assertThat(listaDiario.getType()).isEqualTo("emotion");
        assertThat(listaDiario.getEmocion()).isEqualTo("Triste");
        assertThat(listaDiario.getContenido()).isEqualTo("Test content");
        assertThat(listaDiario.getFechaPublicacion()).isNotNull();
    }

    @Test
    void listaDiarioGetters() {
        Emocion.ListaDiario listaDiario = new Emocion.ListaDiario("Content", "Enojado");
        
        assertThat(listaDiario.getType()).isEqualTo("emotion");
        assertThat(listaDiario.getEmocion()).isEqualTo("Enojado");
        assertThat(listaDiario.getContenido()).isEqualTo("Content");
        assertThat(listaDiario.getFechaPublicacion()).isNotNull();
    }
}