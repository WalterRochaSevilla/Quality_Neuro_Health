package com.example.backend.controller;

import com.example.backend.service.ExamenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/examen")
public class ExamenController {
    @Autowired
    private ExamenService examenService;

    @PostMapping("aniadir")
    public ResponseEntity<?> escribirEnDiario(@RequestBody Map<String, String> request) {
        try {
            String usuarioId = request.get("usuarioId");
            String description = request.get("description");
            String name = request.get("name");
            String result = request.get("result");

            Map<String, Object> respuesta = examenService.aumentarExamen(usuarioId, description, name, result);
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/{usuarioId}")
    public ResponseEntity<?> obtenerDiarioPorUsuario(@PathVariable String usuarioId) {
        try {
            Map<String, Object> diario = examenService.obtenerExamenes(usuarioId);
            return ResponseEntity.ok(diario);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}