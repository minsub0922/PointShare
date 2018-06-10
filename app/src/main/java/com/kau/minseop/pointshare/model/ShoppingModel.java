package com.kau.minseop.pointshare.model;

/**
 * Created by minseop on 2018-06-10.
 */

public class ShoppingModel {
    private String sellerAddress;
    private String qrCode;
    private CouponModel couponModel;

    public ShoppingModel(String sellerAddress, String qrCode, CouponModel couponModel){
        this.sellerAddress = sellerAddress;
        this.qrCode = qrCode;
        this.couponModel = couponModel;
    }

    public void setSellerAddress(String sellerAddress){this.sellerAddress = sellerAddress;}
    public String getSellerAddress(){return sellerAddress;}

    public void setQrCode(String qrCode){this.qrCode = qrCode;}
    public String getQrCode(){return qrCode;}

    public void setCouponModel(CouponModel couponModel){this.couponModel = couponModel;}
    public CouponModel getCouponModel(){return couponModel;}
}
