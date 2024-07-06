package it.uniroma3.siw.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Ricetta;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.RicettaService;
import it.uniroma3.siw.service.UserService;
import utils.FileUploadUtil;

/**
 * Controller per gestire le operazioni relative alle ricette.
 */
@Controller
public class RicettaController {

    @Autowired
    private RicettaService ricettaService;

    @Autowired
    private UserService userService;

    @Autowired
    private GlobalController globalController;

    /**
     * Metodo per ottenere una ricetta specifica.
     * 
     * @param id l'ID della ricetta
     * @param model il modello della vista
     * @return il nome della vista che mostra la ricetta
     */
    @GetMapping("/ricetta/{id}")
    public String getRicetta(@PathVariable("id") Long id, Model model) {
        model.addAttribute("ricetta", ricettaService.findById(id));
        return "ricetta.html";
    }

    /**
     * Metodo per ottenere tutte le ricette.
     * 
     * @param model il modello della vista
     * @return il nome della vista che mostra tutte le ricette
     */
    @GetMapping("/ricette")
    public String getAllRicette(Model model) {
        model.addAttribute("tutte_ricette", ricettaService.getAllRicette());
        return "ricette.html";
    }

    /**
     * Metodo per mostrare il form per aggiungere una nuova ricetta.
     * 
     * @param userId l'ID dell'utente
     * @param model il modello della vista
     * @return il nome della vista del form per aggiungere una nuova ricetta
     */
    @GetMapping("/ricetta/new")
    public String showAddRicettaForm(@RequestParam("userId") Long userId, Model model) {
        User utente = userService.getUserById(userId);
        if (utente != null) {
            model.addAttribute("utente", utente);
            model.addAttribute("userId", userId); // Aggiungi userId al modello
            model.addAttribute("ricetta", new Ricetta());
            return "add-ricetta";
        } else {
            return "redirect:/";
        }
    }

    /**
     * Metodo per aggiungere una nuova ricetta.
     * 
     * @param ricetta l'oggetto Ricetta da aggiungere
     * @param userId l'ID dell'utente
     * @param model il modello della vista
     * @return il nome della vista di successo o del form di aggiunta in caso di errore
     */
    @PostMapping("/ricetta/new")
    public String addRicetta(@ModelAttribute("ricetta") Ricetta ricetta, @RequestParam("userId") Long userId, Model model) {
        User utente = userService.getUserById(userId);

        if (utente != null) {
            ricettaService.saveRicetta(ricetta, utente);
            return "success-ricetta";
        } else {
            model.addAttribute("error", "User not found");
            return "add-ricetta";
        }
    }

    /**
     * Metodo per mostrare il form di modifica di una ricetta.
     * 
     * @param id l'ID della ricetta
     * @param model il modello della vista
     * @return il nome della vista del form di modifica della ricetta o una redirezione al login in caso di errore
     */
    @GetMapping("/ricetta/pagina/{id}")
    public String ricettaPaginaModifica(@PathVariable("id") Long id, Model model) {
        User user = globalController.getCurrentUser();
        Ricetta ricetta = ricettaService.findById(id);

        if (ricetta.getUtente().equals(user)) {
            model.addAttribute("user", user);
            model.addAttribute("user_id", user.getId());
            model.addAttribute("ricetta", ricetta);
            return "scheda-ricetta";
        } else {
            return "redirect:/login";
        }
    }

