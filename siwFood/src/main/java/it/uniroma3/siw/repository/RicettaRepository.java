package it.uniroma3.siw.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Ricetta;
import it.uniroma3.siw.model.User;

public interface RicettaRepository extends CrudRepository<Ricetta, Long> {
    List<Ricetta> findByUtente(User utente);

}
