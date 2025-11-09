# SpringTen Smart Contracts


## My overviw of the contract 

### 1. SpringTenToken.sol
- **Purpose**: ERC-20 token for the SpringTen platform
- **Features**:
  - Standard ERC-20 functionality
  - Staking rewards system (10% APY)
  - Burnable tokens
  - Pausable transfers
  - Owner minting capabilities

### 2. SpringTenNFT.sol
- **Purpose**: ERC-721 NFT collection contract
- **Features**:
  - Standard ERC-721 functionality
  - Batch minting
  - Metadata URI storage
  - Enumerable tokens
  - Configurable minting price
  - Owner controls

### 3. SpringTenMarketplace.sol
- **Purpose**: NFT marketplace for buying/selling NFTs
- **Features**:
  - Fixed price sales
  - Auction functionality
  - Bidding system
  - Listing management
  - Fee collection
  - Reentrancy protection

## Quick Start

### Prerequisites
- Node.js (v16 or higher)
- npm or yarn
- Hardhat

### Installation
```bash
cd contracts
npm install
```

### Compile Contracts
```bash
npm run compile
```

### Run Tests
```bash
npm test
```

### Deploy to Local Network
```bash
# Start local Hardhat node
npm run node

# In another terminal, deploy contracts
npm run deploy
```

### Deploy to Testnet (Sepolia)
```bash
# Set environment variables
export SEPOLIA_URL="your_sepolia_rpc_url"
export PRIVATE_KEY="your_private_key"
export ETHERSCAN_API_KEY="your_etherscan_api_key"

# Deploy
npm run deploy:testnet
```

## Contract Addresses

After deployment, contract addresses will be saved to `deployments/{network}.json`.

### Example Deployment (Localhost)
```json
{
  "network": "localhost",
  "chainId": 1337,
  "contracts": {
    "SpringTenToken": "0x...",
    "SpringTenNFT": "0x...",
    "SpringTenMarketplace": "0x..."
  }
}
```

## Contract Functions

### SpringTenToken
- `mint(address to, uint256 amount)` - Mint new tokens (owner only)
- `stake(uint256 amount)` - Stake tokens for rewards
- `unstake(uint256 amount)` - Unstake tokens
- `claimRewards()` - Claim staking rewards
- `calculatePendingRewards(address user)` - Calculate pending rewards

### SpringTenNFT
- `mintNFT(address to, string tokenURI)` - Mint single NFT
- `batchMint(address to, string[] tokenURIs)` - Mint multiple NFTs
- `ownerMint(address to, string tokenURI)` - Owner mint (free)
- `getTokensByOwner(address owner)` - Get user's tokens
- `toggleMinting()` - Toggle minting on/off (owner only)

### SpringTenMarketplace
- `createMarketItem(address nftContract, uint256 tokenId, uint256 price, bool isAuction, uint256 auctionDuration)` - List NFT
- `createMarketSale(uint256 itemId)` - Buy NFT
- `placeBid(uint256 itemId)` - Place bid on auction
- `endAuction(uint256 itemId)` - End auction
- `fetchMarketItems()` - Get all market items
- `fetchUserListings(address user)` - Get user's listings
- `fetchUserPurchases(address user)` - Get user's purchases

## 🧪 Testing

The test suite covers:
- Token functionality (minting, staking, rewards)
- NFT functionality (minting, batch minting, metadata)
- Marketplace functionality (listing, buying, auctions, bidding)

Run tests with:
```bash
npm test
```

## 🔒 Security Features

- **ReentrancyGuard**: Prevents reentrancy attacks
- **Ownable**: Access control for admin functions
- **Pausable**: Emergency pause functionality
- **Input Validation**: Comprehensive input checking
- **Safe Math**: Overflow/underflow protection

## Gas Optimization

- **Optimized Solidity**: Version 0.8.19 with optimizer enabled
- **Efficient Storage**: Packed structs and optimized mappings
- **Batch Operations**: Batch minting and operations
- **Event Optimization**: Minimal event emissions

## Network Support

- **Local Development**: Hardhat local network
- **Testnets**: Sepolia, Goerli
- **Mainnet**: Ethereum mainnet
- **L2s**: Polygon, Arbitrum (configurable)

