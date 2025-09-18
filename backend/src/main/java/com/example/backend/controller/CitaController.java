package com.example.backend.controller;

import com.example.backend.model.Cita;
import com.example.backend.service.CitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/citas")
@CrossOrigin(origins = "*")
public class CitaController {


    @Autowired
    private CitaService citaService;

    // Crear una nueva cita
    @PostMapping
    public Cita crearCita(@RequestBody Map<String, String> datosCita) {
        String usuarioId = datosCita.get("usuarioId");
        String especialistaId = datosCita.get("especialistaId");
        String fecha = datosCita.get("fecha");
        String hora = datosCita.get("hora");

        return citaService.crearCita(usuarioId, especialistaId, fecha, hora);
    }

    // Obtener todas las citas
    @GetMapping
    public List<Map<String, Object>> obtenerCitas() {
        return citaService.obtenerCitas();
    }

    // Obtener citas de un usuario específico
    @GetMapping("/usuario/{usuarioId}")
    public List<Cita> obtenerCitasPorUsuario(@PathVariable String usuarioId) {
        return citaService.obtenerCitasPorUsuario(usuarioId);
    }

    // Obtener citas de un especialista específico
    @GetMapping("/especialista/{especialistaId}")
    public List<Map<String, Object>> obtenerCitasPorEspecialista(@PathVariable String especialistaId) {
        return citaService.obtenerCitasPorEspecialista(especialistaId);
    }
}