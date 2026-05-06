# Ktor REST API

## Технологии

| Библиотека | Версия | Назначение |
|---|---|---|
| [Ktor](https://ktor.io) (Netty) | 3.x | HTTP-сервер |
| [Exposed](https://github.com/JetBrains/Exposed) | 0.61 | ORM / работа с БД |
| [PostgreSQL](https://www.postgresql.org) | 16 | База данных |
| [Koin](https://insert-koin.io) | 4.x | Dependency Injection |
| [JWT (auth0)](https://github.com/auth0/java-jwt) | — | Аутентификация |
| [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization) | — | JSON-сериализация |
| [Swagger / OpenAPI](https://swagger.io) | — | Документация API |
| Kotlin | 2.x / JVM 21 | Язык |

## Архитектура

Проект следует классической слоистой архитектуре:

```
HTTP-запрос
    │
    ▼
Routes          — принимают запрос, десериализуют тело, возвращают ответ
    │
    ▼
Services        — бизнес-логика и валидация входных данных
    │
    ▼
Repositories    — работа с БД через Exposed ORM
    │
    ▼
PostgreSQL
```

Зависимости между слоями разрешаются через **Koin** — все сервисы и репозитории
регистрируются в DI-модуле и инжектируются в роуты через `by inject()`.

## Структура проекта

```
src/main/kotlin/
├── Application.kt          # Точка входа, регистрация плагинов
├── plugins/
│   ├── Auth.kt             # JWT-аутентификация (HMAC256)
│   ├── Database.kt         # Подключение к БД, создание таблиц
│   ├── DI.kt               # Koin-модули
│   ├── Errors.kt           # Глобальная обработка ошибок → 400/404/500
│   ├── Routing.kt          # Регистрация маршрутов и защищённых блоков
│   └── Serialization.kt    # Content Negotiation / JSON
├── routes/
│   ├── AuthRoutes.kt       # POST /auth/register, POST /auth/login
│   ├── CityRoutes.kt       # CRUD /cities
│   └── UserRoutes.kt       # CRUD /users
├── services/
│   ├── CityService.kt      # Валидация данных города
│   └── UserService.kt      # Валидация данных пользователя
├── repositories/
│   ├── CityRepository.kt   # SQL-запросы к таблице cities
│   └── UserRepository.kt   # SQL-запросы к таблице users
└── models/
    ├── City.kt / CityTable.kt
    ├── User.kt / UserTable.kt
    ├── CreateCityRequest.kt / CreateUserRequest.kt
    ├── UserResponse.kt
    └── ErrorResponse.kt
```

## API

Все маршруты, кроме `/auth/*`, требуют JWT-токен в заголовке:
```
Authorization: Bearer <token>
```

### Аутентификация

| Метод | Путь | Тело запроса | Ответ |
|---|---|---|---|
| POST | `/auth/register` | `{name, email, hashPassword}` | `201 UserResponse` |
| POST | `/auth/login` | `{name, email, hashPassword}` | `200 {token}` |

**Получение токена:**
```http
POST /auth/login
Content-Type: application/json

{
  "name": "Igor",
  "email": "igor@example.com",
  "hashPassword": "secret"
}
```
```json
{ "token": "eyJhbGciOiJIUzI1NiJ9..." }
```

### Cities

| Метод | Путь | Описание | Успешный ответ |
|---|---|---|---|
| GET | `/cities` | Список всех городов | `200 [City]` |
| GET | `/cities/{id}` | Город по ID | `200 City` |
| POST | `/cities` | Создать город | `201 City` |
| DELETE | `/cities/{id}` | Удалить город | `204` |

### Users

| Метод | Путь | Описание | Успешный ответ |
|---|---|---|---|
| GET | `/users` | Список всех пользователей | `200 [UserResponse]` |
| GET | `/users/{id}` | Пользователь по ID | `200 UserResponse` |
| POST | `/users` | Создать пользователя | `201 UserResponse` |
| DELETE | `/users/{id}` | Удалить пользователя | `204` |

### Коды ошибок

| Код | Причина |
|---|---|
| `400 Bad Request` | Невалидные данные (пустые поля, неверный тип ID) |
| `401 Unauthorized` | Отсутствует или невалидный JWT-токен |
| `404 Not Found` | Ресурс с указанным ID не существует |
| `500 Internal Server Error` | Непредвиденная ошибка сервера |

## Запуск

### Docker Compose (рекомендуется)

```bash
docker-compose up --build
```

Поднимает приложение на `http://localhost:8080` и PostgreSQL на порту `15432`.

### Локально

Требуется запущенный PostgreSQL. Передайте параметры подключения через переменные окружения:

```bash
export DB_URL=jdbc:postgresql://localhost:5432/cities
export DB_USER=user
export DB_PASSWORD=password

./gradlew run
```

После успешного старта в логах появится:
```
Application started in 0.303 seconds.
Responding at http://0.0.0.0:8080
```

## Тесты

```bash
./gradlew test
```

Интеграционные тесты используют Ktor `testApplication` и запускаются против реальной базы данных.

## Документация API

Swagger UI доступен после запуска по адресу:
```
http://localhost:8080/swagger
```
