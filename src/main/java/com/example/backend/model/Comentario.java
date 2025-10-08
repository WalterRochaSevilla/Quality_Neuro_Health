package com.example.backend.model;

import com.example.backend.model.Publicacion;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@NoArgsConstructor
@Document(collection = "comentarios")
public class Comentario {
    @Id
    private String id;

    @DBRef
    private Publicacion publicacion;

    @DBRef
    private Usuario usuario;

    private String contenido;
    private Instant fechaComentario;
    private String comentarioPadreId;

    public Comentario(Publicacion publicacion, Usuario usuario, String contenido, String comentarioPadreId) {
        this.publicacion = publicacion;
        this.usuario = usuario;
        this.contenido = contenido;
        this.comentarioPadreId = comentarioPadreId;
        this.fechaComentario = Instant.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Publicacion getPublicacion() {
        return publicacion;
    }

    public void setPublicacion(Publicacion publicacion) {
        this.publicacion = publicacion;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public Instant getFechaComentario() {
        return fechaComentario;
    }

    public void setFechaComentario(Instant fechaComentario) {
        this.fechaComentario = fechaComentario;
    }

    public String getComentarioPadreId() {
        return comentarioPadreId;
    }

    public void setComentarioPadreId(String comentarioPadreId) {
        this.comentarioPadreId = comentarioPadreId;
    }

}
