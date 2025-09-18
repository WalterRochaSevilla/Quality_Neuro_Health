package com.example.backend.controller;

import com.example.backend.model.Especialista;
import com.example.backend.model.Usuario;
import com.example.backend.repository.EspecialistaRepository;
import com.example.backend.service.EspecialistaService;
import com.example.backend.service.EmailService;
import com.example.backend.service.UsuarioService;
import com.example.backend.service.CitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/especialistas")
public class EspecialistaController {

    @Autowired
    private EspecialistaService especialistaService;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private CitaService citaService;
    @Autowired
    private EspecialistaRepository especialistaRepository;

    // Obtener todos los especialistas
    @GetMapping
    public ResponseEntity<List<Especialista>> getAllEspecialistas() {
        return ResponseEntity.ok(especialistaService.getAllEspecialistas());
    }

    // Obtener horarios de un especialista por ID
    @GetMapping("/{id}/horarios")
    public ResponseEntity<Map<String, List<String>>> getHorariosByEspecialistaId(
            @PathVariable String id,
            @RequestParam String fecha) {
        return ResponseEntity.ok(especialistaService.getHorariosByEspecialistaId(id, fecha));
    }


    @PutMapping("/{id}/ocupar-hora")
    public ResponseEntity<Map> ocuparHora(@PathVariable String id, @RequestBody Map<String, String> body) {
        String hour = body.get("hour");
        String fecha = body.get("fecha");
        String userId = body.get("userId");


        Usuario usuario = usuarioService.obtenerUsuarioPorId(userId);
        Optional<Especialista> doctor = especialistaRepository.findById(id);

        boolean success = especialistaService.addOccupiedHour(id, hour, fecha);
        especialistaService.addPatient(id, userId);
        System.out.println(doctor.get().getEspecialistaId());
        citaService.crearCita(userId, doctor.get().getEspecialistaId(), fecha, hour);
        if (success) {
            try {
                emailService.sendEmail(usuario.getEmail(),
                        "Cita Confirmada",
                        "<h1>Hola " + usuario.getNombre() + "!</h1><p>Tu cita con " + doctor.get().getName() + " a las " + hour +
                                " el día " + fecha + " fue confirmada.</p>");
            } catch (Exception e) {
                System.err.println("Error al enviar el correo: " + e.getMessage());
            }
            return ResponseEntity.ok(Map.of("message", "Hora ocupada con éxito"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("message", "No se pudo ocupar la hora"));
        }
    }

    @GetMapping("/{id}/patients")
    public List<Map<String, String>> getPatientsByEspecialistaId(@PathVariable("id") String especialistaId) {
        return especialistaService.getPatientsByEspecialistaId(especialistaId);
    }

}
