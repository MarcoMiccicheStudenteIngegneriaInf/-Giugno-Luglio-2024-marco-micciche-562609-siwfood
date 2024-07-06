package it.uniroma3.siw.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.User;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);

    boolean existsByEmailAndProvider(String email, String provider);

	User findByEmailAndProvider(String email, String provider);
    
    @Query("SELECT DISTINCT u FROM User u JOIN u.ricette r WHERE r IS NOT NULL")
    List<User> findUsersWithRicette();
   
}