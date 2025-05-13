# GROCEE
---

# Inhaltsverzeichnis
1. Domain Driven Design
    1.1 Ubiquitous Language
    1.2 Analyse und Begründung der verwendeten Muster
2. Clean Architecture
3. Programming Principles
4. Unit Tests
5. Refactoring
6. Entwurfsmuster
7. Use Cases
    7.1 Neue Lebensmittel zur Einkaufsliste hinzufügen
    7.2 Haltbarkeit prüfen
    7.3 Kostenaufteilung durchführen
    7.4 Doppelte Produkte verhindern

---

# 1. Domain Driven Design

## 1.1 Ubiquitous Language

Im Projekt "Grocee" verwenden wir eine einheitliche, domänenspezifische Sprache, um Missverständnisse zu vermeiden und die Kommunikation zwischen Entwicklern und Stakeholdern zu erleichtern. Die wichtigsten Begriffe sind:

| Begriff           | Beschreibung                                                                 |
|-------------------|------------------------------------------------------------------------------|
| **Product**       | Ein Artikel, der eingekauft werden soll, inkl. Name, Marke, Kategorie, Haltbarkeit |
| **Shopping List** | Sammlung geplanter Produkte (pro Benutzer oder WG)                           |
| **Expiration Date** | Datum, bis wann ein Produkt verwendet werden sollte                         |
| **Cost Allocation** | Funktion zur Aufteilung der Einkaufskosten in einer WG                      |
| **Budget**        | Monatliches Limit für Ausgaben in Einzelhaushalten                           |
| **History**       | Liste vergangener Einkäufe                                                   |
| **User**          | Person, die die App nutzt – alleine oder in einer WG                         |
| **WG Member**     | Benutzer innerhalb einer Wohngemeinschaft                                    |

Diese Begriffe werden konsequent im Code, in der Dokumentation und in der UI verwendet. So stellen wir sicher, dass alle Beteiligten dieselbe Sprache sprechen und die Domäne klar abgebildet wird.

## 1.2 Analyse und Begründung der verwendeten Muster (DDD)

Im Projekt werden die wichtigsten taktischen Muster des Domain Driven Design (DDD) eingesetzt, um die Komplexität der Domäne sauber abzubilden und den Code wartbar zu halten:

- **Entity:**  Beispiel: `Product`  – Ein Produkt hat eine eindeutige ID (`UUID`) und kann sich im Laufe der Zeit verändern (z.B. Name, Haltbarkeit). Zwei Produkte mit unterschiedlicher ID sind immer verschieden, auch wenn alle anderen Attribute gleich sind.
- **Value Object:**  Beispiel: `ExpirationDate`  – Das Haltbarkeitsdatum ist ein unveränderliches Objekt. Zwei `ExpirationDate`-Objekte mit demselben Datum sind gleich. Value Objects haben keine eigene Identität und werden nach ihrem Wert verglichen.
- **Aggregate:**  Beispiel: `ShoppingList`  – Die Einkaufsliste ist ein Aggregate, das mehrere Produkte gruppiert und für deren Konsistenz sorgt (z.B. keine doppelten Produktnamen). Änderungen an Produkten erfolgen immer über das Aggregate.
- **Repository:**  Beispiel: `ShoppingListRepository`  – Das Repository kapselt den Zugriff auf die Persistenzschicht. Die Anwendung arbeitet nur mit dem Interface, nicht mit der konkreten Implementierung (z.B. InMemory oder Datenbank).
- **Domain Service:**  Beispiel: `AddProductToShoppingListService`  – Enthält Logik, die nicht zu einer einzelnen Entity gehört, sondern mehrere Aggregate oder Objekte betrifft (z.B. das Hinzufügen eines Produkts zur Liste).

**Begründung:**  Durch den Einsatz dieser Muster bleibt die Domänenlogik klar strukturiert, testbar und unabhängig von technischen Details. Änderungen an der Infrastruktur (z.B. Datenbank) haben keinen Einfluss auf die Fachlogik.

---

# 2. Clean Architecture

Um die Wartbarkeit und Erweiterbarkeit des Projekts zu gewährleisten, orientiert sich die Architektur an den Prinzipien der Clean Architecture. Die wichtigsten Schichten sind:

