# 🎯 Event Service — Microservice de gestion des événements

## 🧱 Objectif

Ce microservice est responsable de la création, la mise à jour, la suppression et la récupération des événements (meetings, soirées, anniversaires, etc.).
Il centralise toutes les données liées aux événements pour l'ensemble des utilisateurs.

---

## 🛠️ Stack technique

- Java 17
- Spring Boot 3.x
- Spring Web
- Spring Data JPA
- PostgreSQL
- Lombok
- Maven

---

## 🗂 Structure typique

event-service/
└── src/main/java/fr/pedro/event_service/
├── controller
├── model
├── repository
├── service
└── EventServiceApplication.java


---

## 🧪 Exemple de routes REST

| Méthode | URL                   | Description                    |
|--------|------------------------|--------------------------------|
| GET    | `/api/events`          | Récupérer tous les événements |
| GET    | `/api/events/{id}`     | Détail d’un événement         |
| POST   | `/api/events`          | Créer un nouvel événement     |
| PUT    | `/api/events/{id}`     | Mettre à jour un événement    |
| DELETE | `/api/events/{id}`     | Supprimer un événement        |

---

## 📦 Exemple JSON

```json
{
  "title": "Apéro rooftop",
  "description": "Soirée chill entre potes",
  "date": "2025-07-01T19:00:00",
  "location": "Lyon, France",
  "participants": ["uuid-user-1", "uuid-user-2"]
}
```

## 🧪 Exemple de test JUnit


```java
@Test
void shouldCreateEventSuccessfully() {
    Event event = new Event("Titre", "Description", LocalDateTime.now(), "Paris");
    Event saved = eventRepository.save(event);
    assertNotNull(saved.getId());
}
```

## 🚀 Evolutions possibles
- Ajouter une pagination sur la liste
- Support de la recherche (par titre, date, lieu…)
- Notifications / intégration calendrier
