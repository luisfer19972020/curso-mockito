package com.curso.mockito.udemy.repositories;

import java.util.List;

public interface IPreguntaRepository {
    List<String> findPreguntasByExamenId(Long id);

    void saveAll(List<String> preguntas);
}
