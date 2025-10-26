package ma.gstock.services;

import ma.gstock.entities.*;
import ma.gstock.entities.enums.StatutCommande;
import ma.gstock.exceptions.StockInsuffisantException;
import ma.gstock.repositories.*;
import ma.gstock.services.dto.CreateCommandeInput;
import ma.gstock.services.dto.LigneCommandeInput;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class CommandeService {
    private final CommandeRepository commandeRepo;
    private final LigneCommandeRepository ligneRepo;
    private final ClientRepository clientRepo;
    private final UtilisateurRepository utilisateurRepo;
    private final ProduitRepository produitRepo;
    private final StockService stockService;

    public CommandeService(CommandeRepository commandeRepo, LigneCommandeRepository ligneRepo,
                           ClientRepository clientRepo, UtilisateurRepository utilisateurRepo,
                           ProduitRepository produitRepo, StockService stockService) {
        this.commandeRepo = commandeRepo;
        this.ligneRepo = ligneRepo;
        this.clientRepo = clientRepo;
        this.utilisateurRepo = utilisateurRepo;
        this.produitRepo = produitRepo;
        this.stockService = stockService;
    }

    /** Création stricte : on décrémente le stock au fil de l’eau ; si une ligne échoue -> exception -> rollback. */
    @Transactional
    public Commande creerCommande(CreateCommandeInput input) {
        Client client = clientRepo.findById(input.clientId())
                .orElseThrow(() -> new IllegalArgumentException("Client introuvable"));
        Utilisateur vendeur = utilisateurRepo.findById(input.vendeurId())
                .orElseThrow(() -> new IllegalArgumentException("Vendeur introuvable"));

        Commande cmd = Commande.builder()
                .client(client)
                .vendeur(vendeur)
                .statut(StatutCommande.EN_ATTENTE)
                .total(BigDecimal.ZERO)
                .build();
        cmd = commandeRepo.save(cmd);

        BigDecimal total = BigDecimal.ZERO;

        for (LigneCommandeInput l : input.lignes()) {
            Produit p = produitRepo.findById(l.produitId())
                    .orElseThrow(() -> new IllegalArgumentException("Produit introuvable: " + l.produitId()));

            int updated = produitRepo.decrementStockIfEnough(p.getId(), l.quantite());
            if (updated == 0) {
                throw new StockInsuffisantException(
                        "Stock insuffisant pour \"" + p.getNom() + "\". Quantité demandée: " + l.quantite());
            }

            BigDecimal prixU = p.getPrix();
            BigDecimal totalLigne = prixU.multiply(BigDecimal.valueOf(l.quantite()));

            LigneCommande lc = LigneCommande.builder()
                    .commande(cmd)
                    .produit(p)
                    .quantite(l.quantite())
                    .prixUnitaire(prixU)
                    .totalLigne(totalLigne)
                    .build();
            ligneRepo.save(lc);

            total = total.add(totalLigne);
        }

        cmd.setTotal(total);
        return commandeRepo.save(cmd);
    }

    /** Pour valider une ancienne commande en attente : on tente le décrément atomique pour chaque ligne. */
    @Transactional
    public void validerCommande(Long commandeId) {
        Commande cmd = commandeRepo.findById(commandeId)
                .orElseThrow(() -> new IllegalArgumentException("Commande introuvable"));

        if (cmd.getStatut() == StatutCommande.VALIDEE) return;

        for (LigneCommande l : cmd.getLignes()) {
            Produit p = l.getProduit();
            int updated = produitRepo.decrementStockIfEnough(p.getId(), l.getQuantite());
            if (updated == 0) {
                throw new StockInsuffisantException(
                        "Stock insuffisant pour \"" + p.getNom() + "\". Quantité demandée: " + l.getQuantite());
            }
        }
        cmd.setStatut(StatutCommande.VALIDEE);
        commandeRepo.save(cmd);
    }

    /** Annulation : on réapprovisionne (si tu préfères, ne ré-augmente que si la commande était VALIDEE). */
    @Transactional
    public void annulerCommande(Long commandeId) {
        Commande cmd = commandeRepo.findById(commandeId)
                .orElseThrow(() -> new IllegalArgumentException("Commande introuvable"));

        if (cmd.getStatut() == StatutCommande.ANNULEE) return;

        cmd.getLignes().forEach(l ->
                stockService.incrementerStock(l.getProduit().getId(), l.getQuantite())
        );

        cmd.setStatut(StatutCommande.ANNULEE);
        commandeRepo.save(cmd);
    }

    @Transactional
    public Commande changerStatut(Long id, StatutCommande statut) {
        Commande cmd = commandeRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Commande introuvable"));
        cmd.setStatut(statut);
        return commandeRepo.save(cmd);
    }
}
