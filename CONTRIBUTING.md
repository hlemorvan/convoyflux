# Contributing to convoyflux

## Prérequis

- Java 25 (Temurin)
- Maven 3.9+
- Node.js 22+
- Docker + Docker Compose

## Lancer la stack en local

```bash
docker compose up --build
```

Frontend : http://localhost:3000  
Backend API : http://localhost:8080

## Tests backend

```bash
cd backend
mvn verify          # TU + intégration (Testcontainers) + Cucumber + JaCoCo
```

## Tests frontend

```bash
cd frontend
npm ci
npm test            # Vitest
npm run test:e2e    # Playwright (nécessite la stack Docker)
```

## Conventions

- **Aucun appel bloquant** dans la chaîne réactive (`block()`, JDBC, JPA classique sont interdits).
- En-tête SPDX obligatoire sur tout fichier source : `// SPDX-License-Identifier: GPL-3.0-or-later`
- Commits : préfixe conventionnel (`feat:`, `fix:`, `test:`, `docs:`, `chore:`).
- Une PR = un sujet, tests inclus.

## Licence

Ce projet est distribué sous **GNU General Public License v3.0** ou ultérieure.
Toute contribution est soumise aux mêmes termes — voir le fichier [LICENSE](LICENSE) pour le texte complet.
