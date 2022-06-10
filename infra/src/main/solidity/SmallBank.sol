pragma solidity ^0.8.0;

contract SmallBank {

    // Maps customer addresses to their balance
    mapping (address => uint) private balances;

    // Log the event about a deposit being made by an address and its amount
    event AccountDeposit(address indexed accountAddress, uint amount);

    /// @notice Deposit ether into bank, requires method is "payable"
    /// @return The balance of the user after the deposit is made
    function deposit() public payable returns (uint) {
        return 0;
    }

    /// @notice Just reads balance of the account requesting, so "constant"
    /// @return The balance of the customer
    function balance() public view returns (uint) {
        return 0;
    }
}
