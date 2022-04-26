package com.curso.mockito.udemy.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.Optional;

import com.curso.mockito.udemy.Datos;
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
        when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);

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

    @DisplayName(value = "Se puede retornar un examen con una de sus preguntas")
    @Test
    void find_examen_por_nombre_con_preguntas() {
        // Simulamos la respuesta del reposiotrio es vacia
        when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntaRepository.findPreguntasByExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);

        Examen examen = examenService.findExamenPorNombreConPreguntas("Matematicas");

        // Aserciones
        assertEquals(4, examen.getPreguntas().size(), () -> "No todas las preguntas fueron devueltas");
        assertTrue(examen.getPreguntas().contains("aritmetica"), () -> "No todas las preguntas fueron devueltas");
    }

    @DisplayName(value = "Se puede retornar un examen con una de sus preguntas comprobando sus invocaciones")
    @Test
    void find_examen_por_nombre_con_preguntas_verify() {
        // Simulamos la respuesta del reposiotrio es vacia
        when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntaRepository.findPreguntasByExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);

        Examen examen = examenService.findExamenPorNombreConPreguntas("Matematicas");

        // Aserciones
        assertEquals(4, examen.getPreguntas().size(), () -> "No todas las preguntas fueron devueltas");
        assertTrue(examen.getPreguntas().contains("aritmetica"), () -> "No todas las preguntas fueron devueltas");
        verify(examenRepository).findAll();
        verify(preguntaRepository).findPreguntasByExamenId(anyLong());
    }

    @DisplayName(value = "Se puede retornar un examen sin sus preguntas comprobando sus invocaciones")
    @Test
    void find_examen_por_nombre_con_preguntas_verify_lista_vacia() {
        // Simulamos la respuesta del reposiotrio es vacia
        when(examenRepository.findAll()).thenReturn(Collections.emptyList());
        when(preguntaRepository.findPreguntasByExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);

        Examen examen = examenService.findExamenPorNombreConPreguntas("Matematicas2");

        // Aserciones
        assertNull(examen, () -> "El examen no deberia de ser encontrado");
        verify(examenRepository).findAll();
        //verify(preguntaRepository).findPreguntasByExamenId(anyLong());
    }
}
