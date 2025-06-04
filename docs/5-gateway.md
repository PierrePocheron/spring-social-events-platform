# 🚪 API Gateway avec Spring Cloud Gateway

## 🎯 Objectif

Centraliser toutes les requêtes entrantes (front-end ou API) vers les bons microservices backend.

---

## ⚙️ Configuration YAML

```yaml
server:
  port: 8080

spring:
  application:
    name: gateway-service

  cloud:
    gateway:
      routes:
        - id: user-service
          uri: http://localhost:8081
          predicates:
            - Path=/api/users/**
        - id: event-service
          uri: http://localhost:8082
          predicates:
            - Path=/api/events/**
```

## 💡 Remarques

- Chaque route redirige vers un microservice via uri
- Les prédicats Path déterminent l’URL d’entrée
- On peut ajouter des filters (authentification, logs...)

## 🔧 Problèmes rencontrés

- DNS macOS (Netty) → ajouter dépendance netty-resolver-dns-native-macos
- Ports non alignés entre services
- UNKNOWN dans Eureka

## ✅ Test de routing

```bash
curl http://localhost:8080/api/users
curl http://localhost:8080/api/events
```

## 🔐 Sécurité à venir

- JWT Token à intercepter sur la Gateway
- Filtre de vérification de token avant redirection

## 🧪 Tests E2E possibles
- Vérifier le routing par URL
- Mock d’un service inaccessible
- Tests de fallback (resilience)