    /**
     * Metodo per aggiornare una ricetta esistente.
     * 
     * @param ricettaId l'ID della ricetta
     * @param ricettaForm l'oggetto Ricetta con i dati aggiornati
     * @param model il modello della vista
     * @return il nome della vista della scheda ricetta o una redirezione al login in caso di errore
     */
    @PostMapping("/ricetta/update/{ricettaId}")
    public String updateRicetta(@PathVariable("ricettaId") Long ricettaId, @ModelAttribute("ricetta") Ricetta ricettaForm, Model model) {
        User utente = globalController.getCurrentUser();
        Ricetta ricetta = ricettaService.findById(ricettaId);

        if (ricetta != null && ricetta.getUtente().equals(utente)) {
            // Aggiorna i campi della ricetta con i valori provenienti dal form
            ricetta.setNome(ricettaForm.getNome());
            ricetta.setDescrizione(ricettaForm.getDescrizione());

            // Mantieni l'utente che ha creato la ricetta
            ricetta.setUtente(utente);

            // Salva la ricetta aggiornata
            ricettaService.saveRicetta(ricetta, ricetta.getUtente());
            model.addAttribute("user", utente);
            model.addAttribute("ricetta", ricetta);
            model.addAttribute("user_id", utente.getId());
            return "scheda-ricetta";
        } else {
            model.addAttribute("error", "User not authorized or recipe not found");
            return "redirect:/login";
        }
    }

    /**
     * Metodo per eliminare una ricetta.
     * 
     * @param id l'ID della ricetta
     * @param model il modello della vista
     * @return la redirezione alla vista utente
     * @throws IOException in caso di errore durante l'eliminazione delle immagini della ricetta
     */
    @PostMapping("/delete/{id}")
    public String deleteRicetta(@PathVariable("id") Long id, Model model) throws IOException {
        User user = globalController.getCurrentUser();
        Ricetta ricetta = ricettaService.findById(id);

        if (ricetta.getUtente().equals(user)) {
            List<String> fotoRicetta = ricetta.getRicettaPictures();
            String uploadDir = "src/main/resources/static/images";
            for (String foto : fotoRicetta) {
                FileUploadUtil.deleteFile(uploadDir, foto);
            }
            ricettaService.deleteById(id);
        }
        return "redirect:/user";
    }

