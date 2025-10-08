// package com.example.backend.service;

// import com.example.backend.model.Usuario;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;

// import java.util.Map;

// import static org.junit.jupiter.api.Assertions.*;

// @SpringBootTest
// class EmocionServiceSpringTest {

//     @Autowired
//     private EmocionService emocionService;

//     @Test
//     void testEscribirEnDiarioIntegrado() {
//         // Este test requiere que la base de datos y los otros servicios estén configurados correctamente
//         // Simula una llamada real
//         Usuario usuario = new Usuario();
//         usuario.setId("user1");
//         // Asegúrate de que user1 exista realmente para que no falle
//         Map<String, Object> respuesta = emocionService.escribirEnDiario("user1", "contenido de prueba", "feliz");
//         assertEquals("user1", respuesta.get("usuarioId"));
//     }
// }
