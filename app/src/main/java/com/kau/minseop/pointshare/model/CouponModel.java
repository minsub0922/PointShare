package com.kau.minseop.pointshare.model;

/**
 * Created by khanj on 2018-06-10.
 */

public class CouponModel {
    String cName="1";
    String company="1";
    String price="1";
    String deadline="1";
    public CouponModel(String cName, String company, String price, String deadline){
        this.cName = cName;
        this.company = company;
        this.price = price;
        this.deadline = deadline;
    }
    public String getcName(){
        return cName;
    }
    public String getCompany(){
        return company;
    }
    public String getPrice(){
        return price;
    }
    public String getDeadline(){
        return deadline;
    }
}
