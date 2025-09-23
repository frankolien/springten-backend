const hre = require("hardhat");

async function main() {
  console.log("🚀 Starting SpringTen contracts deployment...\n");

  // Get the contract factories
  const SpringTenToken = await hre.ethers.getContractFactory("SpringTenToken");
  const SpringTenNFT = await hre.ethers.getContractFactory("SpringTenNFT");
  const SpringTenMarketplace = await hre.ethers.getContractFactory("SpringTenMarketplace");

  // Get the deployer account
  const [deployer] = await hre.ethers.getSigners();
  console.log("Deploying contracts with the account:", deployer.address);
  console.log("Account balance:", (await hre.ethers.provider.getBalance(deployer.address)).toString());

  // Deploy SpringTen Token
  console.log("\n📝 Deploying SpringTen Token...");
  const springTenToken = await SpringTenToken.deploy();
  await springTenToken.waitForDeployment();
  const tokenAddress = await springTenToken.getAddress();
  console.log("✅ SpringTen Token deployed to:", tokenAddress);

  // Deploy SpringTen NFT
  console.log("\n🎨 Deploying SpringTen NFT...");
  const springTenNFT = await SpringTenNFT.deploy(
    "SpringTen Collection", // Collection name
    "SPRINGNFT",           // Collection symbol
    10000,                 // Max supply
    hre.ethers.parseEther("0.01") // Mint price (0.01 ETH)
  );
  await springTenNFT.waitForDeployment();
  const nftAddress = await springTenNFT.getAddress();
  console.log("✅ SpringTen NFT deployed to:", nftAddress);

  // Deploy SpringTen Marketplace
  console.log("\n🏪 Deploying SpringTen Marketplace...");
  const springTenMarketplace = await SpringTenMarketplace.deploy();
  await springTenMarketplace.waitForDeployment();
  const marketplaceAddress = await springTenMarketplace.getAddress();
  console.log("✅ SpringTen Marketplace deployed to:", marketplaceAddress);

  // Deployment summary
  console.log("\n🎉 Deployment Summary:");
  console.log("=====================================");
  console.log("SpringTen Token:", tokenAddress);
  console.log("SpringTen NFT:", nftAddress);
  console.log("SpringTen Marketplace:", marketplaceAddress);
  console.log("=====================================");

  // Save deployment info
  const deploymentInfo = {
    network: hre.network.name,
    chainId: hre.network.config.chainId,
    deployer: deployer.address,
    contracts: {
      SpringTenToken: tokenAddress,
      SpringTenNFT: nftAddress,
      SpringTenMarketplace: marketplaceAddress,
    },
    timestamp: new Date().toISOString(),
  };

  const fs = require("fs");
  const path = require("path");
  const deploymentPath = path.join(__dirname, "..", "deployments", `${hre.network.name}.json`);
  
  // Create deployments directory if it doesn't exist
  const deploymentsDir = path.dirname(deploymentPath);
  if (!fs.existsSync(deploymentsDir)) {
    fs.mkdirSync(deploymentsDir, { recursive: true });
  }
  
  fs.writeFileSync(deploymentPath, JSON.stringify(deploymentInfo, null, 2));
  console.log(`\n📄 Deployment info saved to: ${deploymentPath}`);

  // Verify contracts on Etherscan (if not localhost)
  if (hre.network.name !== "localhost" && hre.network.name !== "hardhat") {
    console.log("\n🔍 Verifying contracts on Etherscan...");
    
    try {
      await hre.run("verify:verify", {
        address: tokenAddress,
        constructorArguments: [],
      });
      console.log("✅ SpringTen Token verified");
    } catch (error) {
      console.log("❌ SpringTen Token verification failed:", error.message);
    }

    try {
      await hre.run("verify:verify", {
        address: nftAddress,
        constructorArguments: [
          "SpringTen Collection",
          "SPRINGNFT",
          10000,
          hre.ethers.parseEther("0.01")
        ],
      });
      console.log("✅ SpringTen NFT verified");
    } catch (error) {
      console.log("❌ SpringTen NFT verification failed:", error.message);
    }

    try {
      await hre.run("verify:verify", {
        address: marketplaceAddress,
        constructorArguments: [],
      });
      console.log("✅ SpringTen Marketplace verified");
    } catch (error) {
      console.log("❌ SpringTen Marketplace verification failed:", error.message);
    }
  }

  console.log("\n🎊 Deployment completed successfully!");
}

main()
  .then(() => process.exit(0))
  .catch((error) => {
    console.error(error);
    process.exit(1);
  });
