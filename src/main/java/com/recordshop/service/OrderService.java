package com.recordshop.service;

import com.recordshop.dto.OrderDto;
import com.recordshop.dto.OrderHistDto;
import com.recordshop.dto.OrderItemDto;
import com.recordshop.entity.*;
import com.recordshop.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class OrderService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final ItemImgRepository itemImgRepository;


    //                  상품 번호, 수량   ,  회원ID
    public Long order(OrderDto orderDto , String username) {

        //상품 정보 가져오기
        Item item = itemRepository.findById(orderDto.getItemId())
                .orElseThrow(EntityNotFoundException::new);

        // 회원정보 가져오기
        Member member = memberRepository.findByUsername(username);

        //주문 저장 리스트
        List<OrderItem> orderItemList = new ArrayList<>();

        // 상품명과 갯수로 주문 상품 엔티티 생성
        OrderItem orderItem = OrderItem.createOrderItem(item,orderDto.getCount());

        // 여러 상품 주문
        orderItemList.add(orderItem);

        // 회원 정보와 주문할 상품 리스트 정보를 이용하여 주문 엔티티 생성
        Order order = Order.createOrder(member,orderItemList);

        // 주문엔티티 저장(orders table - > DB 저장)
        orderRepository.save(order);

        return order.getId();
    }       //end order

    @Transactional(readOnly = true)
    public Page<OrderHistDto> getOrderList(String username, Pageable pageable) {

        // 유저의 아이디와 페이징 조건을 이용하여 주문 목록 조회
        List<Order> orders = orderRepository.findOrders(username, pageable);

        //유저의 주문 총 개수 구하기
        Long totalCount = orderRepository.countOrder(username);

        List<OrderHistDto> orderHistDtos = new ArrayList<>();

        for (Order order : orders) {    // 주문 리스트를 순회하면서 구매 이력 페이지에 전달할 DTO 생성

            OrderHistDto orderHistDto = new OrderHistDto(order);
            List<OrderItem> orderItems = order.getOrderItems();

            for (OrderItem orderItem : orderItems) {
                ItemImg itemImg = itemImgRepository.findByItemIdAndRepimgYn(orderItem.getItem().getId(),"Y");   // 주문한 상품의 대표 이미지 조회
                OrderItemDto orderItemDto = new OrderItemDto(orderItem,itemImg.getImgUrl());

                orderHistDto.addOrderItemDto(orderItemDto);
            }
            orderHistDtos.add(orderHistDto);
        }

        //페이지 구현 객체 생성 후 반환
        return new PageImpl<OrderHistDto>(orderHistDtos, pageable, totalCount);

    }   //end getOrderList

    //주문 취소 본인이 맞는지 확인
    @Transactional(readOnly = true)
    public boolean validateOrder(Long orderId, String username) {

        Member curMember = memberRepository.findByUsername(username);
        Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);

        Member savedMember = order.getMember();

        if(!StringUtils.equals(curMember.getUsername(),savedMember.getUsername())){
            return false;
        }

        return true;

    }   //end validateOrder

    public void cancelOrder(Long orderId) {

        Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);
        order.cancelOrder();

    }// end cancelOrder

    public Long orders(List<OrderDto> orderDtoList, String username) {

        Member member = memberRepository.findByUsername(username);

        // 주문 상품 리스트
        List<OrderItem> orderItemList = new ArrayList<>();

        for (OrderDto orderDto : orderDtoList) {
            Item item = itemRepository.findById(orderDto.getItemId()).orElseThrow(EntityNotFoundException::new);

            // 상품 주문
            OrderItem orderItem = OrderItem.createOrderItem(item,orderDto.getCount());
            orderItemList.add(orderItem);
        }

        // 상품 주문 리스트로 상품을 주문
        Order order = Order.createOrder(member,orderItemList);
        orderRepository.save(order);

        return order.getId();

    }   // end orders

     public List<OrderItem> orderList(List<OrderDto> orderDtoList, String username) {

        Member member = memberRepository.findByUsername(username);

        // 주문 상품 리스트
        List<OrderItem> orderItemList = new ArrayList<>();

        for (OrderDto orderDto : orderDtoList) {
            Item item = itemRepository.findById(orderDto.getItemId()).orElseThrow(EntityNotFoundException::new);

            // 상품 주문
            OrderItem orderItem = OrderItem.createOrderItem(item,orderDto.getCount());
            orderItemList.add(orderItem);
        }


        return orderItemList;

    }   // end orders

    @Transactional(readOnly = true)
    public Page<OrderHistDto> getAdminOrderList(Pageable pageable) {



        // 모든 주문 가져오기
        Page<Order> ordersPage = orderRepository.findAllByOrders(pageable);

        //유저들의 주문 총 개수 구하기
        Long totalCount = orderRepository.countByOrders();

        List<OrderHistDto> orderHistDtos = new ArrayList<>();

        for (Order order : ordersPage.getContent()) {    // 주문 리스트를 순회하면서 구매 이력 페이지에 전달할 DTO 생성

            OrderHistDto orderHistDto = new OrderHistDto(order);
            List<OrderItem> orderItems = order.getOrderItems();

            for (OrderItem orderItem : orderItems) {
                ItemImg itemImg = itemImgRepository.findByItemIdAndRepimgYn(orderItem.getItem().getId(),"Y");   // 주문한 상품의 대표 이미지 조회
                OrderItemDto orderItemDto = new OrderItemDto(orderItem,itemImg.getImgUrl());

                orderHistDto.addOrderItemDto(orderItemDto);
            }
            orderHistDtos.add(orderHistDto);
        }

        //페이지 구현 객체 생성 후 반환
        return new PageImpl<OrderHistDto>(orderHistDtos, pageable, totalCount);

    }   //end getOrderList

}
