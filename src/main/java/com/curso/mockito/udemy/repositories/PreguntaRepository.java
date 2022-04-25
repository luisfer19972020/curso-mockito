package com.curso.mockito.udemy.repositories;

import java.util.Collections;
import java.util.List;

public class PreguntaRepository implements IPreguntaRepository {

    @Override
    public List<String> findPreguntasByExamenId(Long id) {
        return Collections.emptyList();
    }
    
}
