package com.kau.minseop.pointshare.model;

/**
 * Created by khanj on 2018-06-10.
 */

public class CouponModel {
    String cName="1";
    String company="1";
    String price="1";
    String deadline="1";
    String coupongurl = "https://firebasestorage.googleapis.com/v0/b/pointshare-4a908.appspot.com/o/cardcoupon%2Fcjone%2Fcjlogo.png?alt=media&token=3c985504-55fc-459d-9273-7c37337007b6";
    String companyurl =  "https://firebasestorage.googleapis.com/v0/b/pointshare-4a908.appspot.com/o/cardcoupon%2Fcjone%2Fcjlogo.png?alt=media&token=3c985504-55fc-459d-9273-7c37337007b6";
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
    public String getCoupongurl(){return coupongurl;}
    public String getCompanyurl(){return companyurl;}
}
