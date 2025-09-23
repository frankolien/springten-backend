# Springten - Web3 Crypto Wallet Backend

A comprehensive Spring Boot backend for a Web3 crypto wallet application, supporting multiple blockchain networks and advanced wallet features.

## Features

### 🔐 Security & Authentication
- JWT-based authentication
- Biometric security integration
- Secure wallet creation with recovery phrases
- Password encryption with BCrypt

### 💼 Wallet Management
- Multi-blockchain wallet support (Ethereum, Bitcoin, Polygon, BSC, Solana)
- Wallet creation and address generation
- Balance checking and transaction history
- Private key encryption and secure storage

### 🔄 Trading Operations
- Token swapping (DEX integration)
- Buy/Sell operations with fiat onramps
- Real-time price data integration
- Transaction fee calculation

### 📊 Additional Features
- Staking operations
- Transaction history and analytics
- Real-time price charts
- Multi-token portfolio management

## Tech Stack

### Backend Framework
- **Spring Boot 3.5.6** - Main application framework
- **Java 17** - Programming language
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Database operations

### Databases
- **PostgreSQL** - Primary database for user data and transactions
- **MongoDB** - Transaction logs and analytics
- **Redis** - Caching and session management

### Web3 Integration
- **Web3j** - Ethereum blockchain integration
- **BitcoinJ** - Bitcoin blockchain integration
- **BouncyCastle** - Cryptographic operations

### External Services
- **CoinGecko API** - Real-time crypto prices
- **Stripe** - Payment processing
- **Infura** - Ethereum node provider

## API Endpoints

### Authentication
```
POST /api/auth/register    - User registration
POST /api/auth/login       - User login
POST /api/auth/refresh     - Token refresh
```

### Wallet Operations
```
GET  /api/wallet/balance   - Get wallet balance
GET  /api/wallet/info      - Get wallet information
POST /api/wallet/create    - Create new wallet
```

### Public Endpoints
```
GET  /api/public/health    - Health check
GET  /api/public/info      - Service information
```

## Getting Started

### Prerequisites
- Java 17+
- Maven 3.6+
- PostgreSQL 12+
- MongoDB 4.4+
- Redis 6+

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd springton
   ```

2. **Configure databases**
   - Create PostgreSQL database: `springten_wallet`
   - Create MongoDB database: `springten_transactions`
   - Start Redis server

3. **Update configuration**
   Edit `src/main/resources/application.properties`:
   ```properties
   # Update database credentials
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   
   # Add your API keys
   coingecko.api.key=your_coingecko_key
   stripe.secret-key=your_stripe_key
   web3.ethereum.rpc-url=your_infura_url
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

5. **Test the API**
   ```bash
   curl http://localhost:8080/api/public/health
   ```

## Configuration

### Environment Variables
- `JWT_SECRET` - Secret key for JWT tokens
- `DB_PASSWORD` - Database password
- `REDIS_PASSWORD` - Redis password
- `COINGECKO_API_KEY` - CoinGecko API key
- `STRIPE_SECRET_KEY` - Stripe secret key
- `INFURA_PROJECT_ID` - Infura project ID

### Database Schema
The application automatically creates the following tables:
- `users` - User accounts and profiles
- `wallets` - Wallet addresses and metadata
- `tokens` - Supported cryptocurrency tokens
- `token_balances` - User token balances
- `transactions` - Transaction history

## Security Considerations

- All private keys are encrypted before storage
- Recovery phrases are hashed and stored securely
- JWT tokens have expiration times
- CORS is configured for cross-origin requests
- Input validation on all API endpoints

## Development

### Running Tests
```bash
mvn test
```

### Building for Production
```bash
mvn clean package
java -jar target/springton-0.0.1-SNAPSHOT.jar
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For support and questions, please open an issue in the GitHub repository.
