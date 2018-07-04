package com.kau.minseop.pointshare.utils;

import com.kau.minseop.pointshare.R;

/**
 * Created by minseop on 2018-07-04.
 */

public class GetImageResource {
    public GetImageResource(){

    }

    public int getCompanyImgRes(String company){
        if (company.contains("스타벅스")){
            return R.drawable.starbucks;
        }else if (company.contains("GS25")){
            return R.drawable.gscoupon;
        }else if (company.contains("greencar")||company.contains("그린카")){
            return R.drawable.greencar;
        }else if (company.contains("socar")||company.contains("SOCAR")){
            return R.drawable.socar;
        }else if (company.contains("모두투어")) {
            return R.drawable.modutour;
        }else if (company.contains("CU")){
            return R.drawable.culogo;
        }
        return 0;
    }

    public int getCouponImgRes(String coupon){
        if (coupon.contains("아메리카노M")){
            return R.drawable.americano_m;
        } else if (coupon.contains("그린티라떼M")|| coupon.contains("그린라떼")){
            return R.drawable.greentealatte;
        } else if (coupon.contains( "체리블라썸M")){
            return R.drawable.cherryblassom;
        }else if (coupon .contains( "아메리카노L")){
            return R.drawable.americano_l;
        }else if (coupon.contains( "쏘카1만원")){
            return R.drawable.socar10000;
        }else if (coupon.contains("GS25모바일상품권(1만원권)")){
            return  R.drawable.gs25price;
        }else if(coupon.contains("하나은행70")){
            return R.drawable.hana;
        }else if(coupon.contains("바나나우유")){
            return R.drawable.bananamilk;
        }else if(coupon.contains("파이크플레이스")){
            return R.drawable.fikeplace;
        }else if(coupon.contains("CU모바일상품권")){
            return R.drawable.cuprice;
        }else if(coupon.contains("(농심)토마토라면")){
            return R.drawable.tomatoramen;
        }
        else if(coupon.contains("(오뚜기)라면볶이")){
            return R.drawable.bokiremen;
        }
        return 0;
    }
}
