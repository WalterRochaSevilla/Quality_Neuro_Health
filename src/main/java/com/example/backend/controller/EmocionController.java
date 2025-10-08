package com.example.backend.controller;

import com.example.backend.service.EmocionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/diario")
public class EmocionController {
    @Autowired
    private EmocionService emocionService;

    @PostMapping("aniadir")
    public ResponseEntity<?> escribirEnDiario(@RequestBody Map<String, String> request) {
        try {
            String usuarioId = request.get("usuarioId");
            String contenido = request.get("contenido");
            String emocion = request.get("emocion");

            Map<String, Object> respuesta = emocionService.escribirEnDiario(usuarioId, contenido, emocion);
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/{usuarioId}")
    public ResponseEntity<?> obtenerDiarioPorUsuario(@PathVariable String usuarioId) {
        try {
            Map<String, Object> diario = emocionService.obtenerDiarioCompleto(usuarioId);
            return ResponseEntity.ok(diario);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}