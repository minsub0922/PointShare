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
            return R.drawable.gs25;
        }else if (company.contains("greencar")||company.contains("그린카")){
            return R.drawable.greencar;
        }else if (company.contains("socar")||company.contains("SOCAR")){
            return R.drawable.socar;
        }else if (company.contains("모두투어")) {
            return R.drawable.modutour;
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
        }else if (coupon.contains( "2시간 무료 이용권")|| coupon.contains("2시간무료")){
            return R.drawable.twohour_free;
        }else if (coupon.contains( "")){

        }
        return 0;
    }
}
