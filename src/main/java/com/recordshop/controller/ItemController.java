package com.recordshop.controller;

import com.recordshop.constant.Category;
import com.recordshop.dto.CartOrderDto;
import com.recordshop.dto.ItemFormDto;
import com.recordshop.dto.ItemSearchDto;
import com.recordshop.dto.MainItemDto;
import com.recordshop.entity.Delivery;
import com.recordshop.entity.Item;
import com.recordshop.entity.Member;
import com.recordshop.entity.Order;
import com.recordshop.repository.DeliveryRepository;
import com.recordshop.repository.OrderRepository;
import com.recordshop.service.CartService;
import com.recordshop.service.ItemService;
import com.recordshop.service.MemberService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Log4j2
public class ItemController {

    private final ItemService itemService;
    private final CartService cartService;
    private final OrderRepository orderRepository;
    private final MemberService memberService;
    private final DeliveryRepository deliveryRepository;

    @GetMapping(value="/admin/item/new")
    public String newItem(Model model) {
        model.addAttribute("itemFormDto", new ItemFormDto());

        return "/item/itemForm";

    }   //end itemForm

    @PostMapping(value = "/admin/item/new")
    public String itemNew(@Valid ItemFormDto itemFormDto, BindingResult bindingResult , Model model, @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList) {

        if (bindingResult.hasErrors()) {
            return "/item/itemForm";
        }

        if(itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null){
            model.addAttribute("errorMessage", "첫번째 상품이미지는 필수 입력 값 입니다.");
            return "/item/itemForm";
        }

        try{
            itemService.saveItem(itemFormDto, itemImgFileList);
        }catch (Exception e){
            model.addAttribute("errorMessage", "상품등록 중 에러가 발생하였습니다.");
            return "/item/itemForm";
        }

        return "redirect:/";

    }       //end itemNew

    @GetMapping(value = "/admin/item/{itemId}")
    public String itemDtl(@PathVariable("itemId") Long itemId, Model model) {

        try{
            ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
            model.addAttribute("itemFormDto", itemFormDto);
        }catch (EntityNotFoundException e){
            model.addAttribute("errorMessage", "존재하지 않는 상품 입니다.");
            model.addAttribute("itemFormDto", new ItemFormDto());
            return "/item/itemForm";
        }

        return "item/itemForm";

    }       // end itemDtl -> 상품 상세보기

    @PostMapping(value="/admin/item/{itemId}")
    public String itemUpdate(@Valid ItemFormDto itemFormDto, BindingResult bindingResult, @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList, Model model) {

        if (bindingResult.hasErrors()) {
            return "/item/itemForm";
        }

        if(itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null){
            model.addAttribute("errorMessage","첫번째 상품 이미지는 필수 입력 값 입니다.");
            return "/item/itemForm";
        }

        try{
            itemService.updateItem(itemFormDto, itemImgFileList);
        }catch (Exception e){
            model.addAttribute("errorMessage","상품 수정 중 에러가 발생하였습니다.");
            return "/item/itemForm";
        }

        return "redirect:/";
    }  //end itemUpdate

    @GetMapping(value = {"/admin/items","/admin/items/{page}"})
    public String itemManage(ItemSearchDto itemSearchDto, @PathVariable("page") Optional<Integer> page,Model model){

        Pageable pageable = PageRequest.of(page.isPresent() ? page.get(): 0 , 10);

        Page<Item> items = itemService.getAdminItemPage(itemSearchDto, pageable);
        model.addAttribute("items", items);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("maxPage", 5);
        return "item/itemMng";
    }

    @GetMapping(value = "/item/{itemId}")
    public String itemDtl(Model model, @PathVariable("itemId") Long itemId) {
        ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
        System.out.println(itemFormDto);
        model.addAttribute("item", itemFormDto);
        return "item/itemDtl";
    }

    @GetMapping(value = "/item/list")
    public String itemList(ItemSearchDto itemSearchDto, @RequestParam(value = "category", required = false) Category category, Optional<Integer> page, Model model) {


        Pageable pageable = PageRequest.of(page.isPresent()?page.get():0, 8);


        Page<MainItemDto> items;
        if (category != null) {
            items = itemService.getItemsByCategory(category, pageable); // 카테고리별 상품 조회
        } else {
            items = itemService.getMainItemPage(itemSearchDto, pageable); // 전체 상품 조회
        }

        model.addAttribute("items", items);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("category", category); // 현재 선택된 카테고리
        model.addAttribute("maxPage", 5);
        return "item/list";
    }

    //아이템 삭제 컨트롤러
    @DeleteMapping(value = "/admin/item/{itemId}")
    public String deleteItem(@PathVariable("itemId") Long itemId) {
        itemService.deleteItem(itemId);
        return "redirect:/admin/items"; // 삭제 후 목록 페이지로 리다이렉트
    }


    // 상품 수정 페이지로 이동 버튼 (GET 요청)
    @GetMapping(value = "/admin/item/edit/{itemId}")
    public String editItem(@PathVariable("itemId") Long itemId, Model model) {
        try {
            // 수정할 상품 정보 가져오기
            ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
            model.addAttribute("itemFormDto", itemFormDto); // 수정할 상품 정보를 모델에 추가
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", "존재하지 않는 상품입니다.");
            return "redirect:/admin/items"; // 상품이 존재하지 않으면 목록 페이지로 리디렉션
        }
        return "/item/itemForm"; // 상품 수정 폼을 반환
    }

    // 상품 수정 처리 (POST 요청)
    @PostMapping(value = "/admin/item/edit/{itemId}")
    public String updateItem(@Valid ItemFormDto itemFormDto, BindingResult bindingResult, @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList, Model model) {
        if (bindingResult.hasErrors()) {
            return "/item/itemForm";
        }

        if (itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null) {
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값입니다.");
            return "/item/itemForm";
        }

        try {
            itemService.updateItem(itemFormDto, itemImgFileList); // 상품 수정
        } catch (Exception e) {
            model.addAttribute("errorMessage", "상품 수정 중 에러가 발생하였습니다.");
            return "/item/itemForm";
        }

        return "redirect:/admin/items";
    }

    @PostMapping(value = "/item/payment")
    public @ResponseBody ResponseEntity orderItemPayment(@RequestBody CartOrderDto cartOrderDto, Principal principal,
                                                         HttpSession httpSession) {

        // 결제할 상품 정보 검증
        List<CartOrderDto> cartOrderDtoList = cartOrderDto.getCartOrderDtoList();
        log.info("결제 된 cartOrderDtoList : " + cartOrderDtoList);

        // 주문 처리
        Long orderId = cartService.orderCartItem(cartOrderDtoList, principal.getName());

        // 주문 ID로 주문 객체를 가져옵니다.
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));
        log.info("주문 정보: " + order);

        // 배송지 정보 생성 (예시로 주소, 전화번호, 닉네임을 저장)
        Member member = memberService.findByUsername(principal.getName());
        log.info("member: " + member);
        Delivery delivery = new Delivery();
        delivery.setNickName(member.getNickName());
        delivery.setPhoneNumber(member.getPhoneNumber());
        delivery.setAddress(member.getAddress());
        delivery.setOrder(order);  // 주문과 배송 정보를 연결

        // Delivery 객체 저장
        deliveryRepository.save(delivery);
        log.info("배송 정보가 저장되었습니다: " + delivery);

        return new ResponseEntity<Long>(orderId, HttpStatus.OK);
    }

}
