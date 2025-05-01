package de.fab001.grocee.domain.repository;

import de.fab001.grocee.domain.model.Einkaufsliste;

import java.util.Optional;
import java.util.UUID;

public interface EinkaufslisteRepository {
    Optional<Einkaufsliste> findeNachId(UUID id);
    void speichern(Einkaufsliste liste);
}
