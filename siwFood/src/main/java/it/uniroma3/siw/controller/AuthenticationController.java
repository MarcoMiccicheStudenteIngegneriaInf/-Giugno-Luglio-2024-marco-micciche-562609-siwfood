package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.RicettaService;
import it.uniroma3.siw.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

/**
 * Controller per gestire le operazioni di autenticazione e registrazione degli utenti.
 */
@Controller
public class AuthenticationController {

    @Autowired
    private CredentialsService credentialsService;

    @Autowired
    private UserService userService;

    @Autowired
    private GlobalController globalController;

    @Autowired
    private RicettaService ricettaService;

    
    
    /**
     * Metodo per visualizzare il form di registrazione.
     * 
     * @param model il modello della vista
     * @return il nome della vista del form di registrazione
     */
    @GetMapping(value = "/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("credentials", new Credentials());
        return "formRegisterUser";
    }

    /**
     * Metodo per visualizzare il form di login.
     * 
     * @param model il modello della vista
     * @return il nome della vista del form di login
     */
    @GetMapping(value = "/login")
    public String showLoginForm(Model model) {
        return "formLogin";
    }

    /**
     * Metodo per gestire la home page dopo il login.
     * 
     * @param model il modello della vista
     * @return il nome della vista della home page
     */
    @GetMapping(value = "/")
    public String index(Model model) {
        // Ottiene l'oggetto Authentication dalla SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User utente = null;
        model.addAttribute("tutte_ricette", ricettaService.getAllRicette());

        // Verifica se l'autenticazione è anonima
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            // Se l'utente è anonimo, ritorna la pagina index
            return "index";
        } else {
            // Se l'utente è autenticato, ottiene il principal (l'utente autenticato)
            Object principal = authentication.getPrincipal();

            // Verifica se il principal è un'istanza di UserDetails (un utente autenticato tramite username e password)
            if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;

                // Ottiene le credenziali dell'utente (ad esempio, ruoli e permessi)
                Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());

                // Verifica se l'utente ha il ruolo di amministratore
                if (credentials.getRole().equals(Credentials.ADMIN_ROLE)) {
                    // Se l'utente è un amministratore, ritorna la pagina admin/indexAdmin
                    return "admin/indexAdmin";
                }
                utente = credentialsService.getUserByCredentials(userDetails.getUsername());
                if (!"google".equals(utente.getProvider())) {
                    model.addAttribute("user", utente);
                    model.addAttribute("user_id", utente.getId());
                }
            }
            // Verifica se il principal è un'istanza di DefaultOidcUser (un utente autenticato tramite OpenID Connect)
            else if (authentication instanceof DefaultOidcUser) {
                DefaultOidcUser oidcUser = (DefaultOidcUser) principal;
                utente = userService.getCustomOAuth2UserByEmailAndProvider(oidcUser.getEmail(), "google");
                String email = oidcUser.getEmail();
                // Modifica: Controlla se l'utente esiste con il provider "google", se no lo crea
                if (utente == null) {
                    // Salva l'utente nelle credenziali e negli utenti OAuth
                    userService.saveOauthUser(oidcUser);
                    User utenteOauth = userService.getCustomOAuth2UserByEmail(email);
                    credentialsService.saveCredentialsFromOidcUser(utenteOauth);
                    utente = userService.getCustomOAuth2UserByEmailAndProvider(oidcUser.getEmail(), "google");
                }

                model.addAttribute("user", utente);
                model.addAttribute("user_id", utente.getId());
            }
        }

        // Se nessuna delle condizioni precedenti è soddisfatta, ritorna la pagina index
        return "index";
    }

    /**
     * Metodo per gestire la redirezione dopo il login con successo.
     * 
     * @param authentication l'oggetto di autenticazione
     * @return la redirezione alla vista appropriata
     */
    @GetMapping("/success")
    public String defaultAfterLogin(Authentication authentication) {
        if (authentication.getPrincipal() instanceof DefaultOidcUser) {
            DefaultOidcUser oidcUser = (DefaultOidcUser) authentication.getPrincipal();
            String email = oidcUser.getEmail();

            if (!credentialsService.existsByUsername(email) && !userService.existsByEmailAndProvider(email, "google")) {
                // Salva l'utente nelle credenziali e negli utenti OAuth
                userService.saveOauthUser(oidcUser);
                User utenteOauth = userService.getCustomOAuth2UserByEmail(email);
                credentialsService.saveCredentialsFromOidcUser(utenteOauth);
            }

            return "index.html"; // Gli utenti OIDC sono sempre reindirizzati alla home
        } else if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            // Estrai le informazioni necessarie da userDetails
            String username = userDetails.getUsername();

            // Recupera le credenziali per determinare il ruolo dell'utente
            Credentials credentials = credentialsService.getCredentials(username);
            if (credentials.getRole().equals(Credentials.ADMIN_ROLE)) {
                return "admin/indexAdmin.html"; // Reindirizza gli amministratori alla pagina admin
            }
            return "index.html"; // Reindirizza gli altri utenti alla pagina principale
        }
        return "redirect:/"; // Redirezione di default
    }

    /**
     * Metodo per gestire il logout.
     * 
     * @param request la richiesta HTTP
     * @param response la risposta HTTP
     * @param authentication l'oggetto di autenticazione
     * @return la redirezione alla pagina di login
     */
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        return "redirect:/login?logout";
    }

    /**
     * Metodo per visualizzare il profilo dell'utente.
     * 
     * @param model il modello della vista
     * @return il nome della vista del profilo utente
     */
    @GetMapping("/user")
    public String userProfile(Model model) {
        User user = globalController.getCurrentUser();

        if (user != null) {
            model.addAttribute("user", user);
            model.addAttribute("user_id", user.getId());
            model.addAttribute("ricette", userService.getRicetteByAuthenticatedUser(user));
            return "userProfile";
        } else {
            return "redirect:/login";
        }
    }

    /**
     * Metodo per registrare un nuovo utente.
     * 
     * @param user l'oggetto utente da validare e registrare
     * @param userBindingResult il risultato della validazione dell'oggetto utente
     * @param credentials l'oggetto credenziali da validare e registrare
     * @param credentialsBindingResult il risultato della validazione dell'oggetto credenziali
     * @param model il modello della vista
     * @return la redirezione alla vista appropriata
     */
    @PostMapping(value = { "/register" })
    public String registerUser(@Valid @ModelAttribute("user") User user,
                               BindingResult userBindingResult, @Valid
                               @ModelAttribute("credentials") Credentials credentials,
                               BindingResult credentialsBindingResult,
                               Model model) {

        // Se user e credential hanno entrambi contenuti validi, memorizza User e le Credentials nel DB
        if (!userBindingResult.hasErrors() && !credentialsBindingResult.hasErrors()) {
            if (credentialsService.usernameExists(credentials.getUsername())) {
                model.addAttribute("usernameError", "Username already exists. Please choose another one.");
                return "formRegisterUser";
            }
            userService.saveUser(user);
            credentials.setUser(user);
            credentialsService.saveCredentials(credentials);
            model.addAttribute("user", user);
            return "registrationSuccessful";
        }
        return "registerUser";
    }

}
