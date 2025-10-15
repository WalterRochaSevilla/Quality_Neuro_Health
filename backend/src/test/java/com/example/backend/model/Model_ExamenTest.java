package com.example.backend.model;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.List;
import static org.assertj.core.api.Assertions.*;

class ExamenTest {

    @Test
    void constructorVacio() {
        Examen examen = new Examen();
        
        assertThat(examen).isNotNull();
        assertThat(examen.getExamenes()).isEmpty();
    }

    @Test
    void constructorConUsuario() {
        Usuario usuario = new Usuario();
        
        Examen examen = new Examen(usuario);
        
        assertThat(examen.getUsuario()).isEqualTo(usuario);
        assertThat(examen.getExamenes()).isEmpty();
    }

    @Test
    void getIdAndSetId() {
        Examen examen = new Examen();
        
        examen.setId("test-id");
        
        assertThat(examen.getId()).isEqualTo("test-id");
    }

    @Test
    void getUsuarioAndSetUsuario() {
        Examen examen = new Examen();
        Usuario usuario = new Usuario();
        
        examen.setUsuario(usuario);
        
        assertThat(examen.getUsuario()).isEqualTo(usuario);
    }

    @Test
    void getExamenes() {
        Examen examen = new Examen();
        
        List<Examen.Examenes> examenes = examen.getExamenes();
        
        assertThat(examenes).isNotNull();
        assertThat(examenes).isEmpty();
    }

    @Test
    void setExamenes() {
        Examen examen = new Examen();
        String description = "Examen de sangre";
        String name = "Hemograma";
        String result = "Normal";
        
        examen.setExamenes(description, name, result);
        
        assertThat(examen.getExamenes()).hasSize(1);
        Examen.Examenes item = examen.getExamenes().get(0);
        assertThat(item.getType()).isEqualTo("exam");
        assertThat(item.getName()).isEqualTo(name);
        assertThat(item.getDescription()).isEqualTo(description);
        assertThat(item.getResult()).isEqualTo(result);
        assertThat(item.getFechaPublicacion()).isNotNull();
    }

    @Test
    void examenesConstructor() {
        Examen.Examenes examenes = new Examen.Examenes("Examen completo", "Checkup", "Aprobado");
        
        assertThat(examenes.getType()).isEqualTo("exam");
        assertThat(examenes.getName()).isEqualTo("Checkup");
        assertThat(examenes.getDescription()).isEqualTo("Examen completo");
        assertThat(examenes.getResult()).isEqualTo("Aprobado");
        assertThat(examenes.getFechaPublicacion()).isNotNull();
    }

    @Test
    void examenesGetters() {
        Examen.Examenes examenes = new Examen.Examenes("Descripción", "Nombre", "Resultado");
        
        assertThat(examenes.getType()).isEqualTo("exam");
        assertThat(examenes.getName()).isEqualTo("Nombre");
        assertThat(examenes.getDescription()).isEqualTo("Descripción");
        assertThat(examenes.getResult()).isEqualTo("Resultado");
        assertThat(examenes.getFechaPublicacion()).isNotNull();
    }
}