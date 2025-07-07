# 📊 Monitoring de l'application avec Prometheus & Grafana

> Fichier : `docs/7-monitoring.md`

---

## 🧠 Objectif du monitoring

Le **monitoring** permet de :
- Visualiser la **santé en temps réel** des services (UP/DOWN).
- Obtenir des **statistiques de performance** : temps de réponse, mémoire, CPU, etc.
- Aider au **debug**, au **scaling**, ou à l’**optimisation** de l’architecture microservices.
- Suivre l’évolution d’un système **en production** dans un dashboard clair.

Dans notre application, on utilise un couple d’outils open-source réputé :

| Outil        | Rôle                                                                 |
|--------------|----------------------------------------------------------------------|
| **Prometheus** | Collecter les métriques exposées par chaque microservice.           |
| **Grafana**     | Afficher ces métriques dans des **dashboards personnalisables**.   |

---

## ⚙️ Fonctionnement général

### 🔄 Comment ça s’articule ?

```
user-service 🔁
event-service  🔁 → /actuator/prometheus ← Prometheus ← Grafana
gateway-service 🔁
```

1. Chaque service Spring expose ses **métriques via `/actuator/prometheus`**.
2. Prometheus **interroge régulièrement cette URL** pour collecter les données.
3. Grafana **interroge Prometheus** pour afficher les infos dans un **dashboard dynamique**.

---

## 📦 Étape 1 – Ajout des dépendances

Dans chaque service que l’on veut monitorer (`user-service`, `event-service`, `gateway-service`), on ajoute dans le `pom.xml` :

```xml
<dependency>
  <groupId>io.micrometer</groupId>
  <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>

<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

---

## ⚙️ Étape 2 – Configuration dans `application-docker.yml`

On configure l’exposition des endpoints et l’export des métriques :

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health, info, prometheus
  metrics:
    export:
      prometheus:
        enabled: true
```

⚠️ Bien mettre ça dans le fichier **`application-docker.yml`** (profil `docker`) pour éviter d’activer Prometheus en local.

---

## 🗂️ Étape 3 – Création du fichier Prometheus

Créer le fichier `monitoring/prometheus.yml` :

```yaml
global:
  scrape_interval: 5s

scrape_configs:
  - job_name: 'user-service'
    static_configs:
      - targets: ['user-service:8081']

  - job_name: 'event-service'
    static_configs:
      - targets: ['event-service:8082']

  - job_name: 'gateway-service'
    static_configs:
      - targets: ['gateway-service:8080']
```

🧠 Cela indique à Prometheus **quelle URL interroger** pour chaque microservice.

> ❌ `discovery-service` est volontairement exclu car il ne supporte pas bien l'export de métriques Prometheus.

---

## 🐳 Étape 4 – Ajout dans `docker-compose.yml`

Ajout des services `prometheus` et `grafana` dans le fichier `docker-compose.yml` :

```yaml
prometheus:
  image: prom/prometheus
  container_name: prometheus
  volumes:
    - ./monitoring/prometheus.yml:/etc/prometheus/prometheus.yml
  ports:
    - "9090:9090"
  networks:
    - user-network

grafana:
  image: grafana/grafana
  container_name: grafana
  ports:
    - "3000:3000"
  networks:
    - user-network
```

---

## 📊 Étape 5 – Dashboard Grafana

1. Accès : [http://localhost:3000](http://localhost:3000)
2. Identifiants par défaut : `admin / admin`
3. Ajouter Prometheus comme **data source** :
   - URL : `http://prometheus:9090`
4. Importer un dashboard :
   - Exemple : ID `4701` (Spring Boot 2 / Micrometer template)
   - Permet d’afficher les métriques automatiquement (mémoire, CPU, latence, etc.)

---

## 💡 À propos de `/actuator/prometheus`

Grâce à l’Actuator de Spring Boot, chaque service expose ses statistiques au format Prometheus sur l’URL :

```
http://<nom-service>:<port>/actuator/prometheus
```

Exemples :
- `http://user-service:8081/actuator/prometheus`
- `http://gateway-service:8080/actuator/prometheus`

---

## ❌ Pourquoi ne pas monitorer `discovery-service` ?

Spring Cloud Netflix Eureka **n’expose pas par défaut** d’endpoint `/actuator/prometheus`, et sa structure ne s’y prête pas bien.

Plutôt que forcer une configuration fragile, on **choisit de le laisser hors monitoring**, car il ne porte **pas de charge métier directe**.

---

## ✅ Résultat attendu

Une fois tout en place :

- Les métriques des services sont visibles sur : [http://localhost:9090](http://localhost:9090)
- Les dashboards sont disponibles sur : [http://localhost:3000](http://localhost:3000)

Cela permet de :
- Voir en temps réel l’activité de chaque service.
- Être alerté si un service est down ou lent.
- Suivre la mémoire, le CPU, la fréquence des requêtes, etc.

---

## 📁 Fichiers importants

| Fichier                             | Rôle                                                    |
|-------------------------------------|----------------------------------------------------------|
| `monitoring/prometheus.yml`         | Configuration des scrapes Prometheus                     |
| `docker-compose.yml`                | Démarrage des services Prometheus + Grafana              |
| `application-docker.yml`            | Configuration des endpoints et export Prometheus         |
| `pom.xml` (dans chaque microservice) | Dépendances Actuator + Micrometer Prometheus             |
