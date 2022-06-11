pragma solidity ^0.8.0;

contract SmallBank {

    address public owner;

    // Maps customer addresses to their balance
    mapping(address => uint) private balances;

    // Log the event about a deposit being made by an address and its amount
    event AccountDeposit(address indexed accountAddress, uint amount);

    constructor() {
        // Set the owner to the creator of this contract
        owner = msg.sender;
    }

    /// @notice Deposit ether into bank, requires method is "payable"
    function deposit() public payable {
    }

    /// @notice Just reads balance of the account requesting, so "constant"
    /// @return The balance of the customer
    function balance() public view returns (uint) {
        return 0;
    }
}
