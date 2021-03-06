pragma solidity ^0.8.0;

contract SmallBank {

    address public owner;

    // Maps customer addresses to their balance
    mapping(address => uint) private balances;

    // Log the event about a deposit being made
    event AccountDeposit(address indexed account, uint amount);

    // Log the event about a withdrawal being
    event AccountWithdrawal(address indexed account, uint amount);

    // Log the event about a withdrawal being
    event AccountTransfer(address indexed from, address indexed to, uint amount);

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

        // Check enough balance available, otherwise throw exception
        require(withdrawAmount <= balances[msg.sender], "Insufficient account balance");

        balances[msg.sender] -= withdrawAmount;
        payable(msg.sender).transfer(withdrawAmount);

        emit AccountWithdrawal(msg.sender, withdrawAmount);
    }

    /// @notice Transfer deposits from bank to account
    function transfer(address toAccount, uint transferAmount) public {

        // Check enough balance available, otherwise throw exception
        require(transferAmount <= balances[msg.sender], "Insufficient account balance");

        balances[msg.sender] -= transferAmount;
        payable(toAccount).transfer(transferAmount);

        emit AccountTransfer(msg.sender, toAccount, transferAmount);
    }

    /// @notice Just reads balance of the account requesting, so "constant"
    /// @return The balance of the customer
    function balance() public view returns (uint) {
        return balances[msg.sender];
    }
}
