# 🐳 6. Dockerisation de l'application - Spring Social Events Platform

## 🎯 Objectif

Conteneuriser toute l'application en microservices avec Docker et Docker Compose pour :

* Faciliter les tests et le déploiement local
* Préparer la montée en production via Swarm ou Kubernetes

---

## 📁 Arborescence du projet

```bash
spring-social-events-platform/
├── docker-compose.yml
├── user-service/
│   ├── Dockerfile
│   ├── src/... (Spring Boot + Postgres)
│      ├── application.yml
│      └── application-docker.yml
├── event-service/
│   ├── Dockerfile
│   ├── src/... (Spring Boot + Postgres)
│      ├── application.yml
│      └── application-docker.yml
├── gateway-service/
│   ├── Dockerfile
│   ├── src/... (Spring Cloud Gateway)
│      ├── application.yml
│      └── application-docker.yml
├── discovery-service/
│   ├── Dockerfile
│   ├── src/... (Spring Eureka Server)
│      ├── application.yml
│      └── application-docker.yml
```

---

## 🧾 Différence entre `application.yml` et `application-docker.yml`

* `application.yml` : utilisé **localement** dans un environnement de développement classique (lancement via IntelliJ, port local, pas de Docker).
* `application-docker.yml` : activé via `SPRING_PROFILES_ACTIVE=docker` dans Docker. Il contient les **URL internes Docker**, les noms de services comme `postgres` ou `discovery-service`, et adapte les paramètres aux **containers Docker**.

> ✅ Cela permet de séparer proprement la configuration locale (développement) de la configuration Docker (conteneurisée).

---

## 🧱 Dockerfile Multi-Stage (modèle utilisé par chaque microservice)

```dockerfile
# Étape 1 : Build de l'application Java
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Étape 2 : Image légère avec JRE seulement
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Chaque service a un `Dockerfile` similaire adapté à son port (8081, 8082, 8080, etc.).

---

## 📜 application-docker.yml

Chaque microservice dispose d'un `application-docker.yml` avec :

* Connexion à sa base Postgres (host = nom du service)
* Utilisation de `SPRING_PROFILES_ACTIVE=docker` via les variables d'environnement Docker
* Enregistrement sur Eureka (si nécessaire)

Exemple pour `user-service` :

```yaml
spring:
  datasource:
    url: jdbc:postgresql://postgres:5432/userdb
  jpa:
    hibernate:
      ddl-auto: update
  server:
    port: 8081
```

---

## 🔀 docker-compose.yml

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
      POSTGRES_DB: userdb
    ports:
      - "5432:5432"
    volumes:
      - pgdata:/var/lib/postgresql/data

  postgres-event:
    image: postgres:15
    environment:
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
      POSTGRES_DB: eventdb
    ports:
      - "5433:5432"
    volumes:
      - eventdata:/var/lib/postgresql/data

  discovery-service:
    build:
      context: ./discovery-service
    container_name: discovery-service
    ports:
      - "8761:8761"
    networks:
      - user-network

  user-service:
    build:
      context: ./user-service
    ports:
      - "8081:8081"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
    depends_on:
      - postgres
      - discovery-service
    networks:
      - user-network

  event-service:
    build:
      context: ./event-service
    ports:
      - "8082:8082"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
    depends_on:
      - postgres-event
      - discovery-service
    networks:
      - user-network

  gateway-service:
    build:
      context: ./gateway-service
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
    depends_on:
      - discovery-service
      - user-service
      - event-service
    networks:
      - user-network

volumes:
  pgdata:
  eventdata:

networks:
  user-network:
    driver: bridge
```

---

## 🔗 Connexion entre les containers

Voici comment tous les services Docker communiquent entre eux :

### 🔌 Réseau partagé

Tous les services sont connectés au **même réseau Docker** appelé `user-network`. Cela permet :

* D'utiliser le **nom du service comme adresse réseau** (ex : `discovery-service`, `postgres`)
* D’éviter l’usage de `localhost` (car chaque container a sa propre stack réseau)

### 🌐 Communication inter-services

* **Discovery Service (Eureka)** joue le rôle d’annuaire : chaque microservice s’y enregistre au démarrage.

* Les services déclarent :

  ```yaml
  eureka:
    client:
      service-url:
        defaultZone: http://discovery-service:8761/eureka/
  ```

* **La Gateway** interroge Eureka pour router dynamiquement les requêtes avec :

  ```yaml
  uri: lb://user-service
  ```

  `lb://` signifie "load balancer via Eureka" — même si un seul container est enregistré.

### 🧪 Exemple : appel à /api/users

1. L'utilisateur appelle : `http://localhost:8080/api/users`
2. Gateway intercepte la requête
3. Elle utilise Eureka pour savoir que `user-service` est disponible à `http://user-service:8081`
4. La requête est redirigée de façon transparente

> 📦 Grâce à cette architecture, tous les services sont autonomes, découvrables, et découplés !

---

## 🚀 Lancement de l’application

```bash
# 1. Build des images
$ docker-compose build

# 2. Lancement des services
$ docker-compose up -d

# 3. Accès aux services
- Eureka : http://localhost:8761
- Gateway : http://localhost:8080
- Users : http://localhost:8080/api/users
- Events : http://localhost:8080/api/events
```

---

✅ **Résultat :** Une architecture microservices isolée, répliquable, testable et prête pour un déploiement cloud.
➡️ **Prochaine étape :** monitoring avec Prometheus + Grafana 🎯
