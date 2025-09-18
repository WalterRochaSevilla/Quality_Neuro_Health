package com.example.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "diario-emociones")
public class Emocion {
    @Id
    private String id;

    @DBRef
    private Usuario usuario;

    private List<ListaDiario> listaDiario = new ArrayList<>();

    public Emocion() {}

    public Emocion(Usuario usuario) {
        this.usuario = usuario;
    }

    public static class ListaDiario {
        private String type;
        private String emocion;
        private String contenido;
        private Instant fechaPublicacion;

        public ListaDiario(String contenido, String emocion) {
            this.type = "emotion";
            this.emocion = emocion;
            this.contenido = contenido;
            this.fechaPublicacion = Instant.now();
        }

        public String getType() {return type;}
        public String getEmocion() {return emocion;}
        public String getContenido() {return contenido;}
        public Instant getFechaPublicacion() {return fechaPublicacion;}
    }


    public String getId() {return id;}
    public Usuario getUsuario() {return usuario;}
    public List<ListaDiario> getListaDiario() {return listaDiario;}
    public void setId(String id) {this.id = id;}
    public void setUsuario(Usuario usuario) {this.usuario = usuario;}
    public void setListaDiario(String contenido, String emocion) {this.listaDiario.add(new ListaDiario(contenido, emocion));}
}