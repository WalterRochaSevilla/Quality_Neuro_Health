package com.example.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "historialPaciente")
public class Examen {
    @Id
    private String id;

    @DBRef
    private Usuario usuario;

    private List<Examenes> examenes = new ArrayList<>();

    public Examen() {}

    public Examen(Usuario usuario) {
        this.usuario = usuario;
    }

    public static class Examenes {
        private String type;
        private String name;
        private String description;
        private String result;
        private Instant fechaPublicacion;

        public Examenes(String description, String name, String result) {
            this.type = "exam";
            this.name = name;
            this.description = description;
            this.result = result;
            this.fechaPublicacion = Instant.now();
        }
        public String getType() {return type;}
        public String getName() {return name;}
        public String getResult() {return result;}
        public String getDescription() {return description;}
        public Instant getFechaPublicacion() {return fechaPublicacion;}
    }


    public String getId() {return id;}
    public Usuario getUsuario() {return usuario;}
    public List<Examenes> getExamenes() {return examenes;}
    public void setId(String id) {this.id = id;}
    public void setUsuario(Usuario usuario) {this.usuario = usuario;}
    public void setExamenes(String description, String name, String result) {this.examenes.add(new Examenes(description, name, result));}
}