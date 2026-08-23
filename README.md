# Spring Social Events Platform — Microservices Spring Boot

Projet d'apprentissage complet autour des **microservices avec Spring Boot / Spring Cloud**, construit étape par étape avec une architecture de niveau production.

---

## Stack technique

| Catégorie | Technologies |
|---|---|
| **Langage / Runtime** | Java 21, Spring Boot 4.1 |
| **Microservices** | Spring Cloud Gateway, Spring Cloud Netflix Eureka, Spring Cloud Circuit Breaker |
| **Résilience** | Resilience4j (Circuit Breaker, Timeout, Fallback) |
| **Messaging** | Apache Kafka (mode KRaft, sans ZooKeeper) |
| **Data** | PostgreSQL, Spring Data JPA, Hibernate |
| **Observabilité** | Prometheus, Grafana, Micrometer, Spring Boot Actuator |
| **Conteneurs** | Docker, Docker Compose (multi-stage Dockerfile) |
| **CI/CD & DevSecOps** | GitHub Actions, SpotBugs + FindSecBugs, OWASP ZAP, Trivy, OWASP Dependency-Check, CycloneDX SBOM |

---

## Microservices

| Service | Rôle | Port |
|---|---|---|
| `gateway-service` | Point d'entrée unique — routage, load balancing | 8080 |
| `discovery-service` | Registre dynamique des services (Eureka) | 8761 |
| `user-service` | Gestion des utilisateurs | 8081 |
| `event-service` | Gestion des événements, publication Kafka | 8082 |
| `notification-service` | Consommateur Kafka — notifications | 8083 |

---

## Lancer le projet

```bash
# Démarrage complet (build + run)
docker compose up --build

# Nettoyer les volumes (Kafka, Postgres) avant de repartir propre
docker compose down -v && docker compose up --build
```

**Endpoints principaux (via gateway) :**
- `GET  http://localhost:8080/api/users`
- `POST http://localhost:8080/api/users`
- `GET  http://localhost:8080/api/events`
- `POST http://localhost:8080/api/events`
- `GET  http://localhost:8082/actuator/health`

**Monitoring :**
- Prometheus : http://localhost:9090
- Grafana : http://localhost:3000 (`admin / admin`)
- Eureka dashboard : http://localhost:8761

---

## Architecture

```
                    Client HTTP
                         │
              ┌──────────▼──────────┐
              │  API Gateway :8080   │   Spring Cloud Gateway
              │  routing, LB         │
              └──────┬──────────┬───┘
                     │          │
            /api/users/**   /api/events/**
                     │          │
          ┌──────────▼┐   ┌─────▼──────────┐
          │user-service│   │ event-service   │
          │  :8081     │◄──│  :8082          │  RestTemplate + Circuit Breaker
          └──────┬─────┘   └──────┬──────────┘
                 │                │  publishes
          [postgres              [postgres      [Kafka KRaft :9092]
           :5432]                 :5433]              │
                                                      │ consumes
                                            ┌─────────▼──────────┐
                                            │ notification-service │
                                            │  :8083               │
                                            └──────────────────────┘

  Transverse : Eureka (découverte) · Prometheus + Grafana (métriques) · Actuator (health)
```

---

## Documentation

| # | Sujet | Fichier |
|---|---|---|
| 0 | Architecture générale | [`docs/0-architecture.md`](./docs/0-architecture.md) |
| 1 | user-service | [`docs/1-user-service.md`](./docs/1-user-service.md) |
| 2 | event-service | [`docs/2-event-service.md`](./docs/2-event-service.md) |
| 3 | Intégration inter-services | [`docs/3-user-event-integration.md`](./docs/3-user-event-integration.md) |
| 4 | Service discovery (Eureka) | [`docs/4-discovery.md`](./docs/4-discovery.md) |
| 5 | API Gateway | [`docs/5-gateway.md`](./docs/5-gateway.md) |
| 6 | Dockerisation | [`docs/6-dockerisation.md`](./docs/6-dockerisation.md) |
| 7 | Monitoring (Prometheus + Grafana) | [`docs/7-monitoring.md`](./docs/7-monitoring.md) |
| 8 | Résilience (Resilience4j) | [`docs/8-resilience4j.md`](./docs/8-resilience4j.md) |
| 9 | Messaging asynchrone (Kafka) | [`docs/9-kafka.md`](./docs/9-kafka.md) |

---

## CI/CD & DevSecOps (GitHub Actions)

| Workflow | Outil | Type |
|---|---|---|
| `maven-ci.yml` | Maven + SpotBugs + FindSecBugs | Build + SAST |
| `container-scan.yml` | Trivy | Scan image Docker |
| `oast-zap.yml` | OWASP ZAP | DAST |
| `codeql-analysis.yml` | CodeQL | Analyse de code |
| `depandabot.yml` | Dependabot | Mises à jour de dépendances |

---

## À venir

- Kubernetes — Deployment, Service, ConfigMap, Secret, Ingress, probes Actuator, HPA
- Distributed tracing — OpenTelemetry + Tempo (3e pilier de l'observabilité)
- Authentification — JWT ou Keycloak
- Config Server — Spring Cloud Config
