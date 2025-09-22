package com.example.backend.service;

import com.example.backend.model.Especialista;
import com.example.backend.model.Usuario;
import com.example.backend.repository.EspecialistaRepository;
import com.example.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class EspecialistaService {

    // Aumentar registro de citas
    @Autowired
    private EspecialistaRepository especialistaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Obtener todos los especialistas
    public List<Especialista> getAllEspecialistas() {
        return especialistaRepository.findAll();
    }

    // Obtener horarios de un especialista por ID
    public Map<String, List<String>> getHorariosByEspecialistaId(String id, String fecha) {
        Optional<Especialista> especialista = especialistaRepository.findById(id);
        Map<String, List<String>> horarios = new HashMap<>();

        if (especialista.isPresent()) {
            horarios.put("hours", especialista.get().getHours());
            Map<String, List<String>> ocupadas = especialista.get().getOccupiedHours();
            horarios.put("occupiedHours", ocupadas.getOrDefault(fecha, List.of()));
        }

        return horarios;
    }


    public boolean addOccupiedHour(String id, String hour, String fecha) {
        Optional<Especialista> especialistaOpt = especialistaRepository.findById(id);
        if (especialistaOpt.isPresent()) {
            Especialista especialista = especialistaOpt.get();
            Map<String, List<String>> ocupadas = especialista.getOccupiedHours();

            if (!ocupadas.containsKey(fecha)) {
                ocupadas.put(fecha, new ArrayList<>());
            }

            List<String> horasEnFecha = ocupadas.get(fecha);
            if (!horasEnFecha.contains(hour)) {
                horasEnFecha.add(hour);
                especialista.setOccupiedHours(ocupadas);
                especialistaRepository.save(especialista);
                return true;
            }
        }
        return false;
    }

    public boolean addPatient(String especialistaId, String pacienteId) {
        Optional<Especialista> especialistaOpt = especialistaRepository.findById(especialistaId);
        if (especialistaOpt.isPresent()) {
            Especialista especialista = especialistaOpt.get();
            List<String> patients = especialista.getPatients();

            if (!patients.contains(pacienteId)) {
                patients.add(pacienteId);
                especialista.setPatients(patients);
                especialistaRepository.save(especialista);
                return true;
            }
        }
        return false;
    }


    public List<Map<String, String>> getPatientsByEspecialistaId(String especialistaId) {
        Optional<Especialista> especialistaOpt = especialistaRepository.findByEspecialistaId(especialistaId);
        List<Map<String, String>> pacientesInfo = new ArrayList<>();

        if (especialistaOpt.isPresent()) {
            Especialista especialista = especialistaOpt.get();
            List<String> pacientesIds = especialista.getPatients();

            List<Usuario> usuarios = usuarioRepository.findAllById(pacientesIds);

            for (Usuario usuario : usuarios) {
                Map<String, String> data = new HashMap<>();
                data.put("id", usuario.getId());
                data.put("nombre", usuario.getNombre());
                pacientesInfo.add(data);
            }
        }

        return pacientesInfo;
    }


}