package com.kau.minseop.pointshare.model;

import org.web3j.abi.datatypes.Int;

/**
 * Created by khanj on 2018-06-09.
 */

public class CardListModel {
    private String cardType;
    private String cardNum;
    private String cardValidityPeriod;
    private String carCVCNum;
    private String cardPassward;
    private String point="100000";

    public CardListModel(String cardType, String cardNum, String cardValidityPeriod, String carCVCNum, String cardPassward){
        this.cardType = cardType ;
        this.cardNum = cardNum;
        this.cardValidityPeriod = cardValidityPeriod;
        this.carCVCNum = carCVCNum;
        this.cardPassward = cardPassward;
    }

    public String getCardType() {
        return cardType;
    }
    public String getCardNum(){
        return cardNum;
    }
    public String getCardValidityPeriod(){
        return cardValidityPeriod;
    }
    public String getCarCVCNum(){
        return carCVCNum;
    }
    public String getCardPassward(){
        return cardPassward;
    }
    public String getPoint(){return point;}
}