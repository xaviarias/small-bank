pragma solidity ^0.8.0;

contract SmallBank {

    address public owner;

    // Maps customer addresses to their balance
    mapping(address => uint) private balances;

    // Log the event about a deposit being made by the customer
    event AccountDeposit(address indexed customer, uint amount);

    // Log the event about a withdrawal being made the customer
    event AccountWithdrawal(address indexed customer, uint amount);

    constructor() {
        // Set the owner to the creator of this contract
        owner = msg.sender;
    }

    /// @notice Deposit ether into bank, requires method is "payable"
    function deposit() public payable {
        balances[msg.sender] += msg.value;
        emit AccountDeposit(msg.sender, msg.value);
    }

    /// @notice Withdraw ether from bank
    function withdraw(uint withdrawAmount) public {
        // Check enough balance available, otherwise just return balance
        require(withdrawAmount <= balances[msg.sender], "Insufficient account balance");

        balances[msg.sender] -= withdrawAmount;
        payable(msg.sender).transfer(withdrawAmount);

        emit AccountWithdrawal(msg.sender, withdrawAmount);
    }

    /// @notice Just reads balance of the account requesting, so "constant"
    /// @return The balance of the customer
    function balance() public view returns (uint) {
        return balances[msg.sender];
    }
}