- **Domain Layer:**  Enthält die zentralen Geschäftsobjekte (Entities, Value Objects, Aggregates) und Interfaces (z.B. Repositories). Hier liegt die eigentliche Fachlogik, komplett unabhängig von technischen Details.
- **Application Layer:**  Beinhaltet die Use Cases (z.B. `AddProductToShoppingListService`). Diese Schicht koordiniert die Abläufe und nutzt die Domain-Objekte, bleibt aber ebenfalls unabhängig von Infrastruktur.
- **Adapters Layer:**  Hier befinden sich die Schnittstellen zur Außenwelt, z.B. REST-Controller (`ShoppingListController`) und die InMemory-Implementierung des Repositories. Diese Schicht "übersetzt" zwischen der Außenwelt (HTTP, Datenbank) und der Anwendung.
- **(Plugins Layer):**  Optional für z.B. Datenbank- oder externe Service-Anbindungen. Im aktuellen Stand ist nur ein InMemory-Repository implementiert.

**Begründung:**  Durch die klare Trennung der Schichten ist der Code leicht testbar, neue Features können ohne große Seiteneffekte ergänzt werden und technische Details (wie Persistenz) lassen sich einfach austauschen. Im aktuellen Stand sind mindestens zwei Schichten (Domain und Application) vollständig umgesetzt, die Adapters-Schicht ist für REST und InMemory-Repo ebenfalls vorhanden.

**Schichtendiagramm (vereinfacht):**
```
[Adapters/Controller/Repo] <-> [Application/Use Cases] <-> [Domain/Model]
```

---

# 3. Programming Principles

> Durch die konsequente Anwendung dieser Prinzipien bleibt der Code übersichtlich, leicht testbar und kann ohne große Seiteneffekte erweitert werden.

Im Projekt werden verschiedene bewährte Programmierprinzipien angewendet, um die Codequalität, Wartbarkeit und Erweiterbarkeit zu sichern. Hier sind fünf Prinzipien, die explizit umgesetzt wurden – jeweils mit kurzer Begründung und Beispiel aus dem Code:

- **Single Responsibility Principle (SRP, SOLID):**  Jede Klasse hat genau eine klar abgegrenzte Aufgabe. Beispiel: `Product` speichert nur Produktdaten. `AddProductToShoppingListService` ist ausschließlich für das Hinzufügen von Produkten zur Liste zuständig.
- **Don't Repeat Yourself (DRY):**  Wiederholte Logik wird vermieden. Beispiel: Die Prüfung auf doppelte Produktnamen findet nur in `ShoppingList.addProduct()` statt und nicht an mehreren Stellen.
- **Open/Closed Principle (SOLID):**  Das System ist offen für Erweiterungen, aber geschlossen für Modifikationen. Beispiel: Neue Arten von Repositories (z.B. Datenbank, InMemory) können hinzugefügt werden, ohne die Anwendungsschicht zu ändern.
- **Dependency Inversion Principle (SOLID):**  High-Level-Module hängen nicht von Low-Level-Modulen ab, sondern von Abstraktionen. Beispiel: Der Controller und die Services arbeiten nur mit dem Interface `ShoppingListRepository`, nicht mit der konkreten Implementierung.
- **Information Expert (GRASP):**  Die Klasse, die die meisten Informationen hat, übernimmt die Verantwortung. Beispiel: `ShoppingList` prüft selbst, ob ein Produkt schon existiert, da sie die Produktliste kennt.


---

# 4. Unit Tests

Im Projekt werden Unit Tests nach den ATRIP-Regeln umgesetzt:

- **Automatic:** Die Tests laufen automatisiert per Maven/CI.
- **Thorough:** Es werden verschiedene Fälle getestet (z.B. Erfolg, Fehler, Randfälle).
- **Repeatable:** Die Tests liefern bei jedem Durchlauf das gleiche Ergebnis.
- **Independent:** Die Tests beeinflussen sich nicht gegenseitig.
- **Professional:** Die Tests sind sauber geschrieben und dokumentiert.

**Umsetzung im Projekt:**
- Es existieren (bzw. werden noch ergänzt) mindestens 10 Unit Tests, die die wichtigsten Use Cases und Logik abdecken.
- Für die Services werden Mocks verwendet, um Abhängigkeiten wie das Repository zu isolieren (z.B. mit Mockito).
- Integrationstests prüfen die REST-API mit MockMvc.

