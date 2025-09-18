package com.example.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "community")
public class Publicacion {
    @Id
    private String id;

    @DBRef
    private Usuario usuario;

    private String titulo;
    private String contenido;
    private Instant fechaPublicacion;
    private String tema;
    private List<String> comentarioIds = new ArrayList<>();

    public Publicacion(Usuario usuario, String titulo, String contenido, String tema) {
        this.usuario = usuario;
        this.titulo = titulo;
        this.contenido = contenido;
        this.tema = tema;
        this.fechaPublicacion = Instant.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public Instant getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(Instant fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public String getTema() {
        return tema;
    }

    public void setTema(String tema) {
        this.tema = tema;
    }

    public List<String> getComentarioIds() {
        return comentarioIds;
    }

    public void setComentarioIds(List<String> comentarioIds) {
        this.comentarioIds = comentarioIds;
    }

}
