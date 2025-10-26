package ma.gstock.services.dto;

import java.util.List;

public record CreateCommandeInput(Long clientId, Long vendeurId, List<LigneCommandeInput> lignes) { }
