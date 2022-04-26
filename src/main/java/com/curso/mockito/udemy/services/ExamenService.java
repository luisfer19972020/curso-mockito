package com.curso.mockito.udemy.services;

import java.util.Optional;

import com.curso.mockito.udemy.models.Examen;
import com.curso.mockito.udemy.repositories.ExamenRepository;
import com.curso.mockito.udemy.repositories.IExamenRepository;
import com.curso.mockito.udemy.repositories.IPreguntaRepository;
import com.curso.mockito.udemy.repositories.PreguntaRepository;

public class ExamenService implements IExamenService {

    private IExamenRepository examenRepository;
    private IPreguntaRepository preguntaRepository;

    public ExamenService(ExamenRepository examenRepository, PreguntaRepository preguntaRepository) {
        this.examenRepository = examenRepository;
        this.preguntaRepository = preguntaRepository;
    }

    @Override
    public Optional<Examen> findExamenPorNombre(String nombre) {
        return examenRepository.findAll()
                .stream()
                .filter(c -> c.getNombre()
                        .contains(nombre))
                .findFirst();
    }

    @Override
    public Examen findExamenPorNombreConPreguntas(String nombre) {
        Optional<Examen> examenOptional = this.findExamenPorNombre(nombre);
        Examen examen = null;
        if (examenOptional.isPresent()) {
            examen = examenOptional.get();
            examen.setPreguntas(this.preguntaRepository.findPreguntasByExamenId(examen.getId()));
        }
        return examen;
    }

    @Override
    public Examen save(Examen examen) {
        if(!examen.getPreguntas().isEmpty()){
            this.preguntaRepository.saveAll(examen.getPreguntas());
        }
        return examenRepository.save(examen);
    }

}
