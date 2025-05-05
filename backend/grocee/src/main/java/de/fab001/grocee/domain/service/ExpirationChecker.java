package de.fab001.grocee.domain.service;

import de.fab001.grocee.domain.model.Product;

import java.util.List;


public class ExpirationChecker {

    public List<Product> findExpired(List<Product> products) {
        return products.stream()
                .filter(p -> p.gExpirationDate().isExpired())
                .toList();
    }

    public List<Product> findCloseToExpire(List<Product> products) {
        return products.stream()
                .filter(p -> p.gExpirationDate().isCloseToExpire())
                .toList();
    }
}
