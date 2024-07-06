package it.uniroma3.siw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.model.Ricetta;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.RicettaRepository;
import it.uniroma3.siw.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The UserService handles logic for Users.
 */
@Service
public class UserService {

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected RicettaRepository ricettaRepository;

    /**
     * This method retrieves a User from the DB based on its ID.
     * @param id the id of the User to retrieve from the DB
     * @return the retrieved User, or null if no User with the passed ID could be found in the DB
     */
    @Transactional
    public User getUserById(Long id) {
        Optional<User> result = this.userRepository.findById(id);
        return result.orElse(null);
    }

    /**
     * This method saves a User in the DB.
     * @param user the User to save into the DB
     * @return the saved User
     * @throws DataIntegrityViolationException if a User with the same username
     *                              as the passed User already exists in the DB
     */
    @Transactional
    public User saveUser(User user) {
        return this.userRepository.save(user);
    }

    
    @Transactional
    public void deleteById(Long id) {
    	userRepository.deleteById(id);
    }
    
    /**
     * This method retrieves all Users from the DB.
     * @return a List with all the retrieved Users
     */
    @Transactional
    public List<User> getAllUsers() {
        List<User> result = new ArrayList<>();
        Iterable<User> iterable = this.userRepository.findAll();
        for(User user : iterable)
            result.add(user);
        return result;
    }
    
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    public boolean existsByEmailAndProvider(String email, String provider) {
        return userRepository.existsByEmailAndProvider(email,provider);
    }


    
    @Transactional
    public void saveOauthUser(DefaultOidcUser oidcUser) {
    	User user = new User();
    	user.setEmail(oidcUser.getEmail());
    	user.setName(oidcUser.getGivenName());
    	user.setSurname(oidcUser.getFamilyName());
    	user.setBirthday(oidcUser.getBirthdate());
    	user.setProvider("google");// Assicurati di gestire correttamente gli altri dettagli
        userRepository.save(user);
    }
    
    public User getCustomOAuth2UserByEmail(String email) {
    	User userOptional = userRepository.findByEmailAndProvider(email,"google");
        
        	
        	
        return userOptional;
    }

    public List<Ricetta> getRicetteByAuthenticatedUser(User user) {
        return ricettaRepository.findByUtente(user);
    }

    public User getCustomOAuth2UserByEmailAndProvider(String email, String provider) {
        return userRepository.findByEmailAndProvider(email, provider);
    }
    
    public List<User> getUsersWithRicette() {
        return userRepository.findUsersWithRicette();
    }
    

}