**Beispiel für einen Unit Test (JUnit 5):**
```java
@Test
void addProductToList_success() throws Exception {
    UUID listId = UUID.randomUUID();
    ShoppingList list = new ShoppingList(listId);
    shoppingListRepository.safe(list);

    Product product = new Product("Banana", "Fruit", "Chiquita", new ExpirationDate(LocalDate.now().plusDays(5)));

    mockMvc.perform(post("/shoppinglists/" + listId + "/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(product)))
            .andExpect(status().isCreated());
}
```

**Mocks:**  Für die Tests der Service-Klassen werden Mocks für das Repository verwendet, um die Logik unabhängig von der Persistenz zu testen.

---

# 5. Refactoring

> Refactorings sorgen dafür, dass der Code übersichtlicher, leichter testbar und besser wartbar ist. Änderungen an der Infrastruktur oder Erweiterungen der Fachlogik können so mit minimalem Risiko umgesetzt werden.

Im Laufe der Entwicklung wurden verschiedene Code Smells identifiziert und gezielt durch Refactorings behoben, um die Codequalität und Wartbarkeit zu verbessern.

**Identifizierte Code Smells:**
- **Large Class:** Zu große Klassen mit zu vielen Verantwortlichkeiten (z.B. ursprüngliche ShoppingList mit zu viel Logik).
- **Long Method:** Methoden, die zu viel auf einmal machen (z.B. Validierungs- und Hinzufügelogik in einer Methode).
- **Primitive Obsession:** Verwendung von primitiven Datentypen statt Value Objects (z.B. für das Haltbarkeitsdatum).
- **Inconsistent Naming:** Unterschiedliche oder zu lange Bezeichner, teils auf Deutsch, teils auf Englisch.

**Durchgeführte Refactorings:**
- **Extract Class/Method:** Die Logik wurde auf mehrere kleinere Methoden und Klassen verteilt (z.B. separate Service-Klasse für Use Cases).
- **Rename/Unify Naming:** Alle Klassen-, Methoden- und Variablennamen wurden auf Englisch vereinheitlicht, um Lesbarkeit und Wartbarkeit zu erhöhen.
- **Introduce Value Object:** Das Haltbarkeitsdatum wurde als eigenes Value Object (`ExpirationDate`) modelliert.
- **Interface Extraction:** Für die Persistenz wurde ein Repository-Interface eingeführt, um die Infrastruktur von der Domäne zu trennen.

**Beispiel für ein Refactoring:**
Vorher (Primitive Obsession & Inconsistent Naming):
```java
// Ursprünglich: Haltbarkeitsdatum als String, deutsche Namen
private String haltbarkeitsdatum;
```
Nachher (Value Object & einheitliches Englisch):
```java
// Nach Refactoring: Value Object für das Haltbarkeitsdatum
private ExpirationDate expiration;
```

---

# 6. Entwurfsmuster

Im Projekt wird das **Repository-Pattern** als zentrales Entwurfsmuster eingesetzt.

**Einsatz und Begründung:**

Das Repository-Pattern trennt die Domänenschicht von der Persistenzschicht. Die Anwendung arbeitet ausschließlich mit dem Interface `ShoppingListRepository` und kennt keine Details der konkreten Implementierung (z.B. InMemory, Datenbank, etc.). Dadurch bleibt die Fachlogik unabhängig von technischen Details und kann leicht getestet oder erweitert werden.

**Vorteile:**
- **Austauschbarkeit:** Die Persistenz kann einfach gewechselt werden (z.B. von InMemory auf Datenbank), ohne dass die Anwendungsschicht angepasst werden muss.
- **Testbarkeit:** Für Unit Tests kann eine einfache InMemory-Implementierung oder ein Mock verwendet werden.
- **Klarere Verantwortlichkeiten:** Die Domäne kümmert sich nur um die Geschäftslogik, das Repository um die Datenhaltung.

**Beispiel aus dem Code:**
```java
// Interface in der Domäne
public interface ShoppingListRepository {
    Optional<ShoppingList> findById(UUID id);
    void safe(ShoppingList liste);
}

// InMemory-Implementierung (z.B. für Tests)
@Repository
public class InMemoryShoppingListRepository implements ShoppingListRepository {
    private final Map<UUID, ShoppingList> store = new HashMap<>();
    // ...
}
```

