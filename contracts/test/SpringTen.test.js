const { expect } = require("chai");
const { ethers } = require("hardhat");

describe("SpringTen Contracts", function () {
  let springTenToken;
  let springTenNFT;
  let springTenMarketplace;
  let owner;
  let addr1;
  let addr2;
  let addrs;

  beforeEach(async function () {
    [owner, addr1, addr2, ...addrs] = await ethers.getSigners();

    // Deploy SpringTen Token
    const SpringTenToken = await ethers.getContractFactory("SpringTenToken");
    springTenToken = await SpringTenToken.deploy();
    await springTenToken.deployed();

    // Deploy SpringTen NFT
    const SpringTenNFT = await ethers.getContractFactory("SpringTenNFT");
    springTenNFT = await SpringTenNFT.deploy(
      "SpringTen Collection",
      "SPRINGNFT",
      10000,
      ethers.utils.parseEther("0.01")
    );
    await springTenNFT.deployed();

    // Deploy SpringTen Marketplace
    const SpringTenMarketplace = await ethers.getContractFactory("SpringTenMarketplace");
    springTenMarketplace = await SpringTenMarketplace.deploy();
    await springTenMarketplace.deployed();
  });

  describe("SpringTen Token", function () {
    it("Should have correct name and symbol", async function () {
      expect(await springTenToken.name()).to.equal("SpringTen Token");
      expect(await springTenToken.symbol()).to.equal("SPRING");
    });

    it("Should have correct initial supply", async function () {
      const totalSupply = await springTenToken.totalSupply();
      expect(totalSupply).to.equal(ethers.utils.parseEther("100000000")); // 100 million
    });

    it("Should allow staking", async function () {
      const stakeAmount = ethers.utils.parseEther("1000");
      
      // Transfer tokens to addr1
      await springTenToken.transfer(addr1.address, stakeAmount);
      
      // Approve and stake
      await springTenToken.connect(addr1).approve(springTenToken.address, stakeAmount);
      await springTenToken.connect(addr1).stake(stakeAmount);
      
      const stakingInfo = await springTenToken.getStakingInfo(addr1.address);
      expect(stakingInfo.stakedAmount).to.equal(stakeAmount);
      expect(stakingInfo.isActive).to.be.true;
    });

    it("Should calculate rewards correctly", async function () {
      const stakeAmount = ethers.utils.parseEther("1000");
      
      // Transfer tokens to addr1
      await springTenToken.transfer(addr1.address, stakeAmount);
      
      // Approve and stake
      await springTenToken.connect(addr1).approve(springTenToken.address, stakeAmount);
      await springTenToken.connect(addr1).stake(stakeAmount);
      
      // Fast forward time (simulate 1 day)
      await ethers.provider.send("evm_increaseTime", [86400]); // 1 day
      await ethers.provider.send("evm_mine");
      
      const pendingRewards = await springTenToken.calculatePendingRewards(addr1.address);
      expect(pendingRewards).to.be.gt(0);
    });
  });

  describe("SpringTen NFT", function () {
    it("Should have correct collection info", async function () {
      const contractInfo = await springTenNFT.getContractInfo();
      expect(contractInfo.name).to.equal("SpringTen Collection");
      expect(contractInfo.symbol).to.equal("SPRINGNFT");
      expect(contractInfo.maxSupply).to.equal(10000);
    });

    it("Should allow minting with payment", async function () {
      const mintPrice = await springTenNFT.mintPrice();
      const tokenURI = "https://api.springten.com/metadata/1";
      
      await springTenNFT.connect(addr1).mintNFT(addr1.address, tokenURI, {
        value: mintPrice
      });
      
      expect(await springTenNFT.balanceOf(addr1.address)).to.equal(1);
      expect(await springTenNFT.ownerOf(1)).to.equal(addr1.address);
    });

    it("Should allow batch minting", async function () {
      const mintPrice = await springTenNFT.mintPrice();
      const tokenURIs = [
        "https://api.springten.com/metadata/1",
        "https://api.springten.com/metadata/2",
        "https://api.springten.com/metadata/3"
      ];
      
      const totalCost = mintPrice.mul(3);
      await springTenNFT.connect(addr1).batchMint(addr1.address, tokenURIs, {
        value: totalCost
      });
      
      expect(await springTenNFT.balanceOf(addr1.address)).to.equal(3);
    });
  });

  describe("SpringTen Marketplace", function () {
    it("Should allow creating market items", async function () {
      // First mint an NFT
      const mintPrice = await springTenNFT.mintPrice();
      await springTenNFT.connect(addr1).mintNFT(addr1.address, "https://api.springten.com/metadata/1", {
        value: mintPrice
      });
      
      // Approve marketplace to transfer NFT
      await springTenNFT.connect(addr1).approve(springTenMarketplace.address, 1);
      
      // Create market item
      const listingPrice = await springTenMarketplace.listingPrice();
      const itemPrice = ethers.utils.parseEther("0.1");
      
      await springTenMarketplace.connect(addr1).createMarketItem(
        springTenNFT.address,
        1,
        itemPrice,
        false, // Not an auction
        0, // No auction duration
        { value: listingPrice }
      );
      
      const marketItems = await springTenMarketplace.fetchMarketItems();
      expect(marketItems.length).to.equal(1);
      expect(marketItems[0].price).to.equal(itemPrice);
    });

    it("Should allow buying market items", async function () {
      // First mint an NFT
      const mintPrice = await springTenNFT.mintPrice();
      await springTenNFT.connect(addr1).mintNFT(addr1.address, "https://api.springten.com/metadata/1", {
        value: mintPrice
      });
      
      // Approve marketplace to transfer NFT
      await springTenNFT.connect(addr1).approve(springTenMarketplace.address, 1);
      
      // Create market item
      const listingPrice = await springTenMarketplace.listingPrice();
      const itemPrice = ethers.utils.parseEther("0.1");
      
      await springTenMarketplace.connect(addr1).createMarketItem(
        springTenNFT.address,
        1,
        itemPrice,
        false,
        0,
        { value: listingPrice }
      );
      
      // Buy the item
      await springTenMarketplace.connect(addr2).createMarketSale(1, {
        value: itemPrice
      });
      
      // Check that NFT was transferred to buyer
      expect(await springTenNFT.ownerOf(1)).to.equal(addr2.address);
      
      const marketItems = await springTenMarketplace.fetchMarketItems();
      expect(marketItems.length).to.equal(0); // Should be empty after sale
    });

    it("Should allow auction functionality", async function () {
      // First mint an NFT
      const mintPrice = await springTenNFT.mintPrice();
      await springTenNFT.connect(addr1).mintNFT(addr1.address, "https://api.springten.com/metadata/1", {
        value: mintPrice
      });
      
      // Approve marketplace to transfer NFT
      await springTenNFT.connect(addr1).approve(springTenMarketplace.address, 1);
      
      // Create auction item
      const listingPrice = await springTenMarketplace.listingPrice();
      const startingPrice = ethers.utils.parseEther("0.1");
      const auctionDuration = 3600; // 1 hour
      
      await springTenMarketplace.connect(addr1).createMarketItem(
        springTenNFT.address,
        1,
        startingPrice,
        true, // Is auction
        auctionDuration,
        { value: listingPrice }
      );
      
      // Place bids
      const bid1 = ethers.utils.parseEther("0.2");
      const bid2 = ethers.utils.parseEther("0.3");
      
      await springTenMarketplace.connect(addr2).placeBid(1, { value: bid1 });
      await springTenMarketplace.connect(addrs[0]).placeBid(1, { value: bid2 });
      
      // Fast forward time to end auction
      await ethers.provider.send("evm_increaseTime", [auctionDuration + 1]);
      await ethers.provider.send("evm_mine");
      
      // End auction
      await springTenMarketplace.connect(addr1).endAuction(1);
      
      // Check that NFT was transferred to highest bidder
      expect(await springTenNFT.ownerOf(1)).to.equal(addrs[0].address);
    });
  });
});
