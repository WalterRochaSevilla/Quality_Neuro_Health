package com.example.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "citas")
public class Cita {

    @Id
    private String id;
    private String usuarioId;
    private String especialistaId;
    private String fecha; // formato: "YYYY-MM-DD"
    private String hora;  // formato: "HH:mm"

    // Constructor vacío
    public Cita() {}

    // Constructor con parámetros
    public Cita(String usuarioId, String especialistaId, String fecha, String hora) {
        this.usuarioId = usuarioId;
        this.especialistaId = especialistaId;
        this.fecha = fecha;
        this.hora = hora;
    }

    // Getters y setters
    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getUsuarioId() { return usuarioId; }

    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }

    public String getEspecialistaId() { return especialistaId; }

    public void setEspecialistaId(String especialistaId) { this.especialistaId = especialistaId; }

    public String getFecha() { return fecha; }

    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getHora() { return hora; }

    public void setHora(String hora) { this.hora = hora; }
}