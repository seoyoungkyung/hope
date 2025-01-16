package com.recordshop.controller;

import com.recordshop.dto.CartDetailDto;
import com.recordshop.dto.CartItemDto;
import com.recordshop.dto.CartOrderDto;
import com.recordshop.service.CartService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping(value = "/cart")
    public @ResponseBody ResponseEntity order(@RequestBody @Valid CartItemDto cartItemDto, BindingResult bindingResult , Principal principal) {

        if(bindingResult.hasErrors()) {
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();

            for (FieldError fieldError : fieldErrors) {
                sb.append(fieldError.getDefaultMessage());
            }

            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
        }

        String username = principal.getName();
        Long cartItemId;

        try{
            cartItemId = cartService.addCart(cartItemDto, username);
        }catch (Exception e){
            return new ResponseEntity<String> (e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);
    }    // end order

    @GetMapping(value = "/cart")
    public String orderHist(Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        System.out.println("username => " + username);

        List<CartDetailDto> cartDetailList = cartService.getCartList(username);

        model.addAttribute("cartItems", cartDetailList);
        return "cart/cartList";
    }   // end orderHist

    @PatchMapping(value = "/cartItem/{cartItemId}")
    public @ResponseBody ResponseEntity updateCartItem(@PathVariable("cartItemId") Long cartItemId, int count,String username) {

        if(count <= 0){
            return new ResponseEntity<String>("최소 1개 이상 담아주세요", HttpStatus.BAD_REQUEST);
        } else if (!cartService.validateCartItem(cartItemId)) {
            return new ResponseEntity<String>("수정 권한이 없습니다.",HttpStatus.FORBIDDEN);
        }

        cartService.updateCartItemCount(cartItemId, count);

        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);
    }   // end updateCartItem

    @DeleteMapping(value = "/cartItem/{cartItemId}")
    public @ResponseBody ResponseEntity deleteCartItem(@PathVariable("cartItemId") Long cartItemId,String username) {

        if (!cartService.validateCartItem(cartItemId)) {
            return new ResponseEntity<String>("수정 권한이 없습니다.",HttpStatus.FORBIDDEN);
        }

        cartService.deleteCartItem(cartItemId);

        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);

    }   // end deleteCartItem

    @PostMapping(value = "/cart/orders")
    public @ResponseBody ResponseEntity orderCartItem(@RequestBody CartOrderDto cartOrderDto, String username) {

        List<CartOrderDto> cartOrderDtoList = cartOrderDto.getCartOrderDtoList();

        if (cartOrderDtoList == null || cartOrderDtoList.size() == 0){
            return new ResponseEntity<String>("주문할 상품을 선택해주세요.",HttpStatus.FORBIDDEN);
        }

        for(CartOrderDto cartOrder : cartOrderDtoList){

            if(!cartService.validateCartItem(cartOrder.getCartItemId())){
                return new ResponseEntity<String>("주문 권한이 없습니다.",HttpStatus.FORBIDDEN);
            }
        }

        Long orderId = cartService.orderCartItem(cartOrderDtoList,username);


        return new ResponseEntity<Long>(orderId,HttpStatus.OK);

    }   // end orderCartItem

}
