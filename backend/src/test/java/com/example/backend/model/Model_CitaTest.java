package com.example.backend.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class CitaTest {

    @Test
    void constructorVacio() {
        Cita cita = new Cita();
        
        assertThat(cita).isNotNull();
    }

    @Test
    void constructorConParametros() {
        Cita cita = new Cita("user1", "esp1", "2024-01-15", "10:30");
        
        assertThat(cita.getUsuarioId()).isEqualTo("user1");
        assertThat(cita.getEspecialistaId()).isEqualTo("esp1");
        assertThat(cita.getFecha()).isEqualTo("2024-01-15");
        assertThat(cita.getHora()).isEqualTo("10:30");
    }

    @Test
    void getIdAndSetId() {
        Cita cita = new Cita();
        
        cita.setId("test-id");
        
        assertThat(cita.getId()).isEqualTo("test-id");
    }

    @Test
    void getUsuarioIdAndSetUsuarioId() {
        Cita cita = new Cita();
        
        cita.setUsuarioId("test-user");
        
        assertThat(cita.getUsuarioId()).isEqualTo("test-user");
    }

    @Test
    void getEspecialistaIdAndSetEspecialistaId() {
        Cita cita = new Cita();
        
        cita.setEspecialistaId("test-esp");
        
        assertThat(cita.getEspecialistaId()).isEqualTo("test-esp");
    }

    @Test
    void getFechaAndSetFecha() {
        Cita cita = new Cita();
        
        cita.setFecha("2024-01-15");
        
        assertThat(cita.getFecha()).isEqualTo("2024-01-15");
    }

    @Test
    void getHoraAndSetHora() {
        Cita cita = new Cita();
        
        cita.setHora("14:30");
        
        assertThat(cita.getHora()).isEqualTo("14:30");
    }
}