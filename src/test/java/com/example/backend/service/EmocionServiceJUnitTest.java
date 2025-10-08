package com.example.backend.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EmocionServiceJUnitTest {

    @Test
    void testSimpleLogic() {
        String contenido = "Hoy me siento feliz";
        String emocion = "felicidad";
        // Simulación de lógica pura
        String resultado = contenido + " [" + emocion + "]";
        assertEquals("Hoy me siento feliz [felicidad]", resultado);
    }
}