package com.recordshop.dto;


import com.recordshop.constant.OrderStatus;
import com.recordshop.entity.Item;
import com.recordshop.entity.Order;
import com.recordshop.entity.OrderItem;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class OrderHistDto {

    private Long orderId;       //주문 아이디

    private List<OrderItem> orderItems;     //주문 상품아이디*/

    private String orderDate;   //주문 날짜

    private OrderStatus orderStatus;    //주문 상태

    private String memberName;



    // order 객체를 파라미터로 받아서 멤버 변수 값 세팅
    public OrderHistDto(Order order) {
        this.orderId = order.getId();
        this.orderItems = order.getOrderItems();
        this.orderDate = order.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        this.orderStatus = order.getOrderStatus();
        this.memberName = order.getMember().getName();
    }

    //주문 상품 리스트(orderItemDto 객체를 주문 상품 리스트에 추가하는 메소드)
    private List<OrderItemDto> orderItemDtoList = new ArrayList<>();

    public void addOrderItemDto(OrderItemDto orderItemDto) {

        orderItemDtoList.add(orderItemDto);
    }
}