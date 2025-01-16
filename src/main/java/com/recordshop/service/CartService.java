package com.recordshop.service;

import com.recordshop.dto.CartDetailDto;
import com.recordshop.dto.CartItemDto;
import com.recordshop.dto.CartOrderDto;
import com.recordshop.dto.OrderDto;
import com.recordshop.entity.Cart;
import com.recordshop.entity.CartItem;
import com.recordshop.entity.Item;
import com.recordshop.entity.Member;
import com.recordshop.repository.CartItemRepository;
import com.recordshop.repository.CartRepository;
import com.recordshop.repository.ItemRepository;
import com.recordshop.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class CartService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderService orderService;

    public Long addCart(CartItemDto cartItemDto , String username) {

        //장바구니에 담을 상품 엔티티 조회
        Item item = itemRepository.findById(cartItemDto.getItemId()).orElseThrow(EntityNotFoundException::new);

        //현재 로그인한 회원 엔티티 조회
        Member member = memberRepository.findByUsername(username);
        log.info("member"+member);

        //현재 로그인한 회원의 장바구니 엔티티 조회
        Cart cart = cartRepository.findByMemberId(member.getId());

        //상품을 처음으로 장바구니에 담을 경우 해당 회원의 장바구니 엔티티 생성
        if(cart == null) {
            cart = Cart.createCart(member);
            cartRepository.save(cart);
        }

        //현재 상품이 장바구니에 이미 들어가 있는지 조회
        CartItem savedCartItem = cartItemRepository.findByCartIdAndItemId(cart.getId(), item.getId());

        /*// 장바구니에 이미 있던 상품일 경우 기존 수량에 현재 장바구니에 담을 수량 만큼 더해줌.
        if(savedCartItem != null) {
            savedCartItem.addCount(cartItemDto.getCount());
            return savedCartItem.getId();
        }else{

            //장바구니 엔티티, 상품엔티티, 장바구니에 담을 수량을 이용하여 CartItem 엔티티 생성
            CartItem cartItem = CartItem.createCartItem(cart,item,cartItemDto.getCount());
            //장바구니에 들어갈 상품 저장
            cartItemRepository.save(cartItem);
            return cartItem.getId();
        }*/

        if(savedCartItem != null) {
            savedCartItem.addCount(cartItemDto.getCount());
        }else {
            savedCartItem =CartItem.createCartItem(cart,item,cartItemDto.getCount());
            cartItemRepository.save(savedCartItem);
        }
        return savedCartItem.getId();

    }   // end addCart

    @Transactional(readOnly = true)
    public List<CartDetailDto> getCartList(String username) {

        List<CartDetailDto> cartDetailDtoList = new ArrayList<>();

        Member member = memberRepository.findByUsername(username);
        log.info("member"+member);

        Cart cart = cartRepository.findByMemberId(memberRepository.findByUsername(username).getId());
        if(cart == null) {
            return cartDetailDtoList;
        }

        cartDetailDtoList = cartItemRepository.findCartDetailDtoList(cart.getId());

        return cartDetailDtoList;

    }   //end getCartList

    @Transactional(readOnly = true)
    public boolean validateCartItem(Long cartItemId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();


        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(EntityNotFoundException::new);

        Member savedMember = cartItem.getCart().getMember();

        //본인 만 수정가능
        if(!StringUtils.equals(username, savedMember.getUsername())) {
            return false;
        }

        return true;
    }   // end validateCartItem

    public void updateCartItemCount(Long cartItemId, int count) {

        log.info("--------updateCartItemCount---------");
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(EntityNotFoundException::new);

        cartItem.updateCount(count);
    }   //end updateCartItemCount

    public void deleteCartItem(Long cartItemId) {

        log.info("--------deleteCartItem---------");

        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(EntityNotFoundException::new);

        cartItemRepository.delete(cartItem);

        //단독사용 가능(메모리공간에 정보가 올라가있기때문에 다시 조회하지 않아도 가능)
        // cartItemRepository.deleteById(cartItemId);

    }   //end deleteCartItem

    public Long orderCartItem(List<CartOrderDto> cartOrderDtoList,String username) {

        List<OrderDto> orderDtoList = new ArrayList<>();

        for(CartOrderDto cartOrderDto : cartOrderDtoList) {
            CartItem cartItem = cartItemRepository.findById(cartOrderDto.getCartItemId()).orElseThrow(EntityNotFoundException::new);

            OrderDto orderDto = new OrderDto();
            orderDto.setItemId(cartItem.getItem().getId());
            orderDto.setCount(cartItem.getCount());
            orderDtoList.add(orderDto);
        }

        Long orderId = orderService.orders(orderDtoList,username);

        //장바구니 삭제
        for(CartOrderDto cartOrderDto : cartOrderDtoList) {
            CartItem cartItem = cartItemRepository.findById(cartOrderDto.getCartItemId()).orElseThrow(EntityNotFoundException::new);

            cartItemRepository.delete(cartItem);
        }
        log.info("orderId"+orderId);
        return orderId;

    }   //end orderCartItem

    public List<CartDetailDto> getSelectedCartItems(List<Long> selectedItemIds) {
        // selectedItemIds를 이용해서 카트 아이템들을 찾아 반환
        return cartItemRepository.findCartDetailDtoListByCartItemIds(selectedItemIds);
    }

}
