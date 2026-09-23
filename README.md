# API de gestion d'un inventaire de produits

API REST back-end permettant de gérer un inventaire de produits avec suivi des stocks et alerte de stock bas. Développée avec Spring Boot 4, Spring Data JPA, PostgreSQL et Flyway.

## Prérequis

- Java 17
- Maven
- Docker et Docker Compose (pour PostgreSQL)

## Installation et lancement

```bash
cd inventory-api
mvn spring-boot:run -Dspring-boot.run.profiles=postgres
```

L'API est disponible sur : `http://localhost:8080`

> Profil `default` (H2 en mémoire) disponible pour un test rapide sans Docker : `mvn spring-boot:run`.

## Documentation Swagger

- **Swagger UI** : http://localhost:8080/swagger-ui/index.html
- **Spécification OpenAPI** : http://localhost:8080/v3/api-docs

## Architecture

```
com.inventoryapi
├── controller   → ProductController (CRUD + alerte stock bas)
├── service      → ProductService
├── repository   → ProductRepository
├── entity       → Product
├── dto          → ProductRequest, ProductResponse
├── mapper       → ProductMapper (calcule le flag lowStock)
└── exception    → GlobalExceptionHandler, ProductNotFoundException
```

## Modèle de données

| Champ | Type | Contrainte |
|---|---|---|
| `id` | Long | généré automatiquement |
| `name` | String | obligatoire, max 150 caractères |
| `price` | BigDecimal | obligatoire, strictement positif |
| `quantity` | Integer | obligatoire, ≥ 0 |

Chaque produit renvoyé inclut un champ `lowStock` (booléen), `true` si la quantité est strictement inférieure à **5** unités.

## Endpoints et exemples

### Créer un produit

```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Clavier mécanique",
    "price": 49.99,
    "quantity": 20
  }'
```

### Lister tous les produits

```bash
curl http://localhost:8080/api/products
```

### Récupérer un produit

```bash
curl http://localhost:8080/api/products/1
```

### Mettre à jour un produit (prix, quantité...)

```bash
curl -X PUT http://localhost:8080/api/products/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Clavier mécanique",
    "price": 44.99,
    "quantity": 3
  }'
```

### Supprimer un produit

```bash
curl -X DELETE http://localhost:8080/api/products/1
```

### Lister les produits en stock bas

```bash
# Seuil par défaut (5)
curl http://localhost:8080/api/products/low-stock

# Seuil personnalisé
curl "http://localhost:8080/api/products/low-stock?threshold=10"
```

Réponse `200 OK` :
```json
[
  { "id": 1, "name": "Souris sans fil", "price": 15.00, "quantity": 2, "lowStock": true }
]
```

## Gestion des erreurs

| Code | Cas |
|---|---|
| `400` | Nom manquant, prix négatif ou nul, quantité négative |
| `404` | Produit introuvable |
| `500` | Erreur interne inattendue |

## Tester dans Swagger UI

1. Ouvrir http://localhost:8080/swagger-ui/index.html
2. Créer 2-3 produits via `POST /api/products`, dont un avec `quantity` < 5
3. Appeler `GET /api/products/low-stock` et vérifier que seul ce produit apparaît

## Tests

```bash
mvn test
```

- `ProductServiceTest` : logique métier (Mockito) — création, mise à jour, suppression, calcul du stock bas
- `ProductControllerTest` : couche web (`@WebMvcTest`, `MockMvc`)

## Stack technique

- Java 17 / Spring Boot 4
- Spring Data JPA / Hibernate
- PostgreSQL + Flyway
- springdoc-openapi (Swagger UI)
- JUnit 5 / Mockito / AssertJ
