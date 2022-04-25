package com.curso.mockito.udemy.services;

import java.util.Optional;

import com.curso.mockito.udemy.models.Examen;
import com.curso.mockito.udemy.repositories.IExamenRepository;

public class ExamenService implements IExamenService {

    private IExamenRepository examenRepository;

    public ExamenService(IExamenRepository examenRepository) {
        this.examenRepository = examenRepository;
    }

    @Override
    public Optional<Examen> findExamenPorNombre(String nombre) {
        return examenRepository.findAll()
                .stream()
                .filter(c -> c.getNombre()
                        .contains(nombre))
                .findFirst();
    }

}
