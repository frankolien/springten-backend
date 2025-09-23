// SPDX-License-Identifier: MIT
pragma solidity ^0.8.19;

import "@openzeppelin/contracts/token/ERC721/ERC721.sol";
import "@openzeppelin/contracts/token/ERC721/extensions/ERC721URIStorage.sol";
import "@openzeppelin/contracts/token/ERC721/extensions/ERC721Enumerable.sol";
import "@openzeppelin/contracts/access/Ownable.sol";
import "@openzeppelin/contracts/utils/Counters.sol";

contract SpringTenNFT is ERC721, ERC721URIStorage, ERC721Enumerable, Ownable {
    using Counters for Counters.Counter;
    
    Counters.Counter private _tokenIdCounter;
    
    // Base URI for metadata
    string private _baseTokenURI;
    
    // Collection info
    string public collectionName;
    string public collectionSymbol;
    uint256 public maxSupply;
    uint256 public mintPrice;
    bool public mintingActive;
    
    // Events
    event NFTMinted(address indexed to, uint256 indexed tokenId, string tokenURI);
    event BatchMinted(address indexed to, uint256[] tokenIds);
    event MintingToggled(bool active);
    event PriceUpdated(uint256 newPrice);
    
    constructor(
        string memory _name,
        string memory _symbol,
        uint256 _maxSupply,
        uint256 _mintPrice
    ) ERC721(_name, _symbol) {
        collectionName = _name;
        collectionSymbol = _symbol;
        maxSupply = _maxSupply;
        mintPrice = _mintPrice;
        mintingActive = true;
        _transferOwnership(msg.sender);
    }
    
    // Mint a single NFT
    function mintNFT(address to, string memory tokenURI) public payable returns (uint256) {
        require(mintingActive, "Minting is not active");
        require(msg.value >= mintPrice, "Insufficient payment");
        require(totalSupply() < maxSupply, "Max supply reached");
        require(to != address(0), "Cannot mint to zero address");
        
        uint256 tokenId = _tokenIdCounter.current();
        _tokenIdCounter.increment();
        
        _safeMint(to, tokenId);
        _setTokenURI(tokenId, tokenURI);
        
        // Refund excess payment
        if (msg.value > mintPrice) {
            payable(msg.sender).transfer(msg.value - mintPrice);
        }
        
        emit NFTMinted(to, tokenId, tokenURI);
        return tokenId;
    }
    
    // Mint multiple NFTs in a batch
    function batchMint(
        address to,
        string[] memory tokenURIs
    ) public payable returns (uint256[] memory) {
        require(mintingActive, "Minting is not active");
        require(msg.value >= mintPrice * tokenURIs.length, "Insufficient payment");
        require(totalSupply() + tokenURIs.length <= maxSupply, "Exceeds max supply");
        require(to != address(0), "Cannot mint to zero address");
        
        uint256[] memory tokenIds = new uint256[](tokenURIs.length);
        
        for (uint256 i = 0; i < tokenURIs.length; i++) {
            uint256 tokenId = _tokenIdCounter.current();
            _tokenIdCounter.increment();
            
            _safeMint(to, tokenId);
            _setTokenURI(tokenId, tokenURIs[i]);
            tokenIds[i] = tokenId;
        }
        
        // Refund excess payment
        uint256 totalCost = mintPrice * tokenURIs.length;
        if (msg.value > totalCost) {
            payable(msg.sender).transfer(msg.value - totalCost);
        }
        
        emit BatchMinted(to, tokenIds);
        return tokenIds;
    }
    
    // Owner can mint for free
    function ownerMint(address to, string memory tokenURI) public onlyOwner returns (uint256) {
        require(totalSupply() < maxSupply, "Max supply reached");
        require(to != address(0), "Cannot mint to zero address");
        
        uint256 tokenId = _tokenIdCounter.current();
        _tokenIdCounter.increment();
        
        _safeMint(to, tokenId);
        _setTokenURI(tokenId, tokenURI);
        
        emit NFTMinted(to, tokenId, tokenURI);
        return tokenId;
    }
    
    // Get all tokens owned by an address
    function getTokensByOwner(address owner) public view returns (uint256[] memory) {
        uint256 tokenCount = balanceOf(owner);
        uint256[] memory tokenIds = new uint256[](tokenCount);
        
        for (uint256 i = 0; i < tokenCount; i++) {
            tokenIds[i] = tokenOfOwnerByIndex(owner, i);
        }
        
        return tokenIds;
    }
    
    // Get token metadata
    function getTokenMetadata(uint256 tokenId) public view returns (string memory) {
        require(_exists(tokenId), "Token does not exist");
        return tokenURI(tokenId);
    }
    
    // Update base URI
    function setBaseURI(string memory baseURI) public onlyOwner {
        _baseTokenURI = baseURI;
    }
    
    // Toggle minting
    function toggleMinting() public onlyOwner {
        mintingActive = !mintingActive;
        emit MintingToggled(mintingActive);
    }
    
    // Update mint price
    function updateMintPrice(uint256 newPrice) public onlyOwner {
        mintPrice = newPrice;
        emit PriceUpdated(newPrice);
    }
    
    // Withdraw contract balance
    function withdraw() public onlyOwner {
        uint256 balance = address(this).balance;
        require(balance > 0, "No funds to withdraw");
        payable(owner()).transfer(balance);
    }
    
    // Override required functions for multiple inheritance
    function _beforeTokenTransfer(
        address from,
        address to,
        uint256 tokenId,
        uint256 batchSize
    ) internal override(ERC721, ERC721Enumerable) {
        super._beforeTokenTransfer(from, to, tokenId, batchSize);
    }
    
    function _burn(uint256 tokenId) internal override(ERC721, ERC721URIStorage) {
        super._burn(tokenId);
    }
    
    function tokenURI(uint256 tokenId)
        public
        view
        override(ERC721, ERC721URIStorage)
        returns (string memory)
    {
        return super.tokenURI(tokenId);
    }
    
    function supportsInterface(bytes4 interfaceId)
        public
        view
        override(ERC721, ERC721Enumerable, ERC721URIStorage)
        returns (bool)
    {
        return super.supportsInterface(interfaceId);
    }
    
    // Get contract info
    function getContractInfo() public view returns (
        string memory name,
        string memory symbol,
        uint256 totalSupplyCount,
        uint256 maxSupplyCount,
        uint256 price,
        bool active
    ) {
        return (
            collectionName,
            collectionSymbol,
            totalSupply(),
            maxSupply,
            mintPrice,
            mintingActive
        );
    }
}
