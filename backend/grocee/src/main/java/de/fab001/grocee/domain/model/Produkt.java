package de.fab001.grocee.domain.model;

import java.util.Objects;
import java.util.UUID;

public class Produkt {
    private final UUID id;
    private String name;
    private String kategorie;
    private String marke;
    private Haltbarkeitsdatum haltbarkeit;

    public Produkt(String name, String kategorie, String marke, Haltbarkeitsdatum haltbarkeit) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.kategorie = kategorie;
        this.marke = marke;
        this.haltbarkeit = haltbarkeit;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Haltbarkeitsdatum getHaltbarkeit() {
        return haltbarkeit;
    }

    public void aktualisiereHaltbarkeit(Haltbarkeitsdatum neu) {
        this.haltbarkeit = neu;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Produkt)) return false;
        Produkt produkt = (Produkt) o;
        return id.equals(produkt.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
