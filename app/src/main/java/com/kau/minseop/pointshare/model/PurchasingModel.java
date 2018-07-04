package com.kau.minseop.pointshare.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by hihi5 on 2018-07-04.
 */

public class PurchasingModel extends RealmObject{
    @PrimaryKey
    private int index;

    private String cName;
    private String company;
    private String price;
    private String deadline;

    public String getcName() {
        return cName;
    }

    public void setcName(String cName) {
        this.cName = cName;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }
    public void setIndex(int i){this.index = i;}
    public int getIndex(){return index;}


}
