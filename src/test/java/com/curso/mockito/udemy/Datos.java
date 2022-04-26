package com.curso.mockito.udemy;

import java.util.Arrays;
import java.util.List;

import com.curso.mockito.udemy.models.Examen;

public class Datos {
    public final static List<Examen> EXAMENES = Arrays.asList(
            new Examen(5L, "Matematicas"),
            new Examen(6L, "Lenguaje"),
            new Examen(7L, "Historia"));
    public final static List<String> PREGUNTAS = Arrays.asList(
            "aritmetica",
            "integrales",
            "trigonometria",
            "geometria");
    public final static Examen EXAMEN = new Examen(8L, "Fisica");
}
