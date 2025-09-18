package com.example.backend.service;

import com.example.backend.model.Emocion;
import com.example.backend.model.Usuario;
import com.example.backend.repository.EmocionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Optional;
import java.util.function.Supplier;

@Service
public class EmocionService {
    @Autowired
    private EmocionRepository emocionRepository;

    @Autowired
    private UsuarioService usuarioService;


    public Map<String, Object> escribirEnDiario(String usuarioId, String contenido, String emocion) {

        Usuario usuario = usuarioService.obtenerUsuarioPorId(usuarioId);
        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado");
        }


        Emocion diario = emocionRepository.findByUsuario_Id(usuarioId)
                .orElseGet(() -> {
                    Emocion nuevoDiario = new Emocion(usuario);
                    nuevoDiario.setUsuario(usuario);
                    return emocionRepository.save(nuevoDiario);
                });

        diario.setListaDiario(contenido, emocion);
        emocionRepository.save(diario);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("id", diario.getId());
        respuesta.put("usuarioId", usuarioId);
        return respuesta;
    }


    public Map<String, Object> obtenerDiarioCompleto(String usuarioId) {
        Emocion diario = emocionRepository.findByUsuario_Id(usuarioId)
                .orElseThrow(() -> new RuntimeException("Diario no encontrado"));

        // Ordenar las entradas por fecha de publicación (más reciente primero)
        List<Emocion.ListaDiario> entradasOrdenadas = diario.getListaDiario().stream()
                .sorted((e1, e2) -> e2.getFechaPublicacion().compareTo(e1.getFechaPublicacion()))
                .collect(Collectors.toList());

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("id", diario.getId());
        respuesta.put("usuarioId", diario.getUsuario().getId());

        List<Map<String, Object>> entradasMapeadas = entradasOrdenadas.stream()
                .map(entrada -> {
                    Map<String, Object> entradaMap = new HashMap<>();
                    entradaMap.put("type", entrada.getType());
                    entradaMap.put("emotion", entrada.getEmocion());
                    entradaMap.put("notes", entrada.getContenido());
                    entradaMap.put("date", entrada.getFechaPublicacion());
                    return entradaMap;
                })
                .collect(Collectors.toList());

        respuesta.put("entries", entradasMapeadas);
        return respuesta;
    }
}