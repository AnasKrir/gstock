// ma/gstock/controllers/AdminClientController.java
package ma.gstock.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.gstock.entities.Client;
import ma.gstock.repositories.ClientRepository;
import ma.gstock.repositories.CommandeRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/clients")
public class AdminClientController {

    private final ClientRepository clientRepository;
    private final CommandeRepository commandeRepository;

    @GetMapping
    public String list(@RequestParam(name = "q", required = false) String q,
                       Model model,
                       CsrfToken csrfToken) { // force le token
        List<Client> clients = (q == null || q.isBlank())
                ? clientRepository.findAllOrderByIdDesc()
                : clientRepository.findByNomContainingIgnoreCaseOrderByIdDesc(q.trim());
        model.addAttribute("clients", clients);
        model.addAttribute("q", q);
        return "admin/clients/list";
    }

    @GetMapping("/new")
    public String newForm(Model model, CsrfToken csrfToken) {
        model.addAttribute("client", new Client());
        return "admin/clients/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id,
                           Model model,
                           CsrfToken csrfToken,
                           RedirectAttributes ra) {
        return clientRepository.findById(id)
                .map(c -> { model.addAttribute("client", c); return "admin/clients/form"; })
                .orElseGet(() -> {
                    ra.addFlashAttribute("error", "Client introuvable.");
                    return "redirect:/admin/clients";
                });
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("client") Client client,
                       BindingResult br,
                       RedirectAttributes ra) {
        if (br.hasErrors()) return "admin/clients/form";
        clientRepository.save(client);
        ra.addFlashAttribute("success", "Client enregistré.");
        return "redirect:/admin/clients";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        if (!clientRepository.existsById(id)) {
            ra.addFlashAttribute("error", "Client introuvable.");
            return "redirect:/admin/clients";
        }
        if (commandeRepository.existsByClientId(id)) {
            ra.addFlashAttribute("error", "Suppression impossible : client lié à des commandes.");
            return "redirect:/admin/clients";
        }
        clientRepository.deleteById(id);
        ra.addFlashAttribute("success", "Client supprimé.");
        return "redirect:/admin/clients";
    }
}
