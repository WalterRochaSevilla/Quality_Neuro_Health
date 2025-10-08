package com.example.backend.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CitaTest {

    @Test
    void testConstructorYGetters() {
        Cita cita = new Cita("user1", "spec1", "2025-12-01", "09:00");
        assertEquals("user1", cita.getUsuarioId());
        assertEquals("spec1", cita.getEspecialistaId());
        assertEquals("2025-12-01", cita.getFecha());
        assertEquals("09:00", cita.getHora());
    }
}
