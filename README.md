# Springten Backend

Spring Boot Web3 crypto wallet backend.

## Tech Stack

- Spring Boot 3.5.6
- Java 17
- H2/PostgreSQL
- MongoDB
- Redis
- Web3j
- BitcoinJ
- JWT

## Features

- JWT authentication
- Multi-blockchain wallet support
- Token operations
- Real-time price data
- Transaction history

## Quick Start

1. Clone repository
2. Update `application.properties` with your API keys
3. Run: `mvn spring-boot:run`
4. Test: `curl http://localhost:8080/api/public/health`

## Configuration

Set these in `application.properties`:
- `coingecko.api.key`
- `stripe.secret-key`
- `web3.ethereum.rpc-url`

## API Endpoints

```
POST /api/auth/register
POST /api/auth/login
GET  /api/wallet/balance
POST /api/wallet/create
GET  /api/public/health
```

## Development

```bash
mvn test
mvn clean package
```
