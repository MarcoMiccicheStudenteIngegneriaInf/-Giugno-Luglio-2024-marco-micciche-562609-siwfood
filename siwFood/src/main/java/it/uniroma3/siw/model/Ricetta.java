package it.uniroma3.siw.model;

import java.util.List;
import java.util.Objects;
import java.util.ArrayList;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;

@Entity
public class Ricetta {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String nome;
    private String descrizione;
    private String categoria;

    @ManyToMany(cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    private List<Ingrediente> ingredienti;

    @ManyToOne
    private User utente;

	@Column(length = 64)
	private List<String> ricettaPictures = new ArrayList<>();
    
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the nome
	 */
	public String getNome() {
		return nome;
	}

	/**
	 * @param nome the nome to set
	 */
	public void setNome(String nome) {
		this.nome = nome;
	}

	/**
	 * @return the descrizione
	 */
	public String getDescrizione() {
		return descrizione;
	}

	/**
	 * @param descrizione the descrizione to set
	 */
	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	/**
	 * @return the ingredienti
	 */
	public List<Ingrediente> getIngredienti() {
		return ingredienti;
	}

	/**
	 * @param ingredienti the ingredienti to set
	 */
	public void setIngredienti(List<Ingrediente> ingredienti) {
		this.ingredienti = ingredienti;
	}

	/**
	 * @return the utente
	 */
	public User getUtente() {
		return utente;
	}

	/**
	 * @param utente the utente to set
	 */
	public void setUtente(User utente) {
		this.utente = utente;
	}

	
	
	
	
	
	/**
	 * @return the categoria
	 */
	public String getCategoria() {
		return categoria;
	}

	/**
	 * @param categoria the categoria to set
	 */
	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	/**
	 * @return the ricettaPictures
	 */
	public List<String> getRicettaPictures() {
		return ricettaPictures;
	}

	/**
	 * @param ricettaPictures the ricettaPictures to set
	 */
	public void setRicettaPictures(List<String> ricettaPictures) {
		this.ricettaPictures = ricettaPictures;
	}

	@Override
	public int hashCode() {
		return Objects.hash(categoria, id, ingredienti, nome, utente);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Ricetta other = (Ricetta) obj;
		return Objects.equals(categoria, other.categoria) && Objects.equals(id, other.id)
				&& Objects.equals(ingredienti, other.ingredienti) && Objects.equals(nome, other.nome)
				&& Objects.equals(utente, other.utente);
	}

	

}
