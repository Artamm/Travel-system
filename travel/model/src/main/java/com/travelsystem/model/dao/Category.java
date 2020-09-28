package com.travelsystem.model.dao;


import lombok.Builder;
import lombok.Getter;
public enum Category {

    CAMP("camp"), PARK("park"),
    FISHING("fishing"), BBQ("bbq"),FESTIVAL("festival"),
    TOUR("tour"),
    SPORTS("sports"), SKIING("skiing"), SWIMMING("swimming"), CYCLING("cycling"), VOLLEYBALL("volleyball"), FOOTBALL("football"), BASKETBALL("basketball"), RUGBY("rugby"),
    MUSEUMS("museums"),CASTLES("castles"),
    OTHERS("others");


    @Getter
    public final String category;

    Category(String roleCode) {
        this.category = roleCode;
    }
}
