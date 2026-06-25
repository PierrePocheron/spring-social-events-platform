# ⚡ Messaging asynchrone avec Apache Kafka (KRaft)

> Fichier : `docs/9-kafka.md`

---

## 🧠 Objectif

Jusqu'ici, event-service appelle user-service de façon **synchrone** (REST). Si on veut notifier un utilisateur quand son événement est créé, on ne va pas rajouter un appel REST de plus — on découple ça avec Kafka.

**Pattern event-driven** :
- event-service publie un événement `EventCreated` dans Kafka
- notification-service écoute le topic et réagit **de façon indépendante**
- Les deux services **ne se connaissent pas** — le couplage est rompu

```
event-service ──publie──▶ [ topic: events.created ] ──▶ notification-service
   (producer)                    (Kafka KRaft)                  (consumer)
```

---

## 🔑 Concepts clés

| Concept | Définition |
|---|---|
| **Topic** | Canal nommé dans Kafka — comme un channel Slack. Ici : `events.created` |
| **Producer** | Service qui publie des messages (event-service) |
| **Consumer** | Service qui lit les messages (notification-service) |
| **Consumer Group** | Groupe d'instances d'un même consumer — chaque message est traité **une seule fois** dans le groupe |
| **KRaft** | Mode sans ZooKeeper (Kafka 3.3+) — Kafka gère lui-même son métadonnées |
| **Partition** | Subdivision d'un topic — permet le parallélisme. La clé du message détermine la partition |
| **Offset** | Position d'un message dans une partition — Kafka retient où chaque consumer en est |

---

## 🐳 Étape 1 — Kafka KRaft dans `docker-compose.yml`

```yaml
kafka:
  image: bitnami/kafka:3.7
  container_name: kafka
  ports:
    - "9092:9092"
  environment:
    - KAFKA_CFG_NODE_ID=1
    - KAFKA_CFG_PROCESS_ROLES=broker,controller
    - KAFKA_CFG_CONTROLLER_QUORUM_VOTERS=1@kafka:9093
    - KAFKA_CFG_LISTENERS=PLAINTEXT://:9092,CONTROLLER://:9093
    - KAFKA_CFG_ADVERTISED_LISTENERS=PLAINTEXT://kafka:9092
    - KAFKA_CFG_LISTENER_SECURITY_PROTOCOL_MAP=CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT
    - KAFKA_CFG_CONTROLLER_LISTENER_NAMES=CONTROLLER
    - KAFKA_CFG_INTER_BROKER_LISTENER_NAME=PLAINTEXT
  networks:
    - user-network
```

> `ADVERTISED_LISTENERS=PLAINTEXT://kafka:9092` : adresse que les autres containers utilisent pour se connecter. `kafka` est le nom DNS interne Docker.

---

## 📦 Étape 2 — Dépendance dans `event-service/pom.xml`

```xml
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>
```

---

## 📨 Étape 3 — Classe d'événement (event-service)

`event-service/.../kafka/EventCreatedEvent.java`

```java
package fr.pedro.event_service.kafka;

import java.time.LocalDate;

public record EventCreatedEvent(
        Long eventId,
        String title,
        String organizerId,
        LocalDate date
) {}
```

> Un `record` Java = classe immuable avec constructeur, getters, equals/hashCode auto-générés. Idéal pour un message Kafka.

---

## 📤 Étape 4 — Le producer (event-service)

`event-service/.../kafka/EventProducer.java`

```java
@Component
@RequiredArgsConstructor
public class EventProducer {

    private static final Logger log = LoggerFactory.getLogger(EventProducer.class);
    static final String TOPIC = "events.created";

    private final KafkaTemplate<String, EventCreatedEvent> kafkaTemplate;

    public void publishEventCreated(EventCreatedEvent event) {
        kafkaTemplate.send(TOPIC, event.eventId().toString(), event);
        log.info("Événement publié sur {} : eventId={}", TOPIC, event.eventId());
    }
}
```

> La clé (`eventId`) garantit que tous les messages du même event vont dans la même partition → ordering préservé par entité.

---

## 🔧 Étape 5 — Publication dans `EventService.java`

Après le `repo.save()` :

```java
Event saved = repo.save(EventMapper.toEntity(dto));
eventProducer.publishEventCreated(new EventCreatedEvent(
        saved.getId(),
        saved.getTitle(),
        saved.getOrganizerId(),
        saved.getDate()
));
return EventMapper.toDTO(saved);
```

---

## ⚙️ Étape 6 — Config Kafka producer (event-service)

`application.yml` (dans le bloc `spring:`) :
```yaml
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
```

`application-docker.yml` :
```yaml
spring:
  kafka:
    bootstrap-servers: kafka:9092
```

---

## 🆕 Étape 7 — Créer notification-service

