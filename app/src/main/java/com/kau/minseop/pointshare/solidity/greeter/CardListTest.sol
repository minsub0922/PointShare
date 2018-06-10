pragma solidity ^0.4.19;

contract CardListTest {

    event AddCardList(uint id,address owner, string qrcode);
    struct CardList{
        address owner;
        string qrcode;
    }
    CardList[] public cardlists;

    function setCardList(string _qrcode) public{
        uint id = cardlists.push(CardList(msg.sender,_qrcode))-1;
        AddCardList(id,msg.sender,_qrcode);

    }
    function getCardList(uint _id) public constant returns(address,string){
        return (cardlists[_id].owner, cardlists[_id].qrcode);
    }
}