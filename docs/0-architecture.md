# 🧱 Étape 0 — Architecture Générale

Bienvenue dans la première étape de ce projet pédagogique ! Ici, on prépare le terrain : on définit l’architecture globale et on met en place les bases de notre environnement.

## 🧠 Objectif

Avant de coder, il faut **penser architecture**. Cette étape a pour but :

- De comprendre comment vont interagir nos microservices
- De poser une base technique commune (Docker, Spring Boot, etc.)
- D’être prêt à travailler service par service, dans un environnement modulaire

## 📐 Architecture prévue

Voici un schéma simplifié (textuel) de l'architecture prévue :

[Gateway API] --> [User Service]
--> [Event Service]
--> [Participation Service]
--> [Notification Service]

[Discovery Server (Eureka)]
↳ Tous les services s’y enregistrent

[Config Server]
↳ Tous les services récupèrent leur config ici

[Docker Compose]
↳ Orchestre les services + PostgreSQL

## 🛠 Stack de départ

| Composant         | Techno                  |
| ----------------- | ----------------------- |
| Microservices     | Spring Boot 3           |
| API Gateway       | Spring Cloud Gateway    |
| Service Discovery | Spring Cloud Eureka     |
| Config Server     | Spring Cloud Config     |
| DB                | PostgreSQL (via Docker) |
| Orchestration     | Docker Compose          |

## 🚀 Étapes suivantes

1. Création de l’arborescence des services
2. Init de chaque projet avec Spring Initializr
3. Mise en place de Docker Compose
4. Configuration du config-server et discovery-service

👉 On est parti pour créer les services un par un, tout en gardant une logique claire et réutilisable.
Ce projet est autant un **bac à sable d’apprentissage** qu’une base de code réutilisable. Let’s go ! 💪
