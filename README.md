# Grocee

[![Java](https://img.shields.io/badge/Backend-Java-blue.svg?style=flat&logo=java)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-2.7-green.svg?style=flat&logo=springboot)](https://spring.io/projects/spring-boot)
[![Next.js](https://img.shields.io/badge/Frontend-Next.js-black.svg?style=flat&logo=next.js)](https://nextjs.org/)
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
| Frontend   | [Next.js](https://nextjs.org/) + React |
| Backend    | [Spring Boot](https://spring.io/) (Java) |
| Architektur | Clean Architecture + Domain Driven Design |
| DB         | TBD (SQLite) |
| Tests      | JUnit + Mocking (Mockito)     |
| Build      | Maven / npm                   |
| Dev Tools  | VS Code, Docker (optional)    |

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
├── frontend/           # Next.js App
│   └── pages/          # SSR/CSR Views
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

**Domain** ist frei von Frameworks. **Application** orchestriert die Use Cases. Die **Adapters** sind z. B. Controller und Mapper. Die äußeren **Plugins** kapseln Technik wie Spring, DBs etc. Die Richtung der Abhängigkeiten zeigt _immer_ von außen nach innen.

---

## 🚀 Getting Started

### Backend
```bash
cd backend
./mvnw spring-boot:run
```

### Frontend
```bash
cd frontend
npm install
npm run dev
```

Standard-Port ist `http://localhost:3000`


## ✅ Was ist geplant?

- [x] Clean Architecture Setup
- [x] Domain Layer inkl. Value Objects und Entities
- [x] Einfache Produkt-API
- [ ] Auth + Session (JWT oder Clerk/Supabase)
- [ ] Kostenaufteilungs-Logik
- [ ] Budget-UI & Visualisierung

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

_„Ship early, refactor often.“_

## Hinweis zur KI-Unterstützung

Ein Teil dieses Codes wurde mit Unterstützung von KI-Tools wie ChatGPT (OpenAI) und/oder GitHub Copilot erstellt. Die finale Implementierung, Überprüfung und Anpassung erfolgte durch den Projektautor.

```
