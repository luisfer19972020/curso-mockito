package com.curso.mockito.udemy.services;

import java.util.Optional;

import com.curso.mockito.udemy.models.Examen;

public interface IExamenService {
    Optional<Examen> findExamenPorNombre(String nombre);

    Examen findExamenPorNombreConPreguntas(String nombre);
}
