package de.fab001.grocee.domain.service;

import de.fab001.grocee.domain.model.Produkt;

import java.util.List;
import java.util.stream.Collectors;


public class Haltbarkeitspruefer {

    public List<Produkt> findeAbgelaufene(List<Produkt> produkte) {
        return produkte.stream()
                .filter(p -> p.getHaltbarkeit().istAbgelaufen())
                .collect(Collectors.toList());
    }

    public List<Produkt> findeKurzVorAblauf(List<Produkt> produkte) {
        return produkte.stream()
                .filter(p -> p.getHaltbarkeit().istKurzVorAblauf())
                .collect(Collectors.toList());
    }
}
