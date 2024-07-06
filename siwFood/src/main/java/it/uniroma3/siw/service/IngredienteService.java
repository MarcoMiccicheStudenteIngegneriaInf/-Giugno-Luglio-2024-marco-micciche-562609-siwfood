package it.uniroma3.siw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.model.Ingrediente;
import it.uniroma3.siw.repository.IngredienteRepository;



@Service
public class IngredienteService {
    @Autowired
    private IngredienteRepository ingredienteRepository  ;

    @Transactional
    public void deleteById(Long id) {
    	ingredienteRepository.deleteById(id);
    }
    
    public Iterable<Ingrediente> getAllIngredienti() {
        return ingredienteRepository.findAll();
    }
}
