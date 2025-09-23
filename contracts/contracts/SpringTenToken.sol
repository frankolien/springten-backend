// SPDX-License-Identifier: MIT
pragma solidity ^0.8.19;

import "@openzeppelin/contracts/token/ERC20/ERC20.sol";
import "@openzeppelin/contracts/token/ERC20/extensions/ERC20Burnable.sol";
import "@openzeppelin/contracts/token/ERC20/extensions/ERC20Pausable.sol";
import "@openzeppelin/contracts/access/Ownable.sol";
import "@openzeppelin/contracts/security/ReentrancyGuard.sol";

contract SpringTenToken is ERC20, ERC20Burnable, ERC20Pausable, Ownable, ReentrancyGuard {
    uint256 public constant MAX_SUPPLY = 1000000000 * 10**18; // 1 billion tokens
    uint256 public constant INITIAL_SUPPLY = 100000000 * 10**18; // 100 million tokens
    
    // Staking rewards
    uint256 public stakingRewardRate = 10; // 10% APY
    uint256 public constant REWARD_PRECISION = 10000; // For percentage calculations
    
    // Staking struct
    struct Stake {
        uint256 amount;
        uint256 timestamp;
        uint256 lastClaimed;
        bool active;
    }
    
    mapping(address => Stake) public stakes;
    mapping(address => uint256) public stakingRewards;
    
    // Events
    event TokensStaked(address indexed user, uint256 amount, uint256 timestamp);
    event TokensUnstaked(address indexed user, uint256 amount, uint256 timestamp);
    event RewardsClaimed(address indexed user, uint256 amount);
    event TokensBurned(address indexed user, uint256 amount);
    
    constructor() ERC20("SpringTen Token", "SPRING") {
        _mint(msg.sender, INITIAL_SUPPLY);
        _transferOwnership(msg.sender);
    }
    
    // Mint new tokens (only owner)
    function mint(address to, uint256 amount) public onlyOwner {
        require(totalSupply() + amount <= MAX_SUPPLY, "Exceeds maximum supply");
        _mint(to, amount);
    }
    
    // Stake tokens
    function stake(uint256 amount) public nonReentrant whenNotPaused {
        require(amount > 0, "Amount must be greater than 0");
        require(balanceOf(msg.sender) >= amount, "Insufficient balance");
        
        // Claim any pending rewards first
        if (stakes[msg.sender].active) {
            claimRewards();
        }
        
        _transfer(msg.sender, address(this), amount);
        
        stakes[msg.sender] = Stake({
            amount: stakes[msg.sender].amount + amount,
            timestamp: block.timestamp,
            lastClaimed: block.timestamp,
            active: true
        });
        
        emit TokensStaked(msg.sender, amount, block.timestamp);
    }
    
    // Unstake tokens
    function unstake(uint256 amount) public nonReentrant {
        require(stakes[msg.sender].active, "No active stake");
        require(amount > 0, "Amount must be greater than 0");
        require(amount <= stakes[msg.sender].amount, "Insufficient staked amount");
        
        // Claim any pending rewards first
        claimRewards();
        
        stakes[msg.sender].amount -= amount;
        
        if (stakes[msg.sender].amount == 0) {
            stakes[msg.sender].active = false;
        }
        
        _transfer(address(this), msg.sender, amount);
        
        emit TokensUnstaked(msg.sender, amount, block.timestamp);
    }
    
    // Claim staking rewards
    function claimRewards() public nonReentrant {
        require(stakes[msg.sender].active, "No active stake");
        
        uint256 pendingRewards = calculatePendingRewards(msg.sender);
        require(pendingRewards > 0, "No rewards to claim");
        
        stakes[msg.sender].lastClaimed = block.timestamp;
        stakingRewards[msg.sender] += pendingRewards;
        
        // Mint new tokens for rewards
        _mint(msg.sender, pendingRewards);
        
        emit RewardsClaimed(msg.sender, pendingRewards);
    }
    
    // Calculate pending rewards for a user
    function calculatePendingRewards(address user) public view returns (uint256) {
        if (!stakes[user].active) return 0;
        
        uint256 timeElapsed = block.timestamp - stakes[user].lastClaimed;
        uint256 annualReward = (stakes[user].amount * stakingRewardRate) / 100;
        uint256 dailyReward = annualReward / 365;
        uint256 secondsInDay = 86400;
        
        return (dailyReward * timeElapsed) / secondsInDay;
    }
    
    // Get user's staking info
    function getStakingInfo(address user) public view returns (
        uint256 stakedAmount,
        uint256 pendingRewards,
        uint256 stakingStartTime,
        bool isActive
    ) {
        Stake memory userStake = stakes[user];
        return (
            userStake.amount,
            calculatePendingRewards(user),
            userStake.timestamp,
            userStake.active
        );
    }
    
    // Update staking reward rate (only owner)
    function updateStakingRate(uint256 newRate) public onlyOwner {
        require(newRate <= 1000, "Rate cannot exceed 10%"); // Max 10%
        stakingRewardRate = newRate;
    }
    
    // Pause token transfers (only owner)
    function pause() public onlyOwner {
        _pause();
    }
    
    // Unpause token transfers (only owner)
    function unpause() public onlyOwner {
        _unpause();
    }
    
    // Override required functions for multiple inheritance
    function _beforeTokenTransfer(
        address from,
        address to,
        uint256 amount
    ) internal override(ERC20, ERC20Pausable) {
        super._beforeTokenTransfer(from, to, amount);
    }
    
    // Burn tokens with additional functionality
    function burn(uint256 amount) public override {
        super.burn(amount);
        emit TokensBurned(msg.sender, amount);
    }
    
    // Emergency functions
    function emergencyWithdraw() public onlyOwner {
        uint256 balance = balanceOf(address(this));
        if (balance > 0) {
            _transfer(address(this), owner(), balance);
        }
    }
}
