package it.uniroma3.siw.model;

import java.util.List;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

@Entity
public class Ingrediente {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	   
	private String nome;
	private int quantita;

	@ManyToMany
    private List<Ricetta> ricette;

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
	 * @return the quantita
	 */
	public int getQuantita() {
		return quantita;
	}

	/**
	 * @param quantita the quantita to set
	 */
	public void setQuantita(int quantita) {
		this.quantita = quantita;
	}

	/**
	 * @return the ricette
	 */
	public List<Ricetta> getRicette() {
		return ricette;
	}

	/**
	 * @param ricette the ricette to set
	 */
	public void setRicette(List<Ricetta> ricette) {
		this.ricette = ricette;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, nome, quantita, ricette);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Ingrediente other = (Ingrediente) obj;
		return Objects.equals(id, other.id) && Objects.equals(nome, other.nome) && quantita == other.quantita
				&& Objects.equals(ricette, other.ricette);
	}

	
	
	
	
}
