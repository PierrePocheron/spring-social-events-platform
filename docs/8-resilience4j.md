# 🛡️ Résilience avec Resilience4j — Circuit Breaker

> Fichier : `docs/8-resilience4j.md`

---

## 🧠 Objectif

Quand `event-service` appelle `user-service` pour vérifier qu'un organisateur existe, deux risques se posent :

- `user-service` est **down** → on attend un timeout réseau (10-30s) avant d'échouer
- `user-service` est **lent** → chaque requête bloque un thread, les ressources s'épuisent

Le **circuit breaker** résout ça : après un certain nombre d'échecs, il "ouvre le circuit" et retourne directement un résultat de secours (fallback) **sans même tenter l'appel réseau**. Comme un disjoncteur électrique.

---

## ⚡ Les trois états du circuit breaker

```
CLOSED ──(trop d'échecs)──▶ OPEN ──(après 10s)──▶ HALF-OPEN
  ▲                                                     │
  └──────────────(appels de test OK)───────────────────┘
```

| État | Comportement |
|---|---|
| **CLOSED** | Les appels passent normalement |
| **OPEN** | Fallback direct, aucun appel réseau tenté |
| **HALF-OPEN** | 2 appels tests pour voir si le service est revenu |

---

## 📦 Étape 1 — Dépendances dans `event-service/pom.xml`

```xml
<!-- Resilience4j via Spring Cloud Circuit Breaker -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-circuitbreaker-resilience4j</artifactId>
</dependency>

<!-- AOP : nécessaire pour que @CircuitBreaker intercepte les appels -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```

> `spring-cloud-starter-circuitbreaker-resilience4j` est géré par le BOM Spring Cloud déjà présent — pas besoin de version explicite.

> AOP (Aspect-Oriented Programming) permet à Spring d'**entourer** l'appel de méthode sans la modifier. Pense-y comme un middleware Express qui s'exécute avant/après une route.

---

## ⚙️ Étape 2 — Configuration dans `application.yml` (et `application-docker.yml`)

```yaml
resilience4j:
  circuitbreaker:
    instances:
      userClientCB:
        sliding-window-size: 5           # évalue sur les 5 derniers appels
        failure-rate-threshold: 50       # ouvre si 50%+ d'échecs
        wait-duration-in-open-state: 10s # attend 10s avant de retenter
        permitted-number-of-calls-in-half-open-state: 2
  timelimiter:
    instances:
      userClientCB:
        timeout-duration: 2s

management:
  health:
    circuitbreakers:
      enabled: true
  endpoint:
    health:
      show-details: always
```

> ⚠️ Le `timelimiter` Resilience4j ne s'applique qu'aux appels **asynchrones** (`CompletableFuture`). Pour un `RestTemplate` synchrone, le timeout est configuré directement sur le `RestTemplate` (voir étape 3).

---

## 🔧 Étape 3 — Modification de `UserClient.java`

**Avant** : un `try/catch` maison qui masque toutes les erreurs sans tracer ni limiter le temps d'attente.

**Après** : annotation `@CircuitBreaker` + timeout réseau sur le `RestTemplate` + fallback loggué.

```java
@Component
public class UserClient {

    private static final Logger log = LoggerFactory.getLogger(UserClient.class);

    private final RestTemplate restTemplate;
    private final String userServiceBaseUrl;

    public UserClient(@Value("${user-service.base-url:http://localhost:8081}") String userServiceBaseUrl) {
        this.userServiceBaseUrl = userServiceBaseUrl;
        // Timeout réseau de 2s (connect + read)
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(2000);
        factory.setReadTimeout(2000);
        this.restTemplate = new RestTemplate(factory);
    }

    @CircuitBreaker(name = "userClientCB", fallbackMethod = "checkUserExistsFallback")
    public boolean checkUserExists(String userId) {
        String url = String.format("%s/api/users/%s", userServiceBaseUrl, userId);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        return response.getStatusCode().is2xxSuccessful();
    }

    @SuppressWarnings("unused") // appelé par réflexion par Resilience4j
    private boolean checkUserExistsFallback(String userId, Throwable ex) {
        log.warn("Circuit breaker activé pour userId={} : {}", userId, ex.getMessage());
        return false;
    }
}
```

Points clés :
- Le `try/catch` disparaît — Resilience4j capture les exceptions
- Le fallback a **la même signature + un `Throwable` en dernier paramètre** — convention obligatoire
- L'IDE signale `checkUserExistsFallback` comme "jamais utilisé" → faux positif, l'appel est réflexif

---

## 🚨 Étape 4 — Gestion des erreurs dans `event-service`

Sans `GlobalExceptionHandler`, une `IllegalArgumentException` retourne un 500 générique. On crée le handler dans event-service :

**`event-service/.../exception/GlobalExceptionHandler.java`**

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleIllegalArgument(IllegalArgumentException ex) {
        return Map.of("error", ex.getMessage());
    }
}
```

---

## ✅ Critères de réussite

**Test 1 — chemin nominal** (`user-service` up) :
```bash
curl -X POST http://localhost:8080/api/events \
  -H "Content-Type: application/json" \
  -d '{"title":"Test","location":"Paris","date":"2025-09-01","type":"CONFERENCE","organizerId":"<id-valide>"}'
# → 201 Created
```

**Test 2 — circuit breaker** (`user-service` stoppé, 5+ requêtes) :
```bash
docker stop user-service
curl -X POST http://localhost:8080/api/events ...
# → 400 en moins de 2s
# → log : "Circuit breaker activé pour userId=... : ..."
# → appels suivants : réponse quasi-instantanée (circuit OPEN)
```

**Test 3 — état du circuit via Actuator** :
```bash
curl http://localhost:8082/actuator/health | json_pp
# → bloc "circuitBreakers" avec "status": "CLOSED" (ou "OPEN" si user-service est down)
```

---

## 📁 Fichiers modifiés

| Fichier | Modification |
|---|---|
| `event-service/pom.xml` | Ajout Resilience4j + AOP |
| `event-service/src/main/resources/application.yml` | Config circuit breaker + management health |
| `event-service/src/main/resources/application-docker.yml` | Idem |
| `event-service/.../client/UserClient.java` | `@CircuitBreaker` + timeout RestTemplate + fallback |
| `event-service/.../exception/GlobalExceptionHandler.java` | Nouveau — 400 sur IllegalArgumentException |
