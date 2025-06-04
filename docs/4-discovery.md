# 🔍 Discovery Service avec Eureka

## 🎯 Objectif

Centraliser l’enregistrement des microservices pour les découvrir dynamiquement, sans avoir besoin de hardcoder les URLs.

---

## ⚙️ Configuration Eureka Server

```yaml
server:
  port: 8761

spring:
  application:
    name: discovery-server

eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
```

## 🛰️ Enregistrement des services clients

Dans application.yml des services :

```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka
```

Et dans les classes principales :

```java
@EnableDiscoveryClient
```

## 📊 Interface d’administration

Accessible sur :
🔗 http://localhost:8761
Permet de voir tous les services enregistrés (gateway, users, events…)

## ❌ Problèmes fréquents

- UNKNOWN status : le service n’a pas fini son bootstrap
- Mauvais application.name
- Port non accessible

## ✅ Bonnes pratiques

- Définir un instance-id unique
- Monitorer les heartbeats
- Utiliser des health endpoints (actuator) pour la surveillance