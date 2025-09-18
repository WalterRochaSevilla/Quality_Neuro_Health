//package com.example.backend.service;
//
//import com.example.backend.model.Cita;
//import com.example.backend.model.Especialista;
//import com.example.backend.model.Usuario;
//import com.example.backend.repository.CitaRepository;
//import com.example.backend.repository.EspecialistaRepository;
//import com.example.backend.repository.UsuarioRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//@Service
//public class CitaService {
//    @Autowired
//    private CitaRepository citaRepository;
//
//    @Autowired
//    private UsuarioRepository usuarioRepository;
//
//    @Autowired
//    private EspecialistaRepository especialistaRepository;
//
//    public Cita crearCita(String usuarioId, String especialistaId, String fecha, String hora) {
//        // Verificamos si el usuario existe
//        Usuario usuario = usuarioRepository.findById(usuarioId)
//                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + usuarioId));
//
//        // Verificamos si el especialista existe
//        Especialista especialista = especialistaRepository.findByEspecialistaId(especialistaId)
//                .orElseThrow(() -> new RuntimeException("Especialista no encontrado con ID: " + especialistaId));
//
//        // Creamos la cita
//        Cita cita = new Cita(usuarioId, especialistaId, fecha, hora);
//
//        // Guardamos la cita
//        return citaRepository.save(cita);
//    }
//
//    public List<Map<String, Object>> obtenerCitas() {
//        return citaRepository.findAll().stream().map(
//                p -> {
//                    Map<String, Object> response = new HashMap<>();
//                    response.put("especialistaId", p.getEspecialistaId());
//                    response.put("fecha", p.getFecha());
//                    response.put("hora", p.getHora());
//                    response.put("id", p.getId());
//                    response.put("usuarioId", p.getUsuarioId());
//
//                    Optional<Usuario> user = usuarioRepository.findById(p.getUsuarioId());
//                    String nombre = user.map(Usuario::getNombre).orElse("");
//                    String apellido = user.map(Usuario::getApellido).orElse("");
//                    response.put("pacienteNombre", nombre + " " + apellido);
//
//                    Optional<Usuario> especialista = usuarioRepository.findById(p.getEspecialistaId());
//                    String espName = especialista.map(Usuario::getNombre).orElse("");
//                    String espApellido = especialista.map(Usuario::getApellido).orElse("");
//                    response.put("especialistaNombre", espName + " " + espApellido);
//                    response.put("estado", "Activo");
//
//                    return response;
//                }
//        ).collect(Collectors.toList());
//    }
//
//    public List<Cita> obtenerCitasPorUsuario(String usuarioId) {
//        return citaRepository.findByUsuarioId(usuarioId);
//    }
//
//    public List<Map<String, Object>> obtenerCitasPorEspecialista(String especialistaId) {
//        return citaRepository.findByEspecialistaId(especialistaId).stream().map(
//                p -> {
//                    Map<String, Object> response = new HashMap<>();
//                    response.put("especialistaId", p.getEspecialistaId());
//                    response.put("fecha", p.getFecha());
//                    response.put("hora", p.getHora());
//                    response.put("id", p.getId());
//                    response.put("usuarioId", p.getUsuarioId());
//
//                    Optional<Usuario> user = usuarioRepository.findById(p.getUsuarioId());
//                    String nombre = user.map(Usuario::getNombre).orElse("");
//                    String apellido = user.map(Usuario::getApellido).orElse("");
//                    response.put("usuario", nombre + " " + apellido);
//
//                    return response;
//                }
//        ).collect(Collectors.toList());
//    }
//}



package com.example.backend.service;

import com.example.backend.model.Cita;
import com.example.backend.model.Especialista;
import com.example.backend.model.Usuario;
import com.example.backend.repository.CitaRepository;
import com.example.backend.repository.EspecialistaRepository;
import com.example.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CitaService {
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

        // Verificamos si el especialista existe
        Especialista especialista = especialistaRepository.findByEspecialistaId(especialistaId)
                .orElseThrow(() -> new RuntimeException("Especialista no encontrado con ID: " + especialistaId));

        // Creamos la cita
        Cita cita = new Cita(usuarioId, especialistaId, fecha, hora);

        // Guardamos la cita
        Cita savedCita = citaRepository.save(cita);

        // Programar recordatorio para el día de la cita
        reminderService.scheduleReminderForAppointment(savedCita);

        // Enviar confirmación inmediata al usuario
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
            System.err.println("Error enviando correo de confirmación para cita ID: " + savedCita.getId());
            e.printStackTrace();
        }

        return savedCita;
    }


    public List<Map<String, Object>> obtenerCitas() {
        return citaRepository.findAll().stream().map(
                p -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("especialistaId", p.getEspecialistaId());
                    response.put("fecha", p.getFecha());
                    response.put("hora", p.getHora());
                    response.put("id", p.getId());
                    response.put("usuarioId", p.getUsuarioId());

                    Optional<Usuario> user = usuarioRepository.findById(p.getUsuarioId());
                    String nombre = user.map(Usuario::getNombre).orElse("");
                    String apellido = user.map(Usuario::getApellido).orElse("");
                    response.put("pacienteNombre", nombre + " " + apellido);

                    Optional<Usuario> especialista = usuarioRepository.findById(p.getEspecialistaId());
                    String espName = especialista.map(Usuario::getNombre).orElse("");
                    String espApellido = especialista.map(Usuario::getApellido).orElse("");
                    response.put("especialistaNombre", espName + " " + espApellido);
                    response.put("estado", "Activo");

                    return response;
                }
        ).collect(Collectors.toList());
    }

    public List<Cita> obtenerCitasPorUsuario(String usuarioId) {
        return citaRepository.findByUsuarioId(usuarioId);
    }

    public List<Map<String, Object>> obtenerCitasPorEspecialista(String especialistaId) {
        return citaRepository.findByEspecialistaId(especialistaId).stream().map(
                p -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("especialistaId", p.getEspecialistaId());
                    response.put("fecha", p.getFecha());
                    response.put("hora", p.getHora());
                    response.put("id", p.getId());
                    response.put("usuarioId", p.getUsuarioId());

                    Optional<Usuario> user = usuarioRepository.findById(p.getUsuarioId());
                    String nombre = user.map(Usuario::getNombre).orElse("");
                    String apellido = user.map(Usuario::getApellido).orElse("");
                    response.put("usuario", nombre + " " + apellido);

                    return response;
                }
        ).collect(Collectors.toList());
    }
}