# Ktor REST API

REST API на Kotlin + Ktor с JWT-аутентификацией, PostgreSQL и Koin DI.

## Технологии

| Библиотека | Назначение |
|---|---|
| [Ktor](https://ktor.io) (Netty) | HTTP-сервер |
| [Exposed](https://github.com/JetBrains/Exposed) | ORM / работа с БД |
| [PostgreSQL](https://www.postgresql.org) | База данных |
| [Koin](https://insert-koin.io) | Dependency Injection |
| [JWT (auth0)](https://github.com/auth0/java-jwt) | Аутентификация |
| [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization) | JSON-сериализация |
| [Swagger / OpenAPI](https://swagger.io) | Документация API |

## Структура проекта

```
src/main/kotlin/
├── Application.kt          # Точка входа
├── plugins/
│   ├── Auth.kt             # JWT-аутентификация
│   ├── Database.kt         # Подключение к БД и миграции
│   ├── DI.kt               # Koin-модули
│   ├── Errors.kt           # Глобальная обработка ошибок
│   ├── Routing.kt          # Регистрация маршрутов
│   └── Serialization.kt    # Content Negotiation / JSON
├── routes/
│   ├── AuthRoutes.kt       # /auth/register, /auth/login
│   ├── CityRoutes.kt       # /cities
│   └── UserRoutes.kt       # /users
├── services/
│   ├── CityService.kt
│   └── UserService.kt
├── repositories/
│   ├── CityRepository.kt
│   └── UserRepository.kt
└── models/                 # Data-классы и таблицы Exposed
```

## API

### Аутентификация

| Метод | Путь | Описание | Защищён |
|---|---|---|---|
| POST | `/auth/register` | Регистрация пользователя | Нет |
| POST | `/auth/login` | Получение JWT-токена | Нет |

**Логин — пример запроса:**
```http
POST /auth/login
Content-Type: application/json

{"email": "user@example.com", "password": "secret"}
```

**Ответ:**
```json
{"token": "<JWT>"}
```

Все последующие запросы к защищённым эндпоинтам требуют заголовок:
```
Authorization: Bearer <token>
```

### Cities

| Метод | Путь | Описание |
|---|---|---|
| GET | `/cities` | Список всех городов |
| GET | `/cities/{id}` | Город по ID |
| POST | `/cities` | Создать город |
| DELETE | `/cities/{id}` | Удалить город |

### Users

| Метод | Путь | Описание |
|---|---|---|
| GET | `/users` | Список всех пользователей |
| GET | `/users/{id}` | Пользователь по ID |
| POST | `/users` | Создать пользователя |
| DELETE | `/users/{id}` | Удалить пользователя |

## Запуск

### Docker Compose (рекомендуется)

```bash
docker-compose up --build
```

Сервис поднимается на `http://localhost:8080`, PostgreSQL — на порту `15432`.

### Локально

Требуется запущенный PostgreSQL. Укажите параметры через переменные окружения:

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

Интеграционные тесты используют Ktor `testApplication` и реальную базу данных.

## Документация API

После запуска Swagger UI доступен по адресу:
```
http://localhost:8080/swagger
```
