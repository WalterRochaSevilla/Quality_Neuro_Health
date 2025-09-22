package com.example.backend.service;

import com.example.backend.model.Examen;
import com.example.backend.model.Usuario;
import com.example.backend.repository.ExamenRepository;
import com.example.backend.exception.UsuarioNotFoundException; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExamenService {
    @Autowired
    private ExamenRepository examenRepository;

    @Autowired
    private UsuarioService usuarioService;

    public Map<String, Object> aumentarExamen(String usuarioId, String description, String name, String result) {
        Usuario usuario = usuarioService.obtenerUsuarioPorId(usuarioId);
        if (usuario == null) {
            throw new UsuarioNotFoundException("Usuario con ID " + usuarioId + " no encontrado");
        }

        Examen diario = examenRepository.findByUsuario_Id(usuarioId)
                .orElseGet(() -> {
                    Examen nuevoDiario = new Examen(usuario);
                    nuevoDiario.setUsuario(usuario);
                    return examenRepository.save(nuevoDiario);
                });

        diario.setExamenes(description, name, result);
        examenRepository.save(diario);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("id", diario.getId());
        respuesta.put("usuarioId", usuarioId);
        return respuesta;
    }

    public Map<String, Object> obtenerExamenes(String usuarioId) {
        Examen diario = examenRepository.findByUsuario_Id(usuarioId)
                .orElseThrow(() -> new UsuarioNotFoundException("Exámenes del usuario con ID " + usuarioId + " no encontrados"));

        List<Examen.Examenes> entradasOrdenadas = diario.getExamenes().stream()
                .sorted((e1, e2) -> e2.getFechaPublicacion().compareTo(e1.getFechaPublicacion()))
                .collect(Collectors.toList());

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("id", diario.getId());
        respuesta.put("usuarioId", diario.getUsuario().getId());

        List<Map<String, Object>> entradasMapeadas = entradasOrdenadas.stream()
                .map(entrada -> {
                    Map<String, Object> entradaMap = new HashMap<>();
                    entradaMap.put("type", entrada.getType());
                    entradaMap.put("description", entrada.getDescription());
                    entradaMap.put("name", entrada.getName());
                    entradaMap.put("result", entrada.getResult());
                    entradaMap.put("date", entrada.getFechaPublicacion());
                    return entradaMap;
                })
                .collect(Collectors.toList());

        respuesta.put("entries", entradasMapeadas);
        return respuesta;
    }
}