    /**
     * Metodo per salvare una foto per una ricetta.
     * 
     * @param multipartFile il file dell'immagine
     * @param ricettaId l'ID della ricetta
     * @return la redirezione alla pagina della ricetta
     * @throws IOException in caso di errore durante il salvataggio del file
     */
    @PostMapping("/user/ricetta/savePhoto/{ricettaId}")
    public String saveRicettaPhoto(@RequestParam("image") MultipartFile multipartFile, @PathVariable Long ricettaId) throws IOException {
        User user = globalController.getCurrentUser();
        Ricetta ricetta = ricettaService.findById(ricettaId);

        if (ricetta != null && ricetta.getUtente().getId().equals(user.getId())) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            ricetta.getRicettaPictures().add(fileName);

            // Salva la ricetta aggiornata
            ricettaService.saveRicetta(ricetta, user);

            String uploadDir = "src/main/resources/static/images";
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

            return "redirect:/ricetta/pagina/" + ricetta.getId();
        }
        return "redirect:/";
    }

    /**
     * Metodo per rimuovere una foto da una ricetta esistente.
     * 
     * @param imageName il nome dell'immagine da rimuovere
     * @param ricettaId l'ID della ricetta
     * @param model il modello della vista
     * @return la redirezione alla pagina di modifica della ricetta
     * @throws IOException in caso di errori durante l'eliminazione del file
     */
    @PostMapping("/user/ricetta/removePhoto/{ricettaId}")
    public String removeRicettaPhoto(@RequestParam("image") String imageName, @PathVariable("ricettaId") Long ricettaId, Model model) throws IOException {
        Ricetta ricetta = ricettaService.findById(ricettaId);
        if (ricetta != null) {
            ricetta.getRicettaPictures().remove(imageName);
            String uploadDir = "src/main/resources/static/images";
            FileUploadUtil.deleteFile(uploadDir, imageName);
            ricettaService.saveRicetta(ricetta, ricetta.getUtente()); // Assicurati di salvare le modifiche alla ricetta
            return "redirect:/ricetta/pagina/" + ricetta.getId();
        }
        return "redirect:/";
    }

    /**
     * Metodo per mostrare il form di aggiornamento della ricetta per l'amministratore.
     * 
     * @param id l'ID della ricetta
     * @param model il modello della vista
     * @return il nome della vista del form di aggiornamento della ricetta o una redirezione alla vista di tutti gli utenti
     */
    @GetMapping("/admin/formUpdateRicetta/{id}")
    public String adminFormUpdateRicetta(@PathVariable("id") Long id, Model model) {
        Ricetta ricetta = this.ricettaService.findById(id);
        if (ricetta != null) {
            model.addAttribute("ricetta", ricetta);
            return "formUpdateRicetta";
        } else {
            model.addAttribute("error", "Ricetta non trovata");
            return "redirect:/admin/all_user";
        }
    }

    /**
     * Metodo per ottenere una ricetta specifica per l'amministratore.
     * 
     * @param id l'ID della ricetta
     * @param model il modello della vista
     * @return il nome della vista che mostra la ricetta per l'amministratore
     */
    @GetMapping("/admin/ricetta/{id}")
    public String getRicettaAdmin(@PathVariable("id") Long id, Model model) {
        model.addAttribute("ricetta", ricettaService.findById(id));
        return "admin/admin-ricetta.html";
    }

    /**
     * Metodo per ottenere tutte le ricette per l'amministratore.
     * 
     * @param model il modello della vista
     * @return il nome della vista che mostra tutte le ricette per l'amministratore
     */
    @GetMapping("/admin/ricette")
    public String getAllRicetteAdmin(Model model) {
        model.addAttribute("tutte_ricette", ricettaService.getAllRicette());
        return "admin/admin-ricette.html";
    }

    /**
     * Metodo per mostrare il form per aggiungere una nuova ricetta per l'amministratore.
     * 
     * @param userId l'ID dell'utente
     * @param model il modello della vista
     * @return il nome della vista del form per aggiungere una nuova ricetta per l'amministratore
     */
    @GetMapping("/admin/ricetta/new")
    public String showAddRicettaFormAdmin(@RequestParam("userId") Long userId, Model model) {
        User utente = userService.getUserById(userId);
        if (utente != null) {
            model.addAttribute("utente", utente);
            model.addAttribute("userId", userId);
            model.addAttribute("ricetta", new Ricetta());
            return "admin/admin-add-ricetta";
        } else {
            return "redirect:/";
        }
    }

    /**
     * Metodo per aggiungere una nuova ricetta per l'amministratore.
     * 
     * @param ricetta l'oggetto Ricetta da aggiungere
     * @param userId l'ID dell'utente
     * @param model il modello della vista
     * @return il nome della vista di successo o del form di aggiunta in caso di errore
     */
    @PostMapping("/admin/ricetta/new")
    public String addRicettaAdmin(@ModelAttribute("ricetta") Ricetta ricetta, @RequestParam("userId") Long userId, Model model) {
        User utente = userService.getUserById(userId);
        if (utente != null) {
            ricettaService.saveRicetta(ricetta, utente);
            return "admin/admin-success-ricetta";
        } else {
            model.addAttribute("error", "User not found");
            return "admin/admin-add-ricetta";
        }
    }

    /**
     * Metodo per mostrare la pagina di modifica della ricetta per l'amministratore.
     * 
     * @param id l'ID della ricetta
     * @param model il modello della vista
     * @return il nome della vista della scheda ricetta per l'amministratore
     */
    @GetMapping("/admin/ricetta/pagina/{id}")
    public String ricettaPaginaModificaAdmin(@PathVariable("id") Long id, Model model) {
        User user = globalController.getCurrentUser();
        Ricetta ricetta = ricettaService.findById(id);

        model.addAttribute("user", user);
        model.addAttribute("user_id", user.getId());
        model.addAttribute("ricetta", ricetta);
        return "admin/admin-scheda-ricetta";
    }

    /**
     * Metodo per aggiornare una ricetta esistente per l'amministratore.
     * 
     * @param ricettaId l'ID della ricetta
     * @param ricettaForm l'oggetto Ricetta con i dati aggiornati
     * @param model il modello della vista
     * @return il nome della vista della scheda ricetta per l'amministratore o una redirezione al login in caso di errore
     */
    @PostMapping("/admin/ricetta/update/{ricettaId}")
    public String updateRicettaAdmin(@PathVariable("ricettaId") Long ricettaId, @ModelAttribute("ricetta") Ricetta ricettaForm, Model model) {
        User utente = globalController.getCurrentUser();
        Ricetta ricetta = ricettaService.findById(ricettaId);
        if (ricetta != null) {
            ricetta.setNome(ricettaForm.getNome());
            ricetta.setDescrizione(ricettaForm.getDescrizione());
            ricetta.setUtente(ricetta.getUtente());
            ricettaService.saveRicetta(ricetta, ricetta.getUtente());
            model.addAttribute("user", utente);
            model.addAttribute("ricetta", ricetta);
            model.addAttribute("user_id", utente.getId());
            return "admin/admin-scheda-ricetta";
        } else {
            model.addAttribute("error", "User not authorized or recipe not found");
            return "redirect:/admin/login";
        }
    }

    /**
     * Metodo per eliminare una ricetta per l'amministratore.
     * 
     * @param id l'ID della ricetta
     * @param model il modello della vista
     * @return la redirezione alla vista delle ricette dell'amministratore
     * @throws IOException in caso di errore durante l'eliminazione delle immagini della ricetta
     */
    @PostMapping("/admin/delete/{id}")
    public String deleteRicettaAdmin(@PathVariable("id") Long id, Model model) throws IOException {
        Ricetta ricetta = ricettaService.findById(id);

        List<String> fotoRicetta = ricetta.getRicettaPictures();
        String uploadDir = "src/main/resources/static/images";
        for (String foto : fotoRicetta) {
            FileUploadUtil.deleteFile(uploadDir, foto);
        }
        ricettaService.deleteById(id);
        return "redirect:/admin/ricette";
    }

    /**
     * Metodo per salvare una foto per una ricetta per l'amministratore.
     * 
     * @param multipartFile il file dell'immagine
     * @param ricettaId l'ID della ricetta
     * @return la redirezione alla pagina della ricetta
     * @throws IOException in caso di errore durante il salvataggio del file
     */
    @PostMapping("/admin/user/ricetta/savePhoto/{ricettaId}")
    public String saveRicettaPhotoAdmin(@RequestParam("image") MultipartFile multipartFile, @PathVariable Long ricettaId) throws IOException {
        User user = globalController.getCurrentUser();
        Ricetta ricetta = ricettaService.findById(ricettaId);
        if (ricetta != null && ricetta.getUtente().getId().equals(user.getId())) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            ricetta.getRicettaPictures().add(fileName);
            ricettaService.saveRicetta(ricetta, user);
            String uploadDir = "src/main/resources/static/images";
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
            return "redirect:/";
        }
        return "redirect:/";
    }

    /**
     * Metodo per rimuovere una foto da una ricetta esistente per l'amministratore.
     * 
     * @param imageName il nome dell'immagine da rimuovere
     * @param ricettaId l'ID della ricetta
     * @param model il modello della vista
     * @return la redirezione alla pagina di modifica della ricetta per l'amministratore
     * @throws IOException in caso di errori durante l'eliminazione del file
     */
    @PostMapping("/admin/user/ricetta/removePhoto/{ricettaId}")
    public String removeRicettaPhotoAdmin(@RequestParam("image") String imageName, @PathVariable("ricettaId") Long ricettaId, Model model) throws IOException {
        Ricetta ricetta = ricettaService.findById(ricettaId);
        if (ricetta != null) {
            ricetta.getRicettaPictures().remove(imageName);
            String uploadDir = "src/main/resources/static/images";
            FileUploadUtil.deleteFile(uploadDir, imageName);
            ricettaService.saveRicetta(ricetta, ricetta.getUtente());
            return "redirect:/admin/ricetta/pagina/" + ricetta.getId();
        }
        return "redirect:/";
    }
}
