# HW-3: Docker и CI/CD для API-автотестов

## Тест

API-автотесты из hw-2: Java 17 + Maven + JUnit 5 + REST Assured, пакет `ru.mts.hw3`.
Проверяют REST-стенд `https://playground.nileslabs.com/api/v1` (жизненный цикл поста
`POST`/`PATCH`/`GET`, Bearer-авторизация `POST /auth/login` → `GET /auth/me`) и
GraphQL `https://graphql.postman-echo.com/graphql`. Нужен интернет.

Креды стенда в репозитории не хранятся — читаются из `PLAYGROUND_USERNAME` и
`PLAYGROUND_PASSWORD` (в CI — repository secrets).

## Сборка

```bash
docker build -t api-test .
```

## Запуск

Тест запускается автоматически при старте контейнера:

```bash
docker run --rm \
  -e PLAYGROUND_USERNAME="$PLAYGROUND_USERNAME" \
  -e PLAYGROUND_PASSWORD="$PLAYGROUND_PASSWORD" \
  api-test
```

Exit code контейнера = exit code `mvn test`: `0` при успехе, ненулевой при падении.

## CI

Конфигурация — [`.github/workflows/ci.yml`](.github/workflows/ci.yml), запускается при **push**
(а также на pull request и вручную через `workflow_dispatch`):

- `build-image` — checkout и `docker build` из текущего состояния репозитория;
- `run-api-test` — `docker run` собранного образа, exit code теста определяет статус pipeline.

Успешный pipeline:
