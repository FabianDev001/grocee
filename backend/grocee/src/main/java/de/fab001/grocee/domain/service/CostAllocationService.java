package de.fab001.grocee.domain.service;

import de.fab001.grocee.domain.model.Product;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CostAllocationService {

    public Map<UUID, Map<UUID, Double>> calculateOpenDebts(List<Product> products) {
        Map<UUID, Map<UUID, Double>> debts = new HashMap<>();
        for (Product p : products) {
            if (!p.isPaid() && p.getNeededBy() != null && p.getBoughtBy() != null && !p.getNeededBy().equals(p.getBoughtBy())) {
                UUID debtor = UUID.fromString(p.getNeededBy());
                UUID creditor = UUID.fromString(p.getBoughtBy());
                double amount = p.getPrice();
                debts.computeIfAbsent(debtor, k -> new HashMap<>());
                debts.get(debtor).merge(creditor, amount, Double::sum);
            }
        }
        return debts;
    }
} 