Via [start.spring.io](https://start.spring.io) :
- Group `fr.pedro` / Artifact `notification-service`
- Java 17, Spring Boot 3.5.x
- Dépendances : **Spring Web**, **Spring for Apache Kafka**, **Actuator**, **Lombok**

---

## 📨 Étape 8 — Classe d'événement (notification-service)

`notification-service/.../kafka/EventCreatedEvent.java` — même structure, paquet différent :

```java
package fr.pedro.notification_service.kafka;

import java.time.LocalDate;

public record EventCreatedEvent(
        Long eventId,
        String title,
        String organizerId,
        LocalDate date
) {}
```

> En production : lib partagée ou Schema Registry Avro. Ici on duplique pour la simplicité — c'est un choix pédagogique assumé.

---

## 📥 Étape 9 — Le consumer (notification-service)

`notification-service/.../kafka/NotificationConsumer.java`

```java
@Component
public class NotificationConsumer {

    private static final Logger log = LoggerFactory.getLogger(NotificationConsumer.class);

    @KafkaListener(topics = "events.created", groupId = "notification-group")
    public void onEventCreated(EventCreatedEvent event) {
        log.info("Nouvelle notification : événement '{}' créé par l'organisateur {}",
                event.title(), event.organizerId());
    }
}
```

> `groupId = "notification-group"` : plusieurs instances du service → chaque message traité **une seule fois** dans le groupe. C'est l'idempotence au niveau infra.

---

## ⚙️ Étape 10 — Config consumer (notification-service)

`application.yml` :
```yaml
server:
  port: 8083

spring:
  application:
    name: notification-service
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: notification-group
      auto-offset-reset: earliest
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        spring.json.trusted.packages: "fr.pedro.notification_service.kafka"
        spring.json.value.default.type: fr.pedro.notification_service.kafka.EventCreatedEvent

management:
  endpoints:
    web:
      exposure:
        include: health
```

`application-docker.yml` :
```yaml
spring:
  kafka:
    bootstrap-servers: kafka:9092
```

> `auto-offset-reset: earliest` : au premier démarrage, lecture depuis le début du topic. Un redémarrage rejoue les messages — idéal pour le dev, à ajuster (`latest`) en prod si tu ne veux pas rejouer l'historique.

---

## 🐳 Étape 11 — Dockerfile notification-service

Identique aux autres services (`notification-service/Dockerfile`) :

```dockerfile
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

## 🐳 Étape 12 — notification-service dans `docker-compose.yml`

```yaml
notification-service:
  build:
    context: ./notification-service
  container_name: notification-service
  ports:
    - "8083:8083"
  environment:
    - SPRING_PROFILES_ACTIVE=docker
  depends_on:
    - kafka
  networks:
    - user-network
```

Et dans `monitoring/prometheus.yml` :
```yaml
  - job_name: 'notification-service'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['notification-service:8083']
```

---

## ✅ Critères de réussite

**Test 1 — tout démarre** :
```bash
docker compose up --build
docker compose ps   # tous les services en "running"
```

**Test 2 — flux complet** :
```bash
curl -X POST http://localhost:8080/api/events \
  -H "Content-Type: application/json" \
  -d '{"title":"Soirée JS","location":"Paris","date":"2025-09-01","type":"CONFERENCE","organizerId":"<id-valide>"}'
```

Dans `docker logs notification-service` :
```
INFO NotificationConsumer : Nouvelle notification : événement 'Soirée JS' créé par l'organisateur <id>
```

**Test 3 — idempotence au redémarrage** :
```bash
docker compose restart notification-service
docker logs notification-service
# → rejoue les messages existants (comportement voulu avec earliest)
```

---

## ⚠️ Idempotence côté consommateur

Kafka garantit la livraison **au moins une fois** (at-least-once). Un message peut donc être consommé deux fois en cas de crash du consumer juste après traitement.

Pour une vraie idempotence :
- Stocker l'`eventId` traité en base
- Vérifier au début du consumer si le message a déjà été traité
- Ne rien faire si c'est le cas

Dans notre cas, logguer est idempotent par nature — logguer deux fois n'a pas d'effet négatif.

---

## 📁 Fichiers créés / modifiés

| Fichier | Modification |
|---|---|
| `docker-compose.yml` | Ajout du service `kafka` (KRaft) |
| `event-service/pom.xml` | Ajout `spring-kafka` |
| `event-service/.../kafka/EventCreatedEvent.java` | Nouveau — record de l'événement |
| `event-service/.../kafka/EventProducer.java` | Nouveau — publication sur le topic |
| `event-service/.../service/EventService.java` | Publication après chaque save |
| `event-service/.../resources/application.yml` | Config producer Kafka |
| `event-service/.../resources/application-docker.yml` | bootstrap-servers Docker |
| `notification-service/` | Nouveau service complet |
| `notification-service/.../kafka/EventCreatedEvent.java` | Record dupliqué |
| `notification-service/.../kafka/NotificationConsumer.java` | Nouveau — consumer `@KafkaListener` |
| `notification-service/.../resources/application.yml` | Config consumer Kafka |
| `notification-service/.../resources/application-docker.yml` | bootstrap-servers Docker |
| `notification-service/Dockerfile` | Nouveau |
| `monitoring/prometheus.yml` | Ajout scrape notification-service |
