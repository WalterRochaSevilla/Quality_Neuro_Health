package com.example.backend.model;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.assertj.core.api.Assertions.*;

class EspecialistaTest {

    @Test
    void constructorVacio() {
        Especialista especialista = new Especialista();
        
        assertThat(especialista).isNotNull();
        assertThat(especialista.getPatients()).isEmpty();
    }

    @Test
    void constructorConParametros() {
        String name = "Dr. Smith";
        String speciality = "Psicología";
        List<String> hours = Arrays.asList("09:00", "10:00", "11:00");
        Map<String, List<String>> occupiedHours = new HashMap<>();
        occupiedHours.put("2024-01-15", Arrays.asList("10:00"));
        List<String> patients = Arrays.asList("patient1", "patient2");
        String especialistaId = "esp123";
        
        Especialista especialista = new Especialista(name, speciality, hours, occupiedHours, patients, especialistaId);
        
        assertThat(especialista.getName()).isEqualTo(name);
        assertThat(especialista.getSpeciality()).isEqualTo(speciality);
        assertThat(especialista.getHours()).isEqualTo(hours);
        assertThat(especialista.getOccupiedHours()).isEqualTo(occupiedHours);
        assertThat(especialista.getPatients()).isEqualTo(patients);
        assertThat(especialista.getEspecialistaId()).isEqualTo(especialistaId);
    }

    @Test
    void getIdAndSetId() {
        Especialista especialista = new Especialista();
        
        especialista.setId("test-id");
        
        assertThat(especialista.getId()).isEqualTo("test-id");
    }

    @Test
    void getNameAndSetName() {
        Especialista especialista = new Especialista();
        
        especialista.setName("Dr. Johnson");
        
        assertThat(especialista.getName()).isEqualTo("Dr. Johnson");
    }

    @Test
    void getSpecialityAndSetSpeciality() {
        Especialista especialista = new Especialista();
        
        especialista.setSpeciality("Neurología");
        
        assertThat(especialista.getSpeciality()).isEqualTo("Neurología");
    }

    @Test
    void getHoursAndSetHours() {
        Especialista especialista = new Especialista();
        List<String> hours = Arrays.asList("08:00", "09:00", "10:00");
        
        especialista.setHours(hours);
        
        assertThat(especialista.getHours()).isEqualTo(hours);
    }

    @Test
    void getOccupiedHoursAndSetOccupiedHours() {
        Especialista especialista = new Especialista();
        Map<String, List<String>> occupiedHours = new HashMap<>();
        occupiedHours.put("2024-01-15", Arrays.asList("09:00", "10:00"));
        
        especialista.setOccupiedHours(occupiedHours);
        
        assertThat(especialista.getOccupiedHours()).isEqualTo(occupiedHours);
    }

    @Test
    void getPatientsAndSetPatients() {
        Especialista especialista = new Especialista();
        List<String> patients = Arrays.asList("patient1", "patient2", "patient3");
        
        especialista.setPatients(patients);
        
        assertThat(especialista.getPatients()).isEqualTo(patients);
    }

    @Test
    void getEspecialistaIdAndSetEspecialistaId() {
        Especialista especialista = new Especialista();
        
        especialista.setEspecialistaId("specialist-123");
        
        assertThat(especialista.getEspecialistaId()).isEqualTo("specialist-123");
    }
}