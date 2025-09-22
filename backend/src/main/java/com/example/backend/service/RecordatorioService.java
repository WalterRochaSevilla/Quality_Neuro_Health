package com.example.backend.service;

import com.example.backend.model.Cita;
import com.example.backend.model.Usuario;
import com.example.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class RecordatorioService {

    private static final Logger logger = LoggerFactory.getLogger(RecordatorioService.class);

    private final UsuarioRepository usuarioRepository;
    private final EmailService emailService;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // ✅ instancia en lugar de static

    @Autowired
    public RecordatorioService(
            UsuarioRepository usuarioRepository,
            EmailService emailService) {
        this.usuarioRepository = usuarioRepository;
        this.emailService = emailService;
    }

    @Async
    public void scheduleReminderForAppointment(Cita cita) {
        try {
            Date appointmentDate = dateFormat.parse(cita.getFecha());
            Date today = new Date();

            if (dateFormat.format(appointmentDate).equals(dateFormat.format(today))) {
                sendReminderEmail(cita);
                return;
            }

            long delay = appointmentDate.getTime() - today.getTime();

            if (delay > 0) {
                scheduler.schedule(() -> sendReminderEmail(cita),
                        delay,
                        TimeUnit.MILLISECONDS);
            }
        } catch (Exception e) {
            logger.error("Error programando recordatorio para cita ID: {}", cita.getId(), e); // ✅ logger
        }
    }

    private void sendReminderEmail(Cita cita) {
        try {
            Usuario usuario = usuarioRepository.findById(cita.getUsuarioId()).orElse(null);
            Usuario especialista = usuarioRepository.findById(cita.getEspecialistaId()).orElse(null);

            if (usuario != null && especialista != null) {
                String subject = "NeuroHealth - Recordatorio de cita hoy";
                String body = buildReminderEmailBody(usuario, especialista, cita);
                emailService.sendEmail(usuario.getEmail(), subject, body);
            }
        } catch (Exception e) {
            logger.error("Error enviando recordatorio para cita ID: {}", cita.getId(), e); // ✅ logger
        }
    }

    private String buildReminderEmailBody(Usuario usuario, Usuario especialista, Cita cita) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "    <style>" +
                "        body { font-family: 'Arial', sans-serif; line-height: 1.6; color: #333; }" +
                "        .container { max-width: 600px; margin: 20px auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 8px; }" +
                "        .header { color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 10px; }" +
                "        .details { background-color: #f8f9fa; padding: 15px; border-left: 4px solid #3498db; margin: 20px 0; }" +
                "        .footer { font-size: 0.9em; color: #7f8c8d; margin-top: 20px; }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class='container'>" +
                "        <h1 class='header'>NeuroHealth - Recordatorio de Cita</h1>" +
                "        <p>Hola <strong>" + usuario.getNombre() + " " + usuario.getApellido() + "</strong>,</p>" +
                "        <p>Este es un recordatorio amable de tu cita programada:</p>" +
                "        <div class='details'>" +
                "            <p><strong>Fecha:</strong> " + cita.getFecha() + "</p>" +
                "            <p><strong>Hora:</strong> " + cita.getHora() + "</p>" +
                "            <p><strong>Especialista:</strong> " + especialista.getNombre() + " " + especialista.getApellido() + "</p>" +
                "        </div>" +
                "        <p>Por favor asegúrate de estar a tiempo para tu sesión.</p>" +
                "        <div class='footer'>" +
                "            <p>Saludos,<br>Equipo NeuroHealth</p>" +
                "            <p>Este es un mensaje automático, por favor no respondas a este correo.</p>" +
                "        </div>" +
                "    </div>" +
                "</body>" +
                "</html>";
    }
}
