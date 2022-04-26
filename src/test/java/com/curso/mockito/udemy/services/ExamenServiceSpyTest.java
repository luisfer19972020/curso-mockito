package com.curso.mockito.udemy.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumingThat;

import java.util.Collections;
import java.util.Optional;

import com.curso.mockito.udemy.Datos;
import com.curso.mockito.udemy.models.Examen;
import com.curso.mockito.udemy.repositories.ExamenRepository;
import com.curso.mockito.udemy.repositories.PreguntaRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExamenServiceSpyTest {

    //Spys para usar lso metodos reales no recomendado
    
    @Spy
    ExamenRepository examenRepository;

    @Spy
    PreguntaRepository preguntaRepository;

    @InjectMocks
    ExamenService examenService;

 
   
    @Test
    void testSpy() {
        // Forma de crear un mock
        // ExamenRepository examenRepository = mock(ExamenRepository.class);
        // Forma de crear un spy (Solo se puede con clases concretas)
        ExamenRepository examenRepository = spy(ExamenRepository.class);
        PreguntaRepository preguntaRepository = spy(PreguntaRepository.class);
        ExamenService examenService = new ExamenService(examenRepository, preguntaRepository);

        // Este es el que manda
        // when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);
        // Para evitar llamar el metodo [ExamenRepository.findAll() - metodo real] del
        // objeto real cuando usamos el mock debemos usar el doReturn
        doReturn(Datos.EXAMENES).when(examenRepository).findAll();

        Examen examen = examenService.findExamenPorNombreConPreguntas("Matematicas");
        assertNotNull(examen, () -> "La clase debe ser nula");
        assertEquals("Matematicas", examen.getNombre());
        verify(examenRepository).findAll();
        verify(preguntaRepository).findPreguntasByExamenId(anyLong());
    }

}
