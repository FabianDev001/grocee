# GROCEE
> **Hinweis:** Diese Dokumentation beschreibt die technische Implementierung des Einkaufslisten-Planers "Grocee".

Dieses Dokument beinhaltet eine vollständige technische Dokumentation des Einkaufslisten-Planers Grocee, welcher Einzelhaushalte und Wohngemeinschaften bei der Organisation von Einkäufen unterstützt.

## Inhaltsverzeichnis
1. [Domain Driven Design](#1-domain-driven-design)
    * [Analyse der Ubiquitous Language](#11-analyse-der-ubiquitous-language)
    * [Analyse und Begründung der verwendeten Muster](#12-analyse-und-begründung-der-verwendeten-muster)
2. [Clean Architecture](#2-clean-architecture)
    * [Schichtarchitektur](#21-schichtarchitektur)
3. [Programming Principles](#3-programming-principles)
4. [Unit Tests](#4-unit-tests)
5. [Refactoring](#5-refactoring)
6. [Entwurfsmuster](#6-entwurfsmuster)
7. [Use Cases](#7-use-cases)
    * [Neue Lebensmittel zur Einkaufsliste hinzufügen](#71-neue-lebensmittel-zur-einkaufsliste-hinzufügen)
    * [Haltbarkeit prüfen](#72-haltbarkeit-prüfen)
    * [Kostenaufteilung durchführen](#73-kostenaufteilung-durchführen)
    * [Doppelte Produkte verhindern](#74-doppelte-produkte-verhindern)

---

## 1. Domain Driven Design
Ein zentrales Element der Anwendung ist die Umsetzung der Domain Driven Design (DDD) Methodik. Im Folgenden wird zunächst die Ubiquitous Language (UL) des Projekts analysiert und danach die Verwendung der taktischen Muster des DDD erläutert und begründet.

### 1.1 Analyse der Ubiquitous Language
Die Ubiquitous Language stellt ein gemeinsames Vokabular dar, das von allen Projektbeteiligten genutzt wird, um Missverständnisse zu vermeiden und eine klare Kommunikation zu fördern.

#### Begriffe der Ubiquitous Language

Begriff | Definition
--- | ---
**Product** | Ein Artikel, der eingekauft werden soll, inkl. Name, Marke, Kategorie und Haltbarkeit
**Product Template** | Wiederverwendbare Vorlage für ein Produkt mit Name, Marke und Kategorie
**Shopping List** | Sammlung geplanter Produkte (pro Benutzer oder WG)
**Shopping List Item** | Einzelner Eintrag in einer Einkaufsliste, der auf ein ProductTemplate verweist und zusätzliche Informationen wie Haltbarkeitsdatum und Preis enthält
**Expiration Date** | Datum, bis wann ein Produkt verwendet werden sollte
**Cost Allocation** | Funktion zur Aufteilung der Einkaufskosten in einer WG
**User** | Person, die die App nutzt – alleine oder in einer WG
**Household** | Wohngemeinschaft oder Einzelhaushalt, der die App nutzt

#### Beziehungen

![](/docs/BeziehungenPlantUML.png)

* Ein **User** kann Mitglied in mehreren **Households** sein und ein **Household** kann mehrere **User** als Mitglieder haben (many-to-many)
* Ein **User** kann mehrere **ShoppingLists** erstellen (one-to-many)
* Eine **ShoppingList** enthält viele **ShoppingListItems** (one-to-many, Komposition)
* Ein **ShoppingListItem** gehört immer zu genau einer **ShoppingList** (many-to-one)
* Ein **ShoppingListItem** referenziert genau ein **ProductTemplate** (many-to-one)
* Ein **ShoppingListItem** hat ein **ExpirationDate** als Value Object (Komposition)
* Ein **Product** ist ein transientes Objekt, das aus **ProductTemplate** und **ShoppingListItem** Daten erstellt wird
* Die **Cost Allocation** berechnet die Kostenverteilung basierend auf den **ShoppingListItems** und deren Zuordnung zu Benutzern (durch `neededBy` und `boughtBy` Attribute)

Diese Begriffe werden konsequent im Code, in der Dokumentation und in der REST-API verwendet, um eine einheitliche Sprache im gesamten Projekt zu gewährleisten.

### 1.2 Analyse und Begründung der verwendeten Muster

Im Projekt werden die wichtigsten taktischen Muster des Domain Driven Design eingesetzt, um die Komplexität der Domäne sauber abzubilden und den Code wartbar zu halten.

Klasse | Taktisches Muster | Begründung
--- | --- | ---
`ShoppingList` | Entity, Aggregate Root | Die Klasse `ShoppingList` (siehe `src/main/java/de/fab001/grocee/domain/model/ShoppingList.java`) repräsentiert eine Einkaufsliste als zentrale Entität. Sie besitzt eine eindeutige ID (UUID), die sie im System identifizierbar macht, und kann im Laufe der Zeit modifiziert werden (z.B. durch Hinzufügen oder Entfernen von Produkten). Als Aggregate Root kontrolliert sie den Zugriff auf die enthaltenen `ShoppingListItems` und stellt deren Konsistenz sicher, indem sie beispielsweise verhindert, dass doppelte Produktnamen auftreten.
`ExpirationDate` | Value Object | Die Klasse `ExpirationDate` (siehe `src/main/java/de/fab001/grocee/domain/model/ExpirationDate.java`) modelliert das Haltbarkeitsdatum als unveränderliches Wertobjekt. Sie hat keine eigene Identität, sondern wird durch ihren Wert definiert. Zwei `ExpirationDate`-Objekte mit demselben Datum sind identisch. Die Klasse kapselt außerdem die Logik zur Prüfung, ob ein Produkt abgelaufen oder bald ablaufend ist.
`ProductTemplate` | Entity | Die Klasse `ProductTemplate` (siehe `src/main/java/de/fab001/grocee/domain/model/ProductTemplate.java`) repräsentiert eine Produktvorlage mit Name, Marke und Kategorie. Diese Entität besitzt eine eindeutige ID und wird von mehreren `ShoppingListItems` referenziert. Die Verwendung als Entity ermöglicht die zentrale Speicherung und Wiederverwendung von Produktinformationen.
`ShoppingListItem` | Entity | Die Klasse `ShoppingListItem` (siehe `src/main/java/de/fab001/grocee/domain/model/ShoppingListItem.java`) verknüpft ein `ProductTemplate` mit einer `ShoppingList` und enthält zusätzliche Informationen wie Preis und Haltbarkeitsdatum. Als Entity besitzt sie eine eigene Identität und ist immer einem Aggregate (`ShoppingList`) zugeordnet.
`User` | Entity | Die Klasse `User` (siehe `src/main/java/de/fab001/grocee/domain/model/User.java`) repräsentiert einen Benutzer des Systems mit einer eindeutigen ID, Namen und E-Mail-Adresse. Als Entity hat jeder Benutzer eine eigene Identität unabhängig von seinen Attributen.
Repositories | Repository | Die Interfaces `ShoppingListRepository`, `ProductTemplateRepository` und `UserRepository` (siehe `src/main/java/de/fab001/grocee/domain/repository/`) definieren die Zugriffsschnittstellen für die Persistenz der jeweiligen Aggregates. Die Repositories kapseln die Datenzugriffslogik und ermöglichen der Domänenschicht einen abstrakten Zugriff auf die persistierten Objekte.
`AddProductToShoppingListService` | Domain Service | Die Klasse `AddProductToShoppingListService` (siehe `src/main/java/de/fab001/grocee/application/usecase/AddProductToShoppingListService.java`) implementiert den Anwendungsfall des Hinzufügens eines Produkts zu einer Einkaufsliste. Als Domain Service kapselt sie komplexe Geschäftslogik, die die Interaktion zwischen mehreren Aggregates erfordert und koordiniert den gesamten Prozess.

Die Verwendung dieser taktischen Muster gewährleistet eine klare Trennung der Verantwortlichkeiten und eine optimale Abbildung der Geschäftslogik. Durch die Isolation der Domänenlogik von technischen Details wird der Code wartbarer und testbarer.

## 2. Clean Architecture

### 2.1 Schichtarchitektur

Die Architektur des Projekts folgt den Prinzipien der Clean Architecture, um Wartbarkeit, Testbarkeit und Erweiterbarkeit zu gewährleisten. Die Anwendung ist in vier Hauptschichten unterteilt:

#### Domain Layer
- **Beschreibung:** Enthält die zentralen Geschäftsobjekte und Interfaces
- **Pfad:** `src/main/java/de/fab001/grocee/domain/`
- **Bestandteile:**
  - Entities, Value Objects und Aggregates (`model`-Paket)
  - Repository-Interfaces (`repository`-Paket)
  - Domain Services (`service`-Paket)
- Diese Schicht ist vollständig unabhängig von technischen Details oder Frameworks und enthält die wesentliche Geschäftslogik der Anwendung.

#### Application Layer
- **Beschreibung:** Beinhaltet die Anwendungsfälle/Use Cases
- **Pfad:** `src/main/java/de/fab001/grocee/application/`
- **Bestandteile:**
  - Services, die die Geschäftslogik orchestrieren (`usecase`-Paket)
- Diese Schicht nutzt die Domain-Objekte, bleibt aber unabhängig von Infrastrukturdetails. Sie dient als Vermittler zwischen der Domänenschicht und den äußeren Adapterschichten.

#### Adapters Layer
- **Beschreibung:** Enthält die Schnittstellen zur Außenwelt
- **Pfad:** `src/main/java/de/fab001/grocee/adapters/`
- **Bestandteile:**
  - REST-Controller im Hauptverzeichnis (`HouseholdController.java`, `UserController.java`, usw.)
  - DTOs im Unterpaket `dto` (`HouseholdDTO.java`, `UserReference.java`)
- Diese Schicht ist verantwortlich für die Umwandlung von Daten zwischen externen Formaten (wie HTTP-Requests) und den internen Domänenmodellen. Die DTOs (Data Transfer Objects) werden explizit in einem eigenen Unterpaket gehalten, um eine klare Trennung zwischen den externen Datenstrukturen und den Domain-Modellen zu gewährleisten und um zu verdeutlichen, dass sie nur für die Kommunikation mit externen Systemen verwendet werden sollten.

#### Plugins/Infrastructure Layer
- **Beschreibung:** Enthält die technischen Implementierungen
- **Pfad:** `src/main/java/de/fab001/grocee/plugins/` und `src/main/java/de/fab001/grocee/config/`
- **Bestandteile:**
  - Datenbank-Konfiguration
  - Repository-Implementierungen (implizit durch Spring Data JPA)
- Diese Schicht implementiert die Infrastruktur-Details, auf die die inneren Schichten über Interfaces zugreifen.

Die Abhängigkeitsrichtung verläuft ausschließlich von außen nach innen. Das bedeutet, dass äußere Schichten von inneren abhängen können, aber niemals umgekehrt. Die Kommunikation von innen nach außen erfolgt über Interfaces (Dependency Inversion Principle).

**Schichtendiagramm:**

![](/docs/SchichtenDiagrammPlantUML.png)

**Begründung:** Durch diese klare Trennung der Schichten ist der Code leicht testbar, neue Features können ohne große Seiteneffekte ergänzt werden, und technische Details (wie die Persistenz) lassen sich einfach austauschen. 

Im aktuellen Stand sind alle Schichten implementiert, wobei Spring Data JPA die konkreten Repository-Implementierungen generiert.

## 3 Programming Principles

Im Projekt werden verschiedene bewährte Programmierprinzipien angewendet, um die Codequalität, Wartbarkeit und Erweiterbarkeit zu sichern:

Prinzip | Erklärung und Beispiel
--- | ---
**Single Responsibility Principle (SRP, SOLID)** | Jede Klasse hat genau eine klar abgegrenzte Aufgabe.<br><br>**Beispiel:** Die Klasse `ExpirationDate` (siehe `src/main/java/de/fab001/grocee/domain/model/ExpirationDate.java`) ist ausschließlich für die Logik rund um Haltbarkeitsdaten verantwortlich. Sie kapselt das Datum und enthält Methoden zur Prüfung von Ablaufstatus, ohne sich mit anderen Aspekten der Produktverwaltung zu befassen.<br><br>**Beispiel:** `ProductTemplate` (siehe `src/main/java/de/fab001/grocee/domain/model/ProductTemplate.java`) speichert nur grundlegende Produktdaten und kennt keine Geschäftslogik, während `ShoppingListItem` die Beziehung zwischen ProductTemplate und ShoppingList verwaltet.
**Open/Closed Principle (SOLID)** | Das System ist offen für Erweiterungen, aber geschlossen für Modifikationen.<br><br>**Beispiel:** Die Vererbungshierarchie für `ShoppingList` (siehe `@Inheritance` und `@DiscriminatorColumn` in `src/main/java/de/fab001/grocee/domain/model/ShoppingList.java`) ermöglicht es, verschiedene Arten von Einkaufslisten zu implementieren, ohne die Basisklasse zu ändern. Neue Arten von Listen können durch Ableitung erstellt werden.
**Dependency Inversion Principle (SOLID)** | High-Level-Module hängen nicht von Low-Level-Modulen ab, sondern von Abstraktionen.<br><br>**Beispiel:** Die Controller und Services arbeiten nur mit dem Interface `ShoppingListRepository` (siehe `src/main/java/de/fab001/grocee/domain/repository/ShoppingListRepository.java`), nicht mit der konkreten Implementierung. Dadurch wird die Geschäftslogik unabhängig von der Persistenztechnologie, und die konkrete Implementierung kann durch Spring Data JPA oder andere Mechanismen bereitgestellt werden.
**Don't Repeat Yourself (DRY)** | Wiederholte Logik wird vermieden.<br><br>**Beispiel:** Die Prüfung auf doppelte Produktnamen findet nur in `ShoppingList.containsProductWithName()` statt und wird von dort aus konsistent wiederverwendet. Dies vermeidet duplizierte Logik und stellt sicher, dass die Prüfung überall identisch implementiert ist.
**Information Expert (GRASP)** | Die Klasse, die die meisten Informationen hat, übernimmt die Verantwortung.<br><br>**Beispiel:** `ShoppingList` prüft selbst, ob ein Produkt schon existiert (Methode `containsProductWithName`), da sie die Liste der Produkte kennt und am besten beurteilen kann, ob ein Produktname bereits vorhanden ist. Die Verantwortung liegt genau dort, wo auch die relevanten Daten liegen.


## 4. Unit Tests ()

Im Projekt werden Unit Tests nach den ATRIP-Regeln implementiert:

- **Automatic:** Die Tests laufen automatisiert über den Maven Build-Prozess.
- **Thorough:** Es werden verschiedene Fälle getestet (Erfolg, Fehler, Grenzfälle).
- **Repeatable:** Die Tests liefern bei jedem Durchlauf das gleiche Ergebnis.
- **Independent:** Die Tests beeinflussen sich nicht gegenseitig.
- **Professional:** Die Tests sind sauber geschrieben und dokumentiert.

**Tests im Projekt:**
- Es gibt Unit-Tests für die Domain-Logik, wie z.B. das Prüfen der Haltbarkeit von Produkten mittels der `ExpirationDate`-Klasse.
- Es gibt Tests für die Validierungslogik, wie das Verhindern von doppelten Produktnamen innerhalb einer Einkaufsliste.
- Integrationstests prüfen die REST-API mit Spring MockMvc, um sicherzustellen, dass die Controller korrekt funktionieren.

**Beispiel für einen Unit Test:**
Siehe `src/test/java/de/fab001/grocee/ExpiredProductsEndpointTest.java` für einen Test, der die Funktionalität des Endpoints für ablaufende Produkte prüft.

**Verwendung von Mocks:**
Bei Integrationstests werden Mocks für Repositories verwendet, um die Tests unabhängig von der tatsächlichen Datenbankimplementierung zu halten. Dies ermöglicht schnelle und zuverlässige Tests, die nicht von externen Systemen abhängen.

## 5. Refactoring

Im Laufe der Entwicklung wurden verschiedene Code Smells identifiziert und durch gezielte Refactorings behoben:

#### Identifizierte Code Smells:

Code Smell | Beschreibung | Lösung
--- | --- | ---
**Duplicated Code** | Im `ShoppingListController` wurde Code für den Zugriff auf Produkte der `TestShoppingList` an mehreren Stellen dupliziert. Dieser Code verwendete Reflection, um auf private Felder zuzugreifen, und enthielt identischen Fehlerbehandlungs- und Logging-Code. | Es wurde eine Hilfsmethode `getProductsFromList` extrahiert, die den duplizierten Code zusammenfasst und parametrisiert.
**Long Method** | Die Methode `addProductToList` im `ShoppingListController` enthielt zu viele Verantwortlichkeiten: Sie validierte Produktdaten und führte die eigentliche Operation durch. Dies erschwerte das Verständnis und die Wartung. | Die Validierungslogik wurde in eine separate Methode `validateProductFields` extrahiert, wodurch die ursprüngliche Methode kürzer und fokussierter wurde.

#### Durchgeführte Refactorings:

Refactoring | Beschreibung | Ergebnis
--- | --- | ---
**Extract Method** | Die Extraktion der Validierungslogik aus der `addProductToList`-Methode in eine separate Methode `validateProductFields`. | Eine kürzere, leichter verständliche `addProductToList`-Methode, die nur noch die wesentlichen Schritte durchführt. Die Validierungslogik ist nun in einer wiederverwendbaren Methode gekapselt.
**Extract Method für dupliziertem Code** | Die Extraktion und Parametrisierung des duplizierten TestShoppingList-Zugriffscodes in die Methode `getProductsFromList` und die Hilfsmethode `prepareProducts`. | Beseitigung von Codeduplikation, verbesserte Wartbarkeit, und Reduzierung der Fehleranfälligkeit. Änderungen müssen nur noch an einer Stelle vorgenommen werden.

Diese Refactorings haben zu einem klareren, wartbareren Design geführt, bei dem die Verantwortlichkeiten besser verteilt sind. Die Reduzierung der Methodenlänge und die Beseitigung von Duplikationen erleichtern das Verständnis und die zukünftige Weiterentwicklung des Codes.

## 6. Entwurfsmuster

Im Projekt werden das **Repository-Pattern** und die **Factory-Method** als zentrale Entwurfsmuster eingesetzt:

### Repository-Pattern

**Einsatz:** 
Die Interfaces `ShoppingListRepository`, `ProductTemplateRepository` und `UserRepository` definieren die Schnittstellen zur Datenpersistenz, ohne Details der Implementierung preiszugeben.

**Begründung:** 
Das Repository-Pattern trennt die Domänenschicht von der Persistenzschicht. Die Anwendungslogik arbeitet ausschließlich mit dem Interface und ist unabhängig von der konkreten Implementierung (SQLite-Datenbank). Diese klare Trennung ermöglicht eine flexible Entwicklung und vereinfacht das Testen.

**Vorteile:** 
- Die Persistenz kann ohne Änderungen an der Geschäftslogik ausgetauscht werden.
- Testbarkeit wird verbessert, da für Tests Mocks oder In-Memory-Implementierungen verwendet werden können.
- Die Domäne bleibt frei von Infrastruktur-Details.

### Factory-Method für Produkte

**Einsatz:** 
Die Methode `ShoppingListItem.getProduct()` (siehe `src/main/java/de/fab001/grocee/domain/model/ShoppingListItem.java`) erzeugt ein `Product`-Objekt basierend auf den Daten eines `ShoppingListItem`.

**Begründung:** 
Diese Factory-Method kapselt die komplexe Logik zur Erzeugung eines `Product`-Objekts aus einem `ShoppingListItem` und versteckt die Details der Transformation. Es wird eine klare, konsistente Schnittstelle zur Erzeugung von Produkten bereitgestellt.

**Vorteile:**
- Die Erzeugungslogik ist an einem Ort zentralisiert.
- Der Client muss die Komplexität der Objekterzeugung nicht kennen.
- Änderungen an der Erzeugungslogik betreffen nur diese eine Methode.

Diese Entwurfsmuster wurden gewählt, weil sie die Trennung von Zuständigkeiten fördern, die Testbarkeit verbessern und die Anwendung flexibel für zukünftige Änderungen machen.

## 7. Use Cases

### 7.1 Neue Lebensmittel zur Einkaufsliste hinzufügen

#### Beschreibung
Als Benutzer kann ich ein neues Produkt zur Einkaufsliste hinzufügen. Ein Produkt besteht aus Name, Kategorie, bevorzugter Marke und einem Haltbarkeitsdatum. Der Produktname muss eindeutig sein – existiert bereits ein Produkt mit gleichem Namen in der Liste, wird das Hinzufügen verweigert.

#### Technische Umsetzung
- Die Methode `addProduct(Product product)` in `ShoppingList` prüft, ob bereits ein Produkt mit gleichem Namen existiert.
- Der Use Case wird in der Service-Klasse `AddProductToShoppingListService` orchestriert.
- REST-Endpoint: `POST /shoppinglists/{id}/products` nimmt ein `Product`-Objekt entgegen und gibt bei Erfolg den Statuscode 201 zurück.

### 7.2 Haltbarkeit prüfen

#### Beschreibung
Als Benutzer möchte ich benachrichtigt werden, wenn ein Produkt ein nahendes oder abgelaufenes Haltbarkeitsdatum hat. 

#### Technische Umsetzung
- Die Klasse `ExpirationDate` enthält die Logik zur Prüfung von Haltbarkeitsdaten mit den Methoden `isExpired()` und `isCloseToExpire()`.
- REST-Endpoint: `GET /shoppinglists/{id}/expiring-products` gibt eine Liste aller Produkte mit ihrem Status zurück (abgelaufen, bald ablaufend, in Ordnung).

### 7.3 Kostenaufteilung durchführen

#### Beschreibung
Als Benutzer in einer WG kann ich die Kosten für Einkäufe automatisch aufteilen. Die Aufteilung erfolgt basierend auf:

1. **Individuelle Zuordnung**: Wenn Produkte bestimmten Mitgliedern zugeordnet sind, tragen diese die Kosten für ihre Produkte.
2. **Einkaufendes Mitglied**: Wer den Einkauf durchgeführt hat, kann sehen, wie viel die anderen Mitglieder ihm/ihr schulden.
3. **Gesamtübersicht**: Für jedes Mitglied wird der individuelle Anteil sowie der Gesamtbeitrag angezeigt.

#### Technische Umsetzung
- Das `Household`-Entity repräsentiert eine Wohngemeinschaft und enthält die Methode `calculateMemberShares()` zur Berechnung der Kostenanteile.
- `HouseholdService` enthält zwei Hauptmethoden:
  - `calculateCostAllocation()`: Verteilt die Gesamtkosten auf die Haushaltsmitglieder basierend auf den ihnen zugeordneten Produkten.
  - `calculateDebts()`: Berechnet, wer wem wie viel Geld schuldet, basierend auf den Attributen `neededBy` und `boughtBy` der ShoppingListItems.
- REST-Endpoints:
  - `GET /households/{id}/cost-allocation?shoppingListId={listId}` liefert eine Übersicht der Kostenanteile.
  - `GET /households/{id}/debts?shoppingListId={listId}` liefert eine Übersicht der Schulden zwischen Mitgliedern.

### 7.4 Doppelte Produkte verhindern

#### Beschreibung
Es darf nicht möglich sein, ein Produkt, das bereits in der Einkaufsliste existiert, erneut hinzuzufügen.

#### Technische Umsetzung
- Die Methode `containsProductWithName(String productName)` in `ShoppingList` prüft case-insensitive, ob ein Produkt mit diesem Namen bereits existiert.
- Bei einem doppelten Produktnamen wird eine Exception geworfen und der REST-Controller gibt einen Fehler 400 zurück.
