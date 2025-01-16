package com.recordshop.dto;

import com.recordshop.entity.Item;
import com.recordshop.entity.OrderItem;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrderItemDto {

    private  String itemNm;     //상품명

    private int count;          //주문 수량

    private int orderPrice;     //주문 금액

    private String imgUrl;      //상품 이미지 경로

    private Long itemId;

    //OrderItemDto 클래스의 생성자로 orderItem 객체와 이미지경로를 파라미터로 받아서 멤버 변수 값을 세팅
    public OrderItemDto(OrderItem orderItem, String imgUrl) {
        this.itemId = orderItem.getItem().getId();
        this.itemNm = orderItem.getItem().getItemDetail();
        this.count = orderItem.getCount();
        this.orderPrice = orderItem.getOrderPrice();
        this.imgUrl = imgUrl;
    }
}
