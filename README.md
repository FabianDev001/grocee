# Grocee

[![Java](https://img.shields.io/badge/Backend-Java_24-blue.svg?style=flat&logo=java)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4.5-green.svg?style=flat&logo=springboot)](https://spring.io/projects/spring-boot)
[![SQLite](https://img.shields.io/badge/Database-SQLite-lightblue.svg?style=flat&logo=sqlite)](https://www.sqlite.org/)
[![Postman](https://img.shields.io/badge/API_Testing-Postman-orange.svg?style=flat&logo=postman)](https://www.postman.com/)
[![License](https://img.shields.io/github/license/FabianDev001/grocee?style=flat)](LICENSE)
[![Build](https://img.shields.io/github/actions/workflow/status/FabianDev001/grocee/build.yml?branch=main)](../../actions)
[![Coverage](https://img.shields.io/badge/coverage-90%25-success)](#)

---

**Grocee** ist ein smarter Einkaufslistenplaner für Haushalte und WGs. Von klassischer Listenverwaltung bis zu Budgettracking und fairer Kostenaufteilung ist alles dabei. Minimaler Overhead, maximaler Nutzen – designed mit Clean Architecture, DDD und modernen Webtechnologien.

---

## 🔍 Überblick

Grocee soll helfen, den Alltag rund ums Einkaufen strukturiert, transparent und teamfähig zu gestalten:

- **Einkaufslisten:** Produkte mit Name, Kategorie, Marke, Haltbarkeitsdatum
- **Lebensmittel-Tracking:** Reminder bei ablaufenden Produkten
- **Budgetkontrolle:** Monatsbudget für Einzelpersonen inkl. Warnung bei Überschreitung
- **Kostenaufteilung:** In WGs wird automatisch fair verteilt, basierend auf zugewiesenen Anteilen
- **Verlauf:** Produkthistorie zur Wiederverwendung
- **Kein Dupes:** Gleiches Produkt ≠ neue Zeile – die Menge wird aktualisiert

---

## 🧩 Tech-Stack

| Layer      | Tool / Stack                  |
|------------|-------------------------------|
| Backend    | [Spring Boot](https://spring.io/) 3.4.5 (Java 24) |
| Frontend   | ~~Next.js~~ (Postman Collection temporär) |
| Architektur | Clean Architecture + Domain Driven Design |
| DB         | SQLite |
| Tests      | JUnit 5 + Mocking (Mockito)     |
| Build      | Maven |
| Dev Tools  | VS Code, Spring Dev Tools    |

---

## 📁 Projektstruktur

```bash
grocee/
├── docs/               # Technische Doku (Markdown, PDF, etc.)
├── backend/            # Java + Spring Boot Backend
│   └── src/
│       └── main/java/de/fab001/grocee/
│           ├── domain/        # DDD: Entities, VOs, Aggregates
│           ├── application/   # UseCases / Services
│           ├── adapters/      # REST Controller, DTOs, Mappings
│           └── plugins/       # DB / External / Framework-Anbindung
├── frontend/           # Postman Collection (temporär anstelle von Next.js)
│   └── postman_collection.json  # API-Endpunkte für Testung
├── .gitignore
├── README.md
```

---

## 🧠 Architektur-Snippet (Clean)

```bash
# clean-layered structure:
├── domain
├── application
├── adapters
└── plugins
```

**Domain** ist frei von Frameworks. **Application** orchestriert die Use Cases. Die **Adapters** sind z. B. Controller und Mapper. Die äußeren **Plugins** kapseln Technik wie Spring, DBs etc. Die Richtung der Abhängigkeiten zeigt _immer_ von außen nach innen.

---

## 🚀 Getting Started

### Backend
```bash
cd backend/grocee
./mvnw spring-boot:run
```

### Frontend (temporär)
Statt einer Next.js-Frontend-Implementierung verwenden wir aktuell eine Postman Collection für API-Tests und Entwicklung. Die Postman Collection enthält alle verfügbaren Endpunkte und kann importiert werden:

1. Postman öffnen
2. Collection importieren aus: `frontend/postman_collection.json`
3. Backend starten (siehe oben)
4. API-Endpunkte über Postman nutzen (Standard: `http://localhost:8080`)

## ✅ Was ist geplant?

- [x] Clean Architecture Setup
- [x] Domain Layer inkl. Value Objects und Entities
- [x] Einfache Produkt-API
- [x] SQLite-Datenbankintegration
- [ ] Auth + Session (JWT oder Clerk/Supabase)
- [x] Kostenaufteilungs-Logik
- [ ] Budget-UI & Visualisierung
- [ ] Next.js Frontend-Implementierung

---

## 💡 Mitwirken

PRs sind willkommen! Fokus auf:

- saubere Architektur (DDD, Clean Architecture)
- gute Naming-Strategien (Ubiquitous Language)
- Testing & Refactoring (mit Coverage)
- keine YAGNI-Logik

#### Für Advance Software Qualität
---

## Lizenz

MIT – [siehe LICENSE](./LICENSE)

---

_„Ship early, refactor often."_

## Hinweis zur KI-Unterstützung

Ein Teil dieses Codes wurde mit Unterstützung von KI-Tools wie ChatGPT (OpenAI) und/oder GitHub Copilot erstellt. Die finale Implementierung, Überprüfung und Anpassung erfolgte durch den Projektautor.

```
