package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.model.Ingrediente;
import it.uniroma3.siw.model.Ricetta;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.IngredienteService;
import it.uniroma3.siw.service.RicettaService;

/**
 * Controller per gestire le operazioni sugli ingredienti nelle ricette.
 */
@Controller
public class IngredienteController {

    @Autowired
    private RicettaService ricettaService;

    @Autowired
    private IngredienteService ingredienteService;

    @Autowired
    private GlobalController globalController;

    /**
     * Metodo per ottenere tutti gli ingredienti.
     * 
     * @param model il modello della vista
     * @return il nome della vista che mostra tutti gli ingredienti
     */
    @GetMapping("/ingredienti")
    public String getAllIngredienti(Model model) {
        model.addAttribute("tutti_ingredienti", ingredienteService.getAllIngredienti());
        return "ingredienti.html";
    }

    /**
     * Metodo per eliminare un ingrediente da una ricetta.
     * 
     * @param id l'ID dell'ingrediente da eliminare
     * @param id_ricetta l'ID della ricetta da cui eliminare l'ingrediente
     * @param model il modello della vista
     * @return il nome della vista della scheda ricetta o una vista di errore
     */
    @PostMapping("/ingrediente/delete/{id}/ricetta/{id_ricetta}")
    public String deleteIngrediente(@PathVariable("id") Long id, @PathVariable("id_ricetta") Long id_ricetta, Model model) {
        User user = globalController.getCurrentUser();
        Ricetta ricetta = ricettaService.findById(id_ricetta);

        if (ricetta.getUtente().equals(user)) {
            // Rimuove l'ingrediente dalla lista degli ingredienti della ricetta
            ricetta.getIngredienti().removeIf(ingrediente -> ingrediente.getId().equals(id));
            // Salva la ricetta aggiornata
            ricettaService.saveRicetta(ricetta, ricetta.getUtente());
            // Elimina l'ingrediente
            ingredienteService.deleteById(id);
            model.addAttribute("ricetta", ricetta);
            model.addAttribute("user", user);
            model.addAttribute("user_id", user.getId());

            return "scheda-ricetta";
        } else {
            return "erroreIngrediente.html";
        }
    }

    /**
     * Metodo per mostrare il form per aggiungere un ingrediente a una ricetta.
     * 
     * @param ricettaId l'ID della ricetta
     * @param model il modello della vista
     * @return il nome della vista del form per aggiungere un ingrediente
     */
    @GetMapping("/ricetta/{ricettaId}/ingredient/add")
    public String getAddIngredientForm(@PathVariable Long ricettaId, Model model) {
        Ricetta ricetta = ricettaService.findById(ricettaId);
        User currentUser = globalController.getCurrentUser();

        if (ricetta != null && ricetta.getUtente().equals(currentUser)) {
            model.addAttribute("ricetta", ricetta);
            model.addAttribute("ingrediente", new Ingrediente());
            return "add-ingrediente";
        } else {
            return "redirect:/user";
        }
    }

    /**
     * Metodo per aggiungere un ingrediente a una ricetta.
     * 
     * @param ricettaId l'ID della ricetta
     * @param ingrediente l'ingrediente da aggiungere
     * @param model il modello della vista
     * @return il nome della vista della scheda ricetta o una redirezione alla vista utente
     */
    @PostMapping("/ricetta/{ricettaId}/ingredient/add")
    public String addIngredient(@PathVariable Long ricettaId, @ModelAttribute Ingrediente ingrediente, Model model) {
        Ricetta ricetta = ricettaService.findById(ricettaId);
        User currentUser = globalController.getCurrentUser();

        if (ricetta != null && ricetta.getUtente().equals(currentUser)) {
            // Aggiunge l'ingrediente alla ricetta
            ricettaService.addIngredientToRicetta(ricetta, ingrediente);
            model.addAttribute("user", currentUser);
            model.addAttribute("ricetta", ricetta);
            model.addAttribute("user_id", currentUser.getId());
            return "scheda-ricetta";
        } else {
            return "redirect:/user";
        }
    }
}
