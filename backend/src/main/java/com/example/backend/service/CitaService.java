package com.example.backend.service;

import com.example.backend.model.Cita;
import com.example.backend.model.Usuario;
import com.example.backend.repository.CitaRepository;
import com.example.backend.repository.EspecialistaRepository;
import com.example.backend.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CitaService {
    
    private static final Logger logger = LoggerFactory.getLogger(CitaService.class);
    
    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EspecialistaRepository especialistaRepository;

    @Autowired
    private RecordatorioService reminderService;

    @Autowired
    private EmailService emailService;

    public Cita crearCita(String usuarioId, String especialistaId, String fecha, String hora) {
        // Verificamos si el usuario existe
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + usuarioId));

        // Verificamos si el especialista existe (sin asignar a variable innecesaria)
        especialistaRepository.findByEspecialistaId(especialistaId)
                .orElseThrow(() -> new RuntimeException("Especialista no encontrado con ID: " + especialistaId));

        // Creamos y guardamos la cita
        Cita cita = new Cita(usuarioId, especialistaId, fecha, hora);
        Cita savedCita = citaRepository.save(cita);

        // Programar recordatorio
        reminderService.scheduleReminderForAppointment(savedCita);

        // Enviar confirmación por email
        enviarConfirmacionCita(usuario, especialistaId, fecha, hora, savedCita.getId());

        return savedCita;
    }

    private void enviarConfirmacionCita(Usuario usuario, String especialistaId, String fecha, String hora, String citaId) {
        try {
            Usuario especialistaUsuario = usuarioRepository.findById(especialistaId).orElse(null);

            String subject = "NeuroHealth - Confirmación de cita";
            String body = "<html>" +
                    "<body style='font-family: Arial, sans-serif;'>" +
                    "<h2 style='color: #2c3e50;'>Hola " + usuario.getNombre() + ",</h2>" +
                    "<p>Has agendado una cita con éxito. Aquí están los detalles:</p>" +
                    "<div style='background-color: #f8f9fa; padding: 15px; border-left: 4px solid #3498db; margin: 10px 0;'>" +
                    "<p><strong>Fecha:</strong> " + fecha + "</p>" +
                    "<p><strong>Hora:</strong> " + hora + "</p>" +
                    "<p><strong>Especialista:</strong> " +
                    (especialistaUsuario != null ? especialistaUsuario.getNombre() + " " + especialistaUsuario.getApellido() : "") + "</p>" +
                    "</div>" +
                    "<p>Recibirás un recordatorio el día de tu cita.</p>" +
                    "<p>Si necesitas cancelar o reprogramar, por favor contáctanos con anticipación.</p>" +
                    "<br>" +
                    "<p>Saludos,</p>" +
                    "<p><strong>Equipo NeuroHealth</strong></p>" +
                    "</body>" +
                    "</html>";

            emailService.sendEmail(usuario.getEmail(), subject, body);
        } catch (Exception e) {
            logger.error("Error enviando correo de confirmación para cita ID: {}", citaId, e);
        }
    }

    public List<Map<String, Object>> obtenerCitas() {
        return citaRepository.findAll().stream()
                .map(this::mapearCitaConDetalles)
                .collect(Collectors.toList());
    }

    public List<Cita> obtenerCitasPorUsuario(String usuarioId) {
        return citaRepository.findByUsuarioId(usuarioId);
    }

    public List<Map<String, Object>> obtenerCitasPorEspecialista(String especialistaId) {
        return citaRepository.findByEspecialistaId(especialistaId).stream()
                .map(this::mapearCitaBasica)
                .collect(Collectors.toList());
    }

    // Método reutilizable para mapear cita con detalles completos
    private Map<String, Object> mapearCitaConDetalles(Cita cita) {
        Map<String, Object> response = mapearCitaBasica(cita);
        
        Optional<Usuario> especialista = usuarioRepository.findById(cita.getEspecialistaId());
        String espName = especialista.map(Usuario::getNombre).orElse("");
        String espApellido = especialista.map(Usuario::getApellido).orElse("");
        response.put("especialistaNombre", espName + " " + espApellido);
        response.put("estado", "Activo");

        return response;
    }

    // Método reutilizable para mapear información básica de la cita
    private Map<String, Object> mapearCitaBasica(Cita cita) {
        Map<String, Object> response = new HashMap<>();
        response.put("especialistaId", cita.getEspecialistaId());
        response.put("fecha", cita.getFecha());
        response.put("hora", cita.getHora());
        response.put("id", cita.getId());
        response.put("usuarioId", cita.getUsuarioId());

        Optional<Usuario> user = usuarioRepository.findById(cita.getUsuarioId());
        String nombre = user.map(Usuario::getNombre).orElse("");
        String apellido = user.map(Usuario::getApellido).orElse("");
        response.put(cita.getEspecialistaId() != null ? "usuario" : "pacienteNombre", 
                    nombre + " " + apellido);

        return response;
    }
}