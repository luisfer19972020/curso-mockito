package com.curso.mockito.udemy.repositories;

import java.util.Collections;
import java.util.List;

import com.curso.mockito.udemy.models.Examen;

public class ExamenRepository implements IExamenRepository {

    @Override
    public List<Examen> findAll() {
        return Collections.emptyList(); 
        /* Arrays.asList(
                new Examen(5L, "Matematicas"),
                new Examen(6L, "Lenguaje"),
                new Examen(7L, "Historia")); */
    }

    @Override
    public Examen save(Examen examen) {
        return null;
    }

}
