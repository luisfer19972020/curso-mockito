package com.curso.mockito.udemy.services;

import static org.junit.jupiter.api.Assertions.*;

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
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExamenServiceTest {

    @Mock
    ExamenRepository examenRepository;

    @Mock
    PreguntaRepository preguntaRepository;

    @InjectMocks
    ExamenService examenService;

    @Captor
    ArgumentCaptor<Long> captor;

    // Si vamos a utililizar notaciones no podemos instanciar la intefaz sino la
    // clase implementada

    @BeforeEach
    void setUp() {
        // MockitoAnnotations Se puede remplazar notando la clase
        // MockitoAnnotations.openMocks(this);// Habilitamos las notaciones para esta
        // clase
        // Simulamos el repositorio que necesita el service

        // this.examenRepository = mock(ExamenRepository.class);
        // this.preguntaRepository = mock(PreguntaRepository.class);
        // this.examenService = new ExamenService(examenRepository, preguntaRepository);
    }

    @Nested
    class ExamenConsultaTest {
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
            // when(preguntaRepository.findPreguntasByExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);

            Examen examen = examenService.findExamenPorNombreConPreguntas("Matematicas2");

            // Aserciones
            assertNull(examen, () -> "El examen no deberia de ser encontrado");
            verify(examenRepository).findAll();
            // verify(preguntaRepository).findPreguntasByExamenId(anyLong());
        }
    }

    @Nested
    class ExamenPersistenciaTest {
        @DisplayName(value = "Se puede guardar un examen sin preguntas")
        @Test
        void save() {
            when(examenRepository.save(any(Examen.class))).thenReturn(Datos.EXAMEN);
            Examen examen = examenService.save(Datos.EXAMEN);

            assertAll(
                    () -> assertNotNull(examen, () -> "El examen no debe ser nulo"),
                    () -> assertEquals(8L, examen.getId(), () -> "El examen no tiene el id correcto"),
                    () -> assertEquals("Fisica", examen.getNombre(), () -> "El examen no tiene el id correcto"),
                    () -> verify(examenRepository).save(any(Examen.class)));
        }

        @DisplayName(value = "Se puede guardar un examen con preguntas")
        @Test
        void save_with_questions() {
            // Teniendo un examen con preguntas
            Examen examenConPreguntas = Datos.EXAMEN;
            examenConPreguntas.setPreguntas(Datos.PREGUNTAS);

            // Cuando guardamos las preguntas atraves del servicio
            when(examenRepository.save(any(Examen.class))).then(new Answer<Examen>() {
                Long secuencia = 8L;

                @Override
                public Examen answer(InvocationOnMock invocation) throws Throwable {
                    Examen examen = invocation.getArgument(0);// Obteenmos el examen que recibe el save
                    examen.setId(secuencia++);// Vamos incrementando el id
                    return examen;
                }
            });
            Examen examen = examenService.save(examenConPreguntas);

            // Then - Entonces verificamos
            assertAll(
                    () -> assertNotNull(examen, () -> "El examen no debe ser nulo"),
                    () -> assertEquals(8L, examen.getId(), () -> "El examen no tiene el id correcto"),
                    () -> assertEquals("Fisica", examen.getNombre(), () -> "El examen no tiene el id correcto"),
                    () -> assertEquals(4, examen.getPreguntas().size(), () -> "El examen no tiene todas la preguntas"),
                    () -> verify(examenRepository).save(any(Examen.class)),
                    () -> verify(preguntaRepository).saveAll(any()));
        }
    }

    @Nested
    class ExamenExepcionesTest {

        @Test
        void manejo_exepcion_test() {
            // Teniendo una consulta

            // Cuando pasamos un elemento ilegal queremos lanzar una excepcion
            when(examenRepository.findAll()).thenReturn(Datos.EXAMENES_ID_NULL);
            when(preguntaRepository.findPreguntasByExamenId(isNull())).thenThrow(IllegalArgumentException.class);

            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                examenService.findExamenPorNombreConPreguntas("Matematicas");
            }, () -> "No se recibio una excepcion de tipo IllegalArgumentException");

            assertEquals(IllegalArgumentException.class, exception.getClass(),
                    () -> "No se recibio una excepcion de tipo IllegalArgumentException");

            verify(examenRepository).findAll();
            verify(preguntaRepository).findPreguntasByExamenId(isNull());
        }
    }

    @Nested
    class ArgumentosTest {
        @Test
        void argumentsMatchersTest() {
            when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);
            when(preguntaRepository.findPreguntasByExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
            Examen examen = examenService.findExamenPorNombreConPreguntas("Matematicas");

            // Verificar que nuestros arguemntos se pasen correcatmente
            assertTrue(examen instanceof Examen, () -> "Se debe devolver una instancia de examen");
            verify(examenRepository).findAll();
            verify(preguntaRepository).findPreguntasByExamenId(argThat(arg -> arg != null && arg >= 5L));
            // verify(preguntaRepository).findPreguntasByExamenId(eq(5L));

        }

        @Test
        void argumentsMatchersClassTest() {
            when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);
            when(preguntaRepository.findPreguntasByExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
            Examen examen = examenService.findExamenPorNombreConPreguntas("Matematicas");

            // Verificar que nuestros arguemntos se pasen correcatmente
            assertTrue(examen instanceof Examen, () -> "Se debe devolver una instancia de examen");
            verify(examenRepository).findAll();
            verify(preguntaRepository).findPreguntasByExamenId(argThat(new MiArgsMatchers()));
            // verify(preguntaRepository).findPreguntasByExamenId(eq(5L));
        }

        @Test
        void ArgumentCaptorTest() {
            when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);
            when(preguntaRepository.findPreguntasByExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
            examenService.findExamenPorNombreConPreguntas("Matematicas");

            // Para capturar el argumento
            // Esto se puede hacer con la notacon captor
            // ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
            verify(preguntaRepository).findPreguntasByExamenId(captor.capture());
            assertEquals(5L, captor.getValue());

        }

    }

    class MiArgsMatchers implements ArgumentMatcher<Long> {
        private Long argument;

        @Override
        public boolean matches(Long argument) {
            this.argument = argument;
            return argument != null && argument > 0L;
        }

        @Override
        public String toString() {
            return "El long " + argument + " no comple con la regla  NOT NULL y no es mayor a 0";
        }

    }

    @Nested
    class MetodosDoSomething {
        @Test
        void testDoThrow() {
            // Teneindo un examen con preguntas
            Examen examen = Datos.EXAMEN;
            examen.setPreguntas(Datos.PREGUNTAS);

            // Cuando lanzmaos una excepcion IllegalArgumentException
            // NOTA: este metodo doThrow e usa para devolver un error solo en metodos void
            doThrow(IllegalArgumentException.class).when(preguntaRepository).saveAll(anyList());
            // when(preguntaRepository.saveAll(anyList())).thenThrow(IllegalArgumentException.class);//Si
            // el metodo retornara algo

            // Entonces nos sercioramos que lancemos una excepcion de tipo
            // IllegalArgumentException
            assertThrows(IllegalArgumentException.class, () -> {
                examenService.save(examen);
            });
        }
    }
}
