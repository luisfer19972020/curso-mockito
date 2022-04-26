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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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

        @Test
        void doAnswer() {
            // Teniendo un servicio de exmaen
            when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);
            // when(preguntaRepository.findPreguntasByExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
            // NOTA: Otra forma de hacer el awser del then
            Mockito.doAnswer(invocation -> {
                Long id = invocation.getArgument(0);
                return id == 5L ? Datos.PREGUNTAS : null;
            }).when(preguntaRepository).findPreguntasByExamenId(anyLong());

            // Cuandobuscamos una pregunta con sus examenes
            Examen examen = examenService.findExamenPorNombreConPreguntas("Matematicas");

            // Asertamos que nos devuelva la preguntas correctas
            assumingThat(examen.getNombre().equals("Matematicas"), () -> {
                assertAll(
                        () -> assertEquals(5L, examen.getId(), () -> "El examen no tien el id correcto"),
                        () -> assertEquals("Matematicas", examen.getNombre(),
                                () -> "El examen no tien el nombre correcto"),
                        () -> assertEquals(4, examen.getPreguntas().size(),
                                () -> "El exmane no tiene todas las preguntas"));
            });
            assumingThat(!examen.getNombre().equals("Matematicas"), () -> {
                assertAll(
                        () -> assertNull(examen.getPreguntas(),
                                () -> "El exmane no deberia tener preguntas"));
            });
            verify(preguntaRepository).findPreguntasByExamenId(anyLong());
        }

        @DisplayName(value = "Se puede guardar un examen con preguntas")
        @Test
        void save_with_questions_do_answer() {
            // Teniendo un examen con preguntas
            Examen examenConPreguntas = Datos.EXAMEN;
            examenConPreguntas.setPreguntas(Datos.PREGUNTAS);

            // Cuando guardamos las preguntas atraves del servicio
            /*
             * when(examenRepository.save(any(Examen.class))).then(new Answer<Examen>() {
             * Long secuencia = 8L;
             * 
             * @Override
             * public Examen answer(InvocationOnMock invocation) throws Throwable {
             * Examen examen = invocation.getArgument(0);// Obteenmos el examen que recibe
             * el save
             * examen.setId(secuencia++);// Vamos incrementando el id
             * return examen;
             * }
             * });
             */
            Mockito.doAnswer(new Answer<Examen>() {
                Long secuencia = 8L;

                @Override
                public Examen answer(InvocationOnMock invocation) throws Throwable {
                    Examen examen = invocation.getArgument(0);// Obteenmos el examen que recibe
                    // el save
                    examen.setId(secuencia++);// Vamos incrementando el id
                    return examen;
                }
            }).when(examenRepository).save(any(Examen.class));
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

        // Para hacer un llamado real del metodo[Para utilizarlo en una dependencia
        // injectada debe ser una clase y no una interfaz]
        @Test
        void doCallRealMethod() {
            when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);
            // when(preguntaRepository.findPreguntasByExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);

            Mockito.doCallRealMethod().when(preguntaRepository).findPreguntasByExamenId(anyLong());
            Examen examen = examenService.findExamenPorNombreConPreguntas("Matematicas");

            assertEquals(5L, examen.getId());
            assertEquals("Matematicas", examen.getNombre());
            // No aseguramos que se mande a llamar el metodo real
            assertEquals(0, examen.getPreguntas().size());
        }

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

    @Nested
    class OrdenNumeroEjecuciones {
        @Test
        void orden_de_invocacion_test() {// Nos permite verificar el orden de ejecucion de las dependencias
            when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);

            examenService.findExamenPorNombreConPreguntas("Matematicas");
            examenService.findExamenPorNombreConPreguntas("Lenguaje");

            InOrder inOrder = inOrder(preguntaRepository);

            inOrder.verify(preguntaRepository).findPreguntasByExamenId(5L);
            inOrder.verify(preguntaRepository).findPreguntasByExamenId(6L);
        }

        @Test
        void orden_de_invocacion_test2() {// Nos permite verificar el orden de ejecucion de las dependencias
            when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);

            examenService.findExamenPorNombreConPreguntas("Matematicas");
            examenService.findExamenPorNombreConPreguntas("Lenguaje");

            InOrder inOrder = inOrder(examenRepository, preguntaRepository);

            inOrder.verify(examenRepository).findAll();
            inOrder.verify(preguntaRepository).findPreguntasByExamenId(5L);
            inOrder.verify(examenRepository).findAll();
            inOrder.verify(preguntaRepository).findPreguntasByExamenId(6L);
        }

        @Test
        void numero_de_invocaciones_test() {
            when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);

            examenService.findExamenPorNombreConPreguntas("Matematicas");
            //Al menos
            verify(preguntaRepository, times(1)).findPreguntasByExamenId(5L);
            verify(preguntaRepository, atLeast(1)).findPreguntasByExamenId(5L);
            verify(preguntaRepository, atLeastOnce()).findPreguntasByExamenId(5L);
            //A lo mucho
            verify(preguntaRepository, atMost(10)).findPreguntasByExamenId(5L);
            verify(preguntaRepository, atMostOnce()).findPreguntasByExamenId(5L);
        }

      /*   @Test
        void numero_de_invocaciones_tes2() {//Aplica cuando sacamos la lista de pregunta 2 veces
            when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);

            examenService.findExamenPorNombreConPreguntas("Matematicas");
            //Al menos
            //verify(preguntaRepository).findPreguntasByExamenId(5L); //Falla
            verify(preguntaRepository, times(2)).findPreguntasByExamenId(5L);
            verify(preguntaRepository, atLeast(1)).findPreguntasByExamenId(5L);
            verify(preguntaRepository, atLeastOnce()).findPreguntasByExamenId(5L);
            //A lo mucho
            verify(preguntaRepository, atMost(10)).findPreguntasByExamenId(5L);
            //verify(preguntaRepository, atMostOnce()).findPreguntasByExamenId(5L);//Falla
        } */

        @Test
        void numero_de_invocaciones_tes3() {//Aplica cuando queremos decir que nunca se utilizara el metodo de un mock
            when(examenRepository.findAll()).thenReturn(Collections.emptyList());

            examenService.findExamenPorNombreConPreguntas("Matematicas");
            
            //Ninguna vez
            verify(preguntaRepository, never()).findPreguntasByExamenId(5L);
            verifyNoInteractions(preguntaRepository);

            verify(examenRepository,times(1)).findAll();
            verify(examenRepository,atLeast(1)).findAll();
            verify(examenRepository,atLeastOnce()).findAll();
            verify(examenRepository,atMost(1)).findAll();
            verify(examenRepository,atMostOnce()).findAll();
            
        }
    }
}
