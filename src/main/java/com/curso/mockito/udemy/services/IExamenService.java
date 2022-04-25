package com.curso.mockito.udemy.services;

import com.curso.mockito.udemy.models.Examen;

public interface IExamenService {
    Examen findExamenPorNombre(String nombre);
}