Das Repository-Pattern wurde gewählt, weil es die saubere Trennung von Fachlogik und Technik fördert und die Anwendung flexibel und testbar macht. Gerade bei wachsender Komplexität (z.B. späterer Datenbankanbindung) ist dieses Muster ein zentraler Baustein für nachhaltige Softwarearchitektur.

---

# 7. Use Cases

## 7.1 Neue Lebensmittel zur Einkaufsliste hinzufügen

### Beschreibung
Als Benutzer kann ich ein neues Produkt zur Einkaufsliste hinzufügen. Ein Produkt besteht aus Name, Kategorie, bevorzugter Marke und einem Haltbarkeitsdatum. Der Produktname muss eindeutig sein – existiert bereits ein Produkt mit gleichem Namen in der Liste, wird das Hinzufügen verweigert.

### Technische Umsetzung
- Die Klasse `ShoppingList` (siehe `src/main/java/de/fab001/grocee/domain/model/ShoppingList.java`) enthält die Methode `addProduct(Product product)`. Diese prüft, ob bereits ein Produkt mit gleichem Namen (case-insensitive) in der Liste existiert. Falls ja, wird eine Exception geworfen.
- Das Domain-Objekt `Product` (siehe `src/main/java/de/fab001/grocee/domain/model/Product.java`) kapselt die Attribute Name, Kategorie, bevorzugte Marke und Haltbarkeitsdatum (`ExpirationDate`).
- Die Service-Klasse `AddProductToShoppingListService` (siehe `src/main/java/de/fab001/grocee/application/usecase/AddProductToShoppingListService.java`) übernimmt die Koordination: Sie lädt die Einkaufsliste aus dem Repository, ruft `addProduct` auf und speichert die Liste wieder ab.
- Die Eindeutigkeit des Produktnamens wird ausschließlich auf Ebene der Einkaufsliste geprüft, nicht global.
- Es existiert ein REST-Endpoint:
  - **POST** `/shoppinglists/{id}/products`
  - Request-Body: Ein `Product`-Objekt als JSON (Name, Kategorie, Marke, Haltbarkeitsdatum)
  - Antwort: `201 Created` bei Erfolg, `400 Bad Request` bei Fehler (z.B. doppelter Name oder Liste nicht gefunden)
- Die Datenhaltung erfolgt aktuell in einer einfachen In-Memory-Implementierung (`InMemoryShoppingListRepository`). Diese ist für Entwicklung und Tests gedacht und speichert die Einkaufslisten im RAM.
- Es existieren Integrationstests, die prüfen, dass Produkte erfolgreich per HTTP hinzugefügt werden können und Fehlerfälle korrekt behandelt werden.

### Beispiel (Code-Auszug)
```java
// Produkt anlegen
Product apfel = new Product("Apfel", "Obst", "BioMarke", new ExpirationDate(LocalDate.of(2024, 12, 31)));

// Produkt zur Liste hinzufügen
shoppingList.addProduct(apfel); // klappt
shoppingList.addProduct(apfel); // wirft Exception, da Name schon vorhanden
```

### Beispiel-Request (curl)
```bash
curl -X POST http://localhost:8080/shoppinglists/<LISTEN_ID>/products \
  -H 'Content-Type: application/json' \
  -d '{"name":"Banana","category":"Fruit","brand":"Chiquita","expiration":{"date":"2024-12-31"}}'
```

## 7.2 Haltbarkeit prüfen

### Beschreibung
Als Benutzer möchte ich benachrichtigt werden, wenn ein Produkt in der Einkaufsliste ein nahendes Haltbarkeitsdatum (innerhalb von 7 Tagen) hat. Produkte mit abgelaufenem Datum werden explizit markiert. Ziel ist es, rechtzeitig auf ablaufende oder bereits abgelaufene Produkte hingewiesen zu werden.

