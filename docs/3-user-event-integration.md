# 🔗 Intégration User ↔ Event

## 🎯 Objectif

Faire le lien entre les utilisateurs et les événements : savoir **qui participe à quoi**, qui **organise**, etc.

---

## 🧠 Design relationnel

- 1 utilisateur peut **participer à plusieurs événements**
- 1 événement peut avoir **plusieurs participants**
- Lien via une table pivot ou une relation directe (liste d’UUID ?)

---

## 📡 Communication entre microservices

- Pattern actuel : REST API
- Exemple : l’event-service appelle le user-service via `RestTemplate` ou `WebClient` pour valider un participant

---

## 🧪 Test d’intégration simulé

```java
// EventService test : mock UserService
when(userClient.getUserById("uuid-user"))
    .thenReturn(new UserDto("uuid-user", "Pedro", "Boss"));
```

## 💡 Idées d'amélioration

- Resilience4j (circuit breaker, retry)
- Passage à un event-driven model (Kafka, RabbitMQ…)
- Ajout de rôles (organisateur, participant, spectateur...)
