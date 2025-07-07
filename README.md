# 🧩 Social Events Platform — Projet Microservices Java Spring

Bienvenue sur **Social Events Platform**, mon projet d'entraînement complet autour des **microservices avec Spring Boot** ☕️⚙️

L'objectif est de concevoir une architecture réaliste, propre, modulaire et évolutive, à la manière d'un vrai projet d'entreprise.
Chaque service, composant, et intégration est **documenté en détail** pour faciliter l'apprentissage et la montée en compétence.

---

## 🛠️ Tech Stack

- **Java 17** + **Spring Boot 3**
- Spring Cloud (Eureka, Gateway, Config Server)
- PostgreSQL
- Docker & Docker Compose
- JUnit & Mockito
- (À venir) GitHub Actions CI
- (Bonus) Kafka, Keycloak, Angular ou React

---

## 📚 Documentation complète

La documentation est organisée dans le dossier [`/docs`](./docs). Chaque étape est numérotée et couvre un aspect clé du projet :

| Étape | Sujet                                 | Fichier                              |
|-------|---------------------------------------|--------------------------------------|
| 0️⃣    | Architecture générale                 | [`0-architecture.md`](./docs/0-architecture.md) |
| 1️⃣    | Service utilisateur (`user-service`)  | [`1-user-service.md`](./docs/1-user-service.md) |
| 2️⃣    | Service événementiel (`event-service`)| [`2-event-service.md`](./docs/2-event-service.md) |
| 3️⃣    | Intégration des services (Feign etc.)| [`3-user-event-integration.md`](./docs/3-user-event-integration.md) |
| 4️⃣    | Découverte de services (Eureka)       | [`4-discovery.md`](./docs/4-discovery.md) |
| 5️⃣    | API Gateway                           | [`5-gateway.md`](./docs/5-gateway.md) |
| 6️⃣    | Dockerisation complète                | [`6-dockerisation.md`](./docs/6-dockerisation.md) |
| 7️⃣    | Monitoring (Prometheus + Grafana)     | [`7-monitoring.md`](./docs/7-monitoring.md) |

➡️ D'autres étapes viendront enrichir ce projet (tests end-to-end, CI/CD, auth, etc.)

---

## 🧱 Microservices développés

| Service              | Description                                  | Port  |
|----------------------|----------------------------------------------|--------|
| `user-service`        | Gestion des utilisateurs                     | 8081   |
| `event-service`       | Gestion des événements sociaux               | 8082   |
| `gateway-service`     | API Gateway unique                           | 8080   |
| `discovery-service`   | Enregistrement dynamique via Eureka         | 8761   |
| (à venir) `config-server` | Configuration centralisée                  | 8888   |
| (à venir) `participation-service` | Gestion des inscriptions aux événements | TBD    |
| (à venir) `notification-service`  | Simulation d'envoi de mails/SMS       | TBD    |

---

## 🚀 Lancer le projet en local (avec Docker)

> **Prérequis :**
> - [Docker](https://www.docker.com/products/docker-desktop) installé
> - Les ports 8080, 8081, 8082, 8761... doivent être libres

### ✅ Étapes

```bash
# 1. Depuis la racine du projet
docker compose down -v  # (optionnel) Nettoie les volumes existants
docker compose up --build
```


## 🧪 Mode développeur

Pour activer des données de test :

```yaml
# application-docker.yml (dans chaque service)
app:
  init:
    dev-data: true
```

Les services injecteront automatiquement un mini-jeu de données cohérent (utilisateurs + événements liés).

## 📈 Suivi, tests, monitoring
- Les logs de chaque service sont accessibles via docker logs.
- Le monitoring Prometheus / Grafana est documenté dans 7-monitoring.md.
- Les tests unitaires sont présents dans chaque microservice.
- Les tests end-to-end sont prévus dans une prochaine itération.

## 🤝 Objectifs pédagogiques
- 🎯 Apprendre à construire une architecture réaliste et scalable avec Spring Boot
- 🧠 Comprendre les concepts de microservices, gateway, service discovery, communication Feign
- 📦 Mettre en place une infrastructure Dockerisée prête à déployer
- 📚 Rédiger une documentation claire comme dans un vrai projet pro
- 🧪 Explorer les sujets avancés (monitoring, auth, CI/CD…)

## 💡 Pour qui est ce projet ?
Ce projet est destiné :
- Aux développeurs Java débutants ou intermédiaires
- À toute personne souhaitant pratiquer Spring Boot dans un cadre réaliste
- À moi-même 😄 pour monter en compétence et me challenger

## 🧠 À venir
- ✅ Tests end-to-end
- 🧪 Configuration centralisée (Spring Config)
- 🔐 Authentification (Keycloak ou JWT)
- 📦 Déploiement cloud (Kubernetes / Swarm)
- 🌍 Frontend Angular / React