### Technische Umsetzung
- Die Klasse `ExpirationDate` enthält die Methoden `isExpired()` und `isCloseToExpire()`, wobei "nahend" als weniger als 7 Tage bis zum Ablauf definiert ist.
- Im REST-Controller gibt es den Endpoint:
  - **GET** `/shoppinglists/{id}/expiring-products`
  - Für jedes Produkt der Liste wird ein JSON-Objekt mit allen Produktfeldern und einem zusätzlichen Feld `status` zurückgegeben:
    - `expired` (bereits abgelaufen)
    - `expiring` (läuft in den nächsten 7 Tagen ab)
    - `ok` (noch länger haltbar)
- Es werden keine DTOs verwendet, sondern die Statusberechnung erfolgt direkt im Controller und wird als zusätzliches Feld im Response ergänzt.

### Beispiel-Response
```json
[
  {
    "id": "...",
    "name": "Joghurt",
    "category": "Milchprodukte",
    "brand": "Gut&Günstig",
    "expiration": { "date": "2024-06-01" },
    "status": "expired"
  },
  {
    "id": "...",
    "name": "Milch",
    "category": "Milchprodukte",
    "brand": "Weihenstephan",
    "expiration": { "date": "2024-06-10" },
    "status": "expiring"
  },
  {
    "id": "...",
    "name": "Käse",
    "category": "Milchprodukte",
    "brand": "Alpenhain",
    "expiration": { "date": "2024-06-20" },
    "status": "ok"
  }
]
```

## 7.3 Kostenaufteilung durchführen

### Beschreibung
Als Benutzer in einer Wohngemeinschaft kann ich die Kosten für Einkäufe automatisch aufteilen. Die Aufteilung erfolgt basierend auf dem Anteil der Produkte, die jedem Mitglied zugeordnet sind. Der Gesamtbeitrag sowie der individuelle Anteil werden angezeigt.

### Technische Umsetzung
- Die Klasse `CostAllocationService` enthält die Logik zur Berechnung der Schulden zwischen WG-Mitgliedern.
- Die Methode `calculateOpenDebts()` berechnet für jedes Produkt, wer wem wie viel schuldet, basierend auf wer das Produkt benötigt (`neededBy`) und wer es gekauft hat (`boughtBy`).
- Im REST-Controller gibt es den Endpoint:
  - **GET** `/shoppinglists/{id}/cost-allocation`
  - Der Endpoint liefert eine Map mit den Schulden, wobei der Schuldner (UUID) auf eine weitere Map zeigt, die den Gläubiger (UUID) auf den Betrag (Double) abbildet.

## 7.4 Doppelte Produkte verhindern

### Beschreibung
Es darf nicht möglich sein, ein Produkt, das bereits in der Einkaufsliste existiert, erneut hinzuzufügen. Stattdessen soll dieser Versuch mit einer verständlichen Fehlermeldung abgelehnt werden.

### Technische Umsetzung
- Die Klasse `ShoppingList` enthält die Methode `containsProductWithName(String productName)`, die prüft, ob bereits ein Produkt mit dem angegebenen Namen in der Liste existiert. Der Vergleich erfolgt case-insensitive.
- In der Methode `addProduct(Product product)` wird diese Prüfung durchgeführt und bei einem bereits existierenden Produktnamen eine `IllegalArgumentException` geworfen.
- Die Service-Klasse `AddProductToShoppingListService` führt diese Prüfung ebenfalls durch, um die Konsistenz zu gewährleisten, auch wenn die Prüfung im Aggregat bereits enthalten ist.
- Im REST-Controller wird die Exception abgefangen und in eine Fehlermeldung mit Status `400 Bad Request` umgewandelt.

### Beispiel (Code-Auszug)
```java
// Prüfung in der ShoppingList-Klasse
public void addProduct(Product product) {
    if (containsProductWithName(product.getName())) {
        throw new IllegalArgumentException("Ein Produkt mit dem Namen '" + product.getName() + "' existiert bereits in dieser Einkaufsliste.");
    }
    // ... weiterer Code zum Hinzufügen des Produkts
}
```

### Tests
Es existieren sowohl Unit-Tests als auch Integrationstests, die sicherstellen, dass:
- Die Prüfung auf doppelte Produktnamen korrekt funktioniert
- Die Prüfung case-insensitive ist (z.B. "Milch" und "MILCH" werden als identisch erkannt)
- Beim Versuch, ein doppeltes Produkt hinzuzufügen, eine entsprechende Fehlermeldung zurückgegeben wird
- Verschiedene Produkte problemlos hinzugefügt werden können