package com.curso.mockito.udemy.repositories;

import java.util.List;

import com.curso.mockito.udemy.models.Examen;

public interface IExamenRepository {
    List<Examen> findAll();

    Examen save(Examen examen);
}
