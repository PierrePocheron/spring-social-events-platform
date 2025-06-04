# 👤 Étape 1 — Création du `user-service`

Dans cette première étape, on développe un microservice Spring Boot qui gère les utilisateurs. On y applique les bonnes pratiques d’architecture, le découpage en couches, les DTOs, les mappers, et la communication avec PostgreSQL via JPA.

---

## 🎯 Objectifs

- Créer un service autonome en Java Spring Boot
- Gérer une entité `User` (CRUD minimal)
- Utiliser les patterns **DTO**, **Mapper**, **Builder**
- Préparer une vraie architecture modulaire

---

## 🧱 Stack utilisée

| Technologie       | Utilité                                |
|------------------|-----------------------------------------|
| Spring Boot 3     | Framework principal                     |
| Spring Web        | Création d'API REST                     |
| Spring Data JPA   | ORM pour base de données                |
| PostgreSQL        | Base relationnelle                      |
| Docker Compose    | Conteneurisation de PostgreSQL          |
| Lombok            | Réduction du code boilerplate           |

---

## 📁 Structure du projet

```bash
user-service/
├── controller/       → API REST (UserController)
├── dto/              → Objets de transfert (UserDTO)
├── entity/           → Entités JPA (User)
├── mapper/           → Conversion entité <-> DTO
├── repository/       → Accès base de données
├── service/          → Logique métier (UserService)
├── UserServiceApplication.java → Point d’entrée
└── application.yml   → Configuration
