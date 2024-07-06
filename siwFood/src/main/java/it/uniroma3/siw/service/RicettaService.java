package it.uniroma3.siw.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.model.Ingrediente;
import it.uniroma3.siw.model.Ricetta;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.IngredienteRepository;
import it.uniroma3.siw.repository.RicettaRepository;

@Service
public class RicettaService {

    @Autowired
    private RicettaRepository ricettaRepository;
    @Autowired
    private IngredienteRepository ingredienteRepository;

    @Transactional
    public void saveRicetta(Ricetta ricetta, User user) {
        ricetta.setUtente(user); // Associa l'utente alla ricetta
        ricettaRepository.save(ricetta); // Salva la ricetta
    }
    
    
    @Transactional
    public void addIngredientToRicetta(Ricetta ricetta, Ingrediente ingrediente) {
        ingredienteRepository.save(ingrediente);  // Salva l'oggetto Ingrediente

    	ricetta.getIngredienti().add(ingrediente);
        ricettaRepository.save(ricetta);
    }
    
    
    public Iterable<Ricetta> getAllRicette() {
        return ricettaRepository.findAll();
    }
    
	public Ricetta findById(Long id) {
		return ricettaRepository.findById(id).get();
	}

	
	
    @Transactional
    public void deleteById(Long id) {
        ricettaRepository.deleteById(id);
    }
    
}
