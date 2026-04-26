# CryptoPulse

CryptoPulse is a Kotlin/Ktor web application for tracking cryptocurrency market data.

It provides a REST API, a simple Web UI, watchlist management, price history, manual refresh, and live price updates.

The aim of the project was to demonstrate coroutines and flow usage in a Kotlin backend application.
## Features

- Cryptocurrency dashboard
- Asset details page
- Price history chart
- Watchlist stored in SQLite
- Manual asset refresh
- Live price updates without page reload
- Backend-served HTML, CSS and JavaScript
- CoinGecko REST API integration
- Binance WebSocket stream integration

## Tech Stack

- Kotlin
- Ktor
- Kotlin Coroutines
- Kotlin Flow
- Ktor HTTP Client
- Ktor Server-Sent Events
- Exposed
- SQLite
- kotlinx.serialization
- Koin
- Vanilla HTML/CSS/JavaScript

## Architecture

Main application layers for backend:

- `routes` — HTTP endpoints
- `services` — application logic
- `clients` — external API clients
- `repositories` — database access
- `streams` — live event handling
- `models` — DTOs, domain models and requests

## Coroutines and Flow Usage

The project uses Kotlin Coroutines and Flow in several places:

- `suspend` functions for CoinGecko API calls
- `coroutineScope` and `async` for loading asset screen data in parallel
- `withContext(Dispatchers.IO)` for blocking database operations
- background coroutine for Binance WebSocket streaming
- `StateFlow` for synchronization status and for cache
- `SharedFlow` as a price update event bus
- `retryWhen`, `onEach`, `onStart` and `onCompletion` for stream lifecycle handling
- SSE endpoint for streaming price updates to the browser

## REST API

Main endpoints:

- `GET /api/assets`
- `GET /api/assets/{id}`
- `GET /api/assets/{id}/history`
- `GET /api/assets/{id}/screen`
- `GET /api/watchlist`
- `POST /api/watchlist`
- `DELETE /api/watchlist/{assetId}`
- `GET /api/status/sync`
- `POST /api/refresh`
- `GET /api/events/prices`

## Live Updates

Live price updates flow through the system like this:

```text
Binance WebSocket
-> BinanceStreamClient
-> PriceEventBus
-> SSE endpoint
-> EventSource in browser
-> UI price update
```
## Local Configuration

Create `local.properties` in the project root:

```properties
coingecko.demo.api.key=your_api_key_here
```

Do not commit this file.

## Running
```
./gradlew run
```
Open:
```
http://localhost:8080
```