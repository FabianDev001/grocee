# GROCEE
---


Inhaltsangabe:


# 1. Domain Driven Design

## 1.1 Ubiquitous Language (UL)

Die folgende Tabelle dokumentiert zentrale Begriffe der Domäne „Einkaufslistenplaner für Einzelhaushalte und Wohngemeinschaften“. Diese Begriffe sind Grundlage für den Code, die Kommunikation und alle Projektartefakte.

| Begriff            | Beschreibung                                                                 |
|--------------------|------------------------------------------------------------------------------|
| **Produkt**         | Ein Artikel, der eingekauft werden soll, inkl. Name, Marke, Kategorie, Haltbarkeit |
| **Einkaufsliste**   | Sammlung geplanter Produkte (pro Benutzer oder WG)                           |
| **Haltbarkeit**     | Datum, bis wann ein Produkt verwendet werden sollte                         |
| **Kostenaufteilung**| Funktion zur Aufteilung der Einkaufskosten in einer WG                      |
| **Budget**          | Monatliches Limit für Ausgaben in Einzelhaushalten                          |
| **Historie**        | Liste vergangener Einkäufe                                                   |
| **Benutzer**        | Person, die die App nutzt – alleine oder in einer WG                        |
| **WG-Mitglied**     | Benutzer innerhalb einer Wohngemeinschaft                                   |

//TODO Update Namen ins Englische

Diese Begriffe werden 1:1 in Klassen, Methoden und UI übernommen, um ein durchgängiges Vokabular sicherzustellen.
// Angaben könnnen sich im Laufe der Entwicklung angepasst oder hinzugefügt werden

---

## 🧱 2. Verwendete DDD-Muster

| Muster              | Beispiel im Projekt                         | Begründung                                                                 |
|---------------------|---------------------------------------------|----------------------------------------------------------------------------|
| **Entity**          | `Produkt`, `Benutzer`, `WG`                 | Eindeutige Identität und veränderlicher Zustand                            |
| **Value Object**    | `Haltbarkeitsdatum`, `Preis`, `ProduktMenge`| Beschreiben nur Werte, keine eigene Identität, unveränderlich              |
| **Aggregate**       | `Einkaufsliste`                             | Gruppiert Produkte, sichert Invarianten (keine Duplikate)                 |
| **Repository**      | `ProduktRepository`, `EinkaufslisteRepository` | Kapselt den Zugriff auf Persistenz, unabhängig von konkreter Technik     |
| **Domain Service**  | `KostenaufteilungService`, `BudgetCheckService` | Geschäftslogik, die keiner konkreten Entity zugeordnet ist               |
// TODO Update Namen ins Englische
---

### ✅ Muster-Begründungen

- `Produkt` ist eine **Entity**, da es über eine ID verfügt (`produktId`) und seinen Zustand (z. B. Menge, Haltbarkeit) verändern kann.
- `Preis` ist ein **Value Object**, da zwei Preis-Objekte mit gleichem Wert als gleich gelten. Es ist unveränderlich.
- `Einkaufsliste` ist ein **Aggregate**, das für Konsistenz sorgt (z. B. keine doppelten Produkte) und als Einheit geladen/gespeichert wird.
- `KostenaufteilungService` ist ein **Domain Service**, da er Logik enthält, die sich über mehrere Entitäten erstreckt (z. B. WG-Mitglieder, Produkte).
- `ProduktRepository` ist ein **Repository**, das Methoden wie `findeProduktMitName(String name)` bereitstellt – vollständig in der Sprache der Domäne gehalten.

---

### 📌 Nächster Schritt

Im nächsten Schritt folgt die Umsetzung der Clean Architecture und die Zuordnung der Klassen zu den Schichten: **Domain**, **Application**, **Adapters**, **Plugins**.




# 2. Schichtarchitektur planen und begründen


# 4. Refactoring

Im Rahmen des Refactoring sollten wir typische Code Smells identifizieren und beseitigen:

//TODO Füge Beschreibung für kommenden Commit rein