package com.recordshop.repository;

import com.recordshop.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface OrderRepository extends JpaRepository<Order, Long> {

    /*
    select *
    from orders o
    join member m
    on m.member_id = o.member_id
    where m.email like '%%'
    order by o.order_date desc;
    */

    // 현재 로그인한 사용자의 주문 데이터를 페이징조건에 맞춰서 조회
    @Query("select o from Order o where o.member.username =:username order by o.orderDate desc")
    List<Order> findOrders(@Param("username") String username, Pageable pageable);


    /*
    select count(*)
    from member
    join orders
    on orders.member_id = member.member_id
    where member.email like '%%';
    */

    // 현재 로그인한 회원의 주문 개수가 몇 개인지 조회
    @Query("select count(o) from Order o where o.member.username =:username")
    Long countOrder(@Param("username") String username);


    @Query("SELECT COUNT(o) FROM Order o")
    Long countByOrders();

    @Query("select o from Order o")
    Page<Order> findAllByOrders(Pageable pageable);

}