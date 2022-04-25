package com.curso.mockito.udemy.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import com.curso.mockito.udemy.models.Examen;
import com.curso.mockito.udemy.repositories.ExamenRepository;
import com.curso.mockito.udemy.repositories.PreguntaRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

public class ExamenServiceTest {
    private ExamenRepository examenRepository;
    private PreguntaRepository preguntaRepository;
    private IExamenService examenService;

    @BeforeEach
    void setUp() {
        // Simulamos el repositorio que necesita el service
        this.examenRepository = mock(ExamenRepository.class);
        this.preguntaRepository = mock(PreguntaRepository.class);
        this.examenService = new ExamenService(examenRepository, preguntaRepository);
    }

    @DisplayName(value = "Se puede encontrar un examen por nombre")
    @Test
    void find_examen_por_nombre() {
        // Simulamos la respuesta del reposiotrio
        when(examenRepository.findAll()).thenReturn(Arrays.asList(
                new Examen(5L, "Matematicas"),
                new Examen(6L, "Lenguaje"),
                new Examen(7L, "Historia")));

        Optional<Examen> examen = examenService.findExamenPorNombre("Matematicas");

        // Aserciones
        assertAll(
                () -> assertTrue(examen.isPresent(), () -> "El examen no ha sido devuelto"),
                () -> assertEquals(5L, examen.get().getId(), () -> "El id del examen es incorrecto"),
                () -> assertEquals("Matematicas", examen.get().getNombre(),
                        () -> "El nombre del examen es incorrecto"));
    }

    @DisplayName(value = "Se puede manejan errores por no encontrar lista vacia")
    @Test
    void find_examen_por_nombre_lista_vacia() {
        // Simulamos la respuesta del reposiotrio es vacia
        when(examenRepository.findAll()).thenReturn(Collections.emptyList());

        Optional<Examen> examen = examenService.findExamenPorNombre("Matematicas");

        // Aserciones
        assertFalse(examen.isPresent(), () -> "El examen no ha sido devuelto");
    }
}
