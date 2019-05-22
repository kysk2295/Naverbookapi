package com.kys.lg.naveractivityr;

import android.media.Image;
import android.widget.ImageView;

public class BookVO {
    //VO: value object -얻어올 정보들을 묶어서 객체화 하는 클래스
    //제목, 저자, 가격, 이미지
    private String b_title,b_auth,b_price,b_img;


    public String getB_title() {
        return b_title;
    }

    public void setB_title(String b_title) {
        this.b_title = b_title;
    }

    public String getB_auth() {
        return b_auth;
    }

    public void setB_auth(String b_auth) {
        this.b_auth = b_auth;
    }

    public String getB_price() {
        return b_price;
    }

    public void setB_price(String b_price) {
        this.b_price = b_price;
    }

    public String getB_img() {
        return b_img;
    }

    public void setB_img(String b_img) {
        this.b_img = b_img;
    }
}
