pragma solidity ^0.4.2;

// Example taken from https://www.ethereum.org/greeter, also used in
// https://github.com/ethereum/go-ethereum/wiki/Contract-Tutorial#your-first-citizen-the-greeter

contract mortal {
    /* Define variable owner of the type address*/
    address owner;

    /* this function is executed at initialization and sets the owner of the contract */
    function mortal() { owner = msg.sender; }

    /* Function to recover the funds on the contract */
    function kill() { if (msg.sender == owner) suicide(owner); }
}

contract coupondeal is mortal {

    struct Coupon {
        string cname;
        string company;
        string qrcode;
        string price;
        string deadline;
    }

    Coupon[] public coupons;

    function createCoupon(string _cname, string _company, string _qrcode, string _price, string _deadline ) public {
        coupons.push(Coupon(_cname, _company, _qrcode, _price, _deadline));
    }
    
    function getCouponList(uint i) constant returns (string, string, string, string, string) {
        return (coupons[i].cname, coupons[i].company, coupons[i].qrcode, coupons[i].price, coupons[i].deadline);
    }
    
    function buy(uint i) constant returns(string) {
        string item = coupons[i].cname ;
        remove(i);
        return "success";
    }
    
    function remove(uint index)  returns(string) {
        if (index >= coupons.length) return;

        for (uint i = index; i<coupons.length-1; i++){
            coupons[i] = coupons[i+1];
        }
        delete coupons[coupons.length-1];
        coupons.length--;
        return "you";
    }
    
}