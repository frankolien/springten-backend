// SPDX-License-Identifier: MIT
pragma solidity ^0.8.19;

import "@openzeppelin/contracts/security/ReentrancyGuard.sol";
import "@openzeppelin/contracts/access/Ownable.sol";
import "@openzeppelin/contracts/token/ERC721/IERC721.sol";
import "@openzeppelin/contracts/token/ERC20/IERC20.sol";
import "@openzeppelin/contracts/utils/Counters.sol";

contract SpringTenMarketplace is ReentrancyGuard, Ownable {
    using Counters for Counters.Counter;
    
    Counters.Counter private _itemIds;
    Counters.Counter private _itemsSold;
    
    uint256 public listingPrice = 0.025 ether; // 0.025 ETH listing fee
    
    struct MarketItem {
        uint256 itemId;
        address nftContract;
        uint256 tokenId;
        address seller;
        address owner;
        uint256 price;
        bool sold;
        bool isAuction;
        uint256 auctionEndTime;
        address highestBidder;
        uint256 highestBid;
    }
    
    mapping(uint256 => MarketItem) private idToMarketItem;
    mapping(address => uint256[]) private userListings;
    mapping(address => uint256[]) private userPurchases;
    
    event MarketItemCreated(
        uint256 indexed itemId,
        address indexed nftContract,
        uint256 indexed tokenId,
        address seller,
        address owner,
        uint256 price,
        bool isAuction
    );
    
    event MarketItemSold(
        uint256 indexed itemId,
        address indexed nftContract,
        uint256 indexed tokenId,
        address seller,
        address buyer,
        uint256 price
    );
    
    event BidPlaced(
        uint256 indexed itemId,
        address indexed bidder,
        uint256 amount
    );
    
    event AuctionEnded(
        uint256 indexed itemId,
        address indexed winner,
        uint256 winningBid
    );
    
    constructor() {
        _transferOwnership(msg.sender);
    }
    
    // Create a new market item (sale or auction)
    function createMarketItem(
        address nftContract,
        uint256 tokenId,
        uint256 price,
        bool isAuction,
        uint256 auctionDuration
    ) public payable nonReentrant {
        require(price > 0, "Price must be at least 1 wei");
        require(msg.value == listingPrice, "Price must be equal to listing price");
        
        _itemIds.increment();
        uint256 itemId = _itemIds.current();
        
        uint256 auctionEndTime = 0;
        if (isAuction) {
            auctionEndTime = block.timestamp + auctionDuration;
        }
        
        idToMarketItem[itemId] = MarketItem(
            itemId,
            nftContract,
            tokenId,
            msg.sender,
            address(this), // Marketplace owns the item initially
            price,
            false,
            isAuction,
            auctionEndTime,
            address(0),
            0
        );
        
        IERC721(nftContract).transferFrom(msg.sender, address(this), tokenId);
        userListings[msg.sender].push(itemId);
        
        emit MarketItemCreated(
            itemId,
            nftContract,
            tokenId,
            msg.sender,
            address(this),
            price,
            isAuction
        );
    }
    
    // Buy a market item
    function createMarketSale(uint256 itemId) public payable nonReentrant {
        MarketItem storage item = idToMarketItem[itemId];
        require(!item.sold, "Item already sold");
        require(!item.isAuction, "Item is in auction");
        require(msg.value == item.price, "Please submit the asking price");
        
        item.sold = true;
        item.owner = msg.sender;
        _itemsSold.increment();
        
        IERC721(item.nftContract).transferFrom(address(this), msg.sender, item.tokenId);
        payable(item.seller).transfer(msg.value);
        userPurchases[msg.sender].push(itemId);
        
        emit MarketItemSold(
            itemId,
            item.nftContract,
            item.tokenId,
            item.seller,
            msg.sender,
            item.price
        );
    }
    
    // Place a bid on an auction item
    function placeBid(uint256 itemId) public payable nonReentrant {
        MarketItem storage item = idToMarketItem[itemId];
        require(item.isAuction, "Item is not in auction");
        require(!item.sold, "Auction already ended");
        require(block.timestamp < item.auctionEndTime, "Auction has ended");
        require(msg.value > item.highestBid, "Bid must be higher than current highest bid");
        require(msg.sender != item.seller, "Seller cannot bid on own item");
        
        // Return previous highest bid
        if (item.highestBidder != address(0)) {
            payable(item.highestBidder).transfer(item.highestBid);
        }
        
        item.highestBidder = msg.sender;
        item.highestBid = msg.value;
        
        emit BidPlaced(itemId, msg.sender, msg.value);
    }
    
    // End an auction and transfer NFT to winner
    function endAuction(uint256 itemId) public nonReentrant {
        MarketItem storage item = idToMarketItem[itemId];
        require(item.isAuction, "Item is not in auction");
        require(!item.sold, "Auction already ended");
        require(block.timestamp >= item.auctionEndTime, "Auction has not ended yet");
        require(msg.sender == item.seller || msg.sender == owner(), "Only seller or owner can end auction");
        
        item.sold = true;
        item.owner = item.highestBidder;
        _itemsSold.increment();
        
        if (item.highestBidder != address(0)) {
            IERC721(item.nftContract).transferFrom(address(this), item.highestBidder, item.tokenId);
            payable(item.seller).transfer(item.highestBid);
            userPurchases[item.highestBidder].push(itemId);
            
            emit AuctionEnded(itemId, item.highestBidder, item.highestBid);
        } else {
            // No bids, return NFT to seller
            IERC721(item.nftContract).transferFrom(address(this), item.seller, item.tokenId);
        }
    }
    
    // Cancel a listing (only if no bids on auction)
    function cancelListing(uint256 itemId) public nonReentrant {
        MarketItem storage item = idToMarketItem[itemId];
        require(msg.sender == item.seller, "Only seller can cancel");
        require(!item.sold, "Item already sold");
        
        if (item.isAuction) {
            require(item.highestBidder == address(0), "Cannot cancel auction with bids");
        }
        
        item.sold = true;
        IERC721(item.nftContract).transferFrom(address(this), item.seller, item.tokenId);
    }
    
    // Fetch all unsold items
    function fetchMarketItems() public view returns (MarketItem[] memory) {
        uint256 itemCount = _itemIds.current();
        uint256 unsoldItemCount = _itemIds.current() - _itemsSold.current();
        uint256 currentIndex = 0;
        
        MarketItem[] memory items = new MarketItem[](unsoldItemCount);
        for (uint256 i = 0; i < itemCount; i++) {
            if (idToMarketItem[i + 1].owner == address(this)) {
                uint256 currentId = i + 1;
                MarketItem storage currentItem = idToMarketItem[currentId];
                items[currentIndex] = currentItem;
                currentIndex += 1;
            }
        }
        return items;
    }
    
    // Fetch user's listings
    function fetchUserListings(address user) public view returns (MarketItem[] memory) {
        uint256[] memory userItemIds = userListings[user];
        MarketItem[] memory items = new MarketItem[](userItemIds.length);
        
        for (uint256 i = 0; i < userItemIds.length; i++) {
            items[i] = idToMarketItem[userItemIds[i]];
        }
        return items;
    }
    
    // Fetch user's purchases
    function fetchUserPurchases(address user) public view returns (MarketItem[] memory) {
        uint256[] memory userItemIds = userPurchases[user];
        MarketItem[] memory items = new MarketItem[](userItemIds.length);
        
        for (uint256 i = 0; i < userItemIds.length; i++) {
            items[i] = idToMarketItem[userItemIds[i]];
        }
        return items;
    }
    
    // Update listing price (only owner)
    function updateListingPrice(uint256 _listingPrice) public onlyOwner {
        listingPrice = _listingPrice;
    }
    
    // Withdraw contract balance (only owner)
    function withdraw() public onlyOwner {
        payable(owner()).transfer(address(this).balance);
    }
    
    // Get contract balance
    function getBalance() public view returns (uint256) {
        return address(this).balance;
    }
}
