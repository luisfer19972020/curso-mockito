package com.curso.mockito.udemy.repositories;

import java.util.Collections;
import java.util.List;

public class PreguntaRepository implements IPreguntaRepository {

    @Override
    public List<String> findPreguntasByExamenId(Long id) {
        System.out.println("PreguntaRepository.findPreguntasByExamenId() - Metodo Real");
        return Collections.emptyList();
    }

    @Override
    public void saveAll(List<String> preguntas) {
        System.out.println("PreguntaRepository.saveAll()");
    }

}
