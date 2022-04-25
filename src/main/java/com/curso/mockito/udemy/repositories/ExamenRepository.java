package com.curso.mockito.udemy.repositories;

import java.util.Arrays;
import java.util.List;

import com.curso.mockito.udemy.models.Examen;

public class ExamenRepository implements IExamenRepository {

    @Override
    public List<Examen> findAll() {
        return Arrays.asList(
                new Examen(5L, "Matematicas"),
                new Examen(6L, "Lenguaje"),
                new Examen(7L, "Historia"));
    }

}
