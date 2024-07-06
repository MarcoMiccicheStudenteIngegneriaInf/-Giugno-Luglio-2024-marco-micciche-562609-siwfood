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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "users") // cambiamo nome perchè in postgres user e' una parola riservata
public class User {
	
	public static final String DEFAULT_State = "DEFAULT";

	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

	@NotBlank
	private String name;
	@NotBlank
	private String surname;
	@NotBlank
	private String email;
	
	private String birthday;
	
	private String provider = DEFAULT_State;
	
	@Column(length = 64)
	private String picture;

	@OneToMany(mappedBy = "utente", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Ricetta> ricette= new ArrayList<>() ;

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
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the surname
	 */
	public String getSurname() {
		return surname;
	}

	/**
	 * @param surname the surname to set
	 */
	public void setSurname(String surname) {
		this.surname = surname;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the birthday
	 */
	public String getBirthday() {
		return birthday;
	}

	/**
	 * @param birthday the birthday to set
	 */
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

    /**
	 * @return the provider
	 */
	public String getProvider() {
		return provider;
	}

	/**
	 * @param provider the provider to set
	 */
	public void setProvider(String provider) {
		this.provider = provider;
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


	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}
	
	@Transient
    public String getPictureImagePath() {
        if (picture == null || id == null) return null;
        String msg="/opera-picture/" + this.id + "/" + this.picture;
        return msg;
    }
	
	@Override
	public int hashCode() {
		return Objects.hash(birthday, email, id, name, surname);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		return Objects.equals(birthday, other.birthday) && Objects.equals(email, other.email)
				&& Objects.equals(id, other.id) && Objects.equals(name, other.name)
				&& Objects.equals(surname, other.surname);
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", surname=" + surname + ", email=" + email + ", birthday="
				+ birthday + "]";
	}
    
    
}