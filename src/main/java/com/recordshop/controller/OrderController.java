package com.recordshop.controller;



import com.recordshop.dto.OrderDto;
import com.recordshop.dto.OrderHistDto;
import com.recordshop.service.CartService;
import com.recordshop.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Log4j2
public class OrderController {

    private final OrderService orderService;
    private final CartService cartService;

    @PostMapping(value = "/order")
    public @ResponseBody ResponseEntity order(@RequestBody @Valid OrderDto orderDto, BindingResult bindingResult, Principal principal) {

        if(bindingResult.hasErrors()) {

            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();

            for(FieldError fieldError : fieldErrors) {
                sb.append(fieldError.getDefaultMessage());
            }

            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
        }

        String username = principal.getName();
        Long orderId;

        try {
            orderId = orderService.order(orderDto,username);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Long>(orderId, HttpStatus.OK);

    }       //end order


    //Principal -> 인증된 사용자를 나타내는 객체
    @GetMapping(value = {"/orders", "/orders/{page}"})
    public String orderHist(@PathVariable("page") Optional<Integer> page,Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // 한번에 가지고 올 주문의 개수는 4개로 설정
        Pageable pageable = PageRequest.of( page.isPresent() ? page.get() : 0, 4);

        //로그인한 회원은 이메일과 페이징 객체를 파라미터로 전달하여 화면에 전달한 주문 목록 데이터를 리턴값으로 받음
        Page<OrderHistDto> ordersHistDtoList = orderService.getOrderList(username, pageable);

        log.info("ordersHistDtoList : "+ordersHistDtoList.toString());


        model.addAttribute("orders", ordersHistDtoList);
        model.addAttribute("page", pageable.getPageNumber());
        model.addAttribute("maxPage" , 5);

        return "order/orderHist";

    }   //end orderHist

    @PostMapping("/order/{orderId}/cancel")
    public @ResponseBody ResponseEntity cancelOrder(@PathVariable("orderId") Long orderId) {



        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        if(!orderService.validateOrder(orderId, username)) {
            return new ResponseEntity<String>("주문 취소 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        orderService.cancelOrder(orderId);
        return new ResponseEntity<Long>(orderId, HttpStatus.OK);

    }   //end cancelOrder

    @GetMapping(value={"/admin/orders","/admin/orders/{page}"})
    public String adminOrders(@PathVariable("page") Optional<Integer> page,Model model) {


        // 한번에 가지고 올 주문의 개수는 4개로 설정
        Pageable pageable = PageRequest.of( page.isPresent() ? page.get() : 0, 10);

        Page<OrderHistDto> orders = orderService.getAdminOrderList(pageable);


        model.addAttribute("orders", orders);
        model.addAttribute("page", pageable.getPageNumber());
        model.addAttribute("maxPage" , 5);

        return "order/adminOrders";
    }

}
