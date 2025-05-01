package de.fab001.grocee.domain.model;

import java.util.*;

public class Einkaufsliste {
    private final UUID id;
    private final List<Produkt> produkte = new ArrayList<>();

    public Einkaufsliste() {
        this.id = UUID.randomUUID();
    }

    public void produktHinzufügen(Produkt produkt) {
        Optional<Produkt> existierendes = produkte.stream()
            .filter(p -> p.getName().equalsIgnoreCase(produkt.getName()))
            .findFirst();

        if (existierendes.isPresent()) {
            throw new IllegalArgumentException("Produkt existiert bereits in der Liste");
        }

        produkte.add(produkt);
    }

    public List<Produkt> getProdukte() {
        return Collections.unmodifiableList(produkte);
    }

    public UUID getId() {
        return id;
    }
}
