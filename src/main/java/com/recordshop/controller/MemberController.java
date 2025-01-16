package com.recordshop.controller;

import com.recordshop.dto.CartDetailDto;
import com.recordshop.constant.Role;
import com.recordshop.entity.CartItem;
import com.recordshop.detail.PrincipalDetails;
import com.recordshop.service.CartService;
import com.recordshop.dto.MemberFormDto;
import com.recordshop.dto.MemberModifyFormDto;
import com.recordshop.entity.Member;
import com.recordshop.repository.MemberRepository;
import com.recordshop.service.MemberService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@RequestMapping("/members")
@Controller
@RequiredArgsConstructor
@Log4j2
public class MemberController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final CartService cartService;

    @GetMapping(value="/new")
    public String memberForm(Model model) {
        model.addAttribute("memberFormDto", new MemberFormDto());

        return "member/memberForm";
    }

    @PostMapping(value = "/new")
    public String newMember(@Valid MemberFormDto memberFormDto, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            return "member/memberForm";
        }

        try {
            Member member = Member.createMember(memberFormDto, passwordEncoder);
            memberService.saveMember(member);
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "member/memberForm";
        }
        return "redirect:/";
    }       //end newMember

    /*@PostMapping(value = "/new")
    public String newMember(Member member) {
        String role = member.setRole(Role.USER);
        String username = member.getUsername();
        String rewPassword =member.getPassword();
        String encodedPassword = passwordEncoder.encode(rewPassword);
        member.setPassword(encodedPassword);
        memberRepository.save(member);
        return "redirect:/";
    }       //end newMember*/

    @GetMapping(value = "/login")
    public String loginMember() {

        return "/member/memberLoginForm";
    }



    @GetMapping(value = "/login/error")
    public String loginError(Model model) {
        model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요.");
        return "/member/memberLoginForm";
    }

    @GetMapping(value = "/myPage")
    public String myPage(Model model) {

        return "/member/myPage";
    }

    //회원 정보 수정
    @GetMapping(value = "/modify")
    public String memberModify(Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Member member = memberService.findByUsername(username);
        log.info("username: " + username);
        log.info("member: " + member);


        MemberModifyFormDto memberModifyFormDto = new MemberModifyFormDto();
        log.info("memberModifyFormDto : " + memberModifyFormDto.toString());
        memberModifyFormDto.setNickName(member.getNickName());
        memberModifyFormDto.setPhoneNumber(member.getPhoneNumber());
        memberModifyFormDto.setAddress(member.getAddress());

        model.addAttribute("memberModifyFormDto", memberModifyFormDto);
        return "member/memberModifyForm";
    }

    @PostMapping(value = "/modify")
    @ResponseBody
    public ResponseEntity<Map<String, String>> memberModify(
            @Valid MemberModifyFormDto memberModifyFormDto, BindingResult bindingResult) {

        Map<String, String> response = new HashMap<>();

        // 폼 유효성 검사
        if (bindingResult.hasErrors()) {
            response.put("status", "error");
            response.put("message", "입력한 정보를 확인해 주세요.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            // 현재 회원 정보를 찾고 업데이트 처리
            Member currentMember = memberService.findByUsername(username);
            memberService.memberUpdate(currentMember.getUsername(), memberModifyFormDto);

            // 수정 완료 메시지
            response.put("status", "success");
            response.put("message", "수정이 완료되었습니다.");
        } catch (IllegalStateException e) {
            response.put("status", "error");
            response.put("message", "오류가 발생했습니다: " + e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    // 회원 탈퇴 메서드
    @PostMapping("/delete")
    public String deleteMember(@RequestParam String phoneNumber, HttpServletRequest request, HttpServletResponse response, Model model) {

        log.info(phoneNumber);

        //Member member = memberRepository.findByPhone(phoneNumber);
        Member member = memberService.findByPhoneNumber(phoneNumber);

        try {
            memberService.memberDelete(member, request, response);
            return "redirect:/";  // 탈퇴 후 리다이렉트
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "member/memberModifyForm";
        }
    }

    @GetMapping(value = "/contact")
    public String contact(Model model) {
        return "/member/contact";
    }




    @GetMapping(value = "/payment")
    public String showPaymentForm(@RequestParam(required = false) String selectedCartItems,
                                  Model model, Authentication authentication, Principal principal) {
        // 로그인된 사용자의 이메일을 가져옴
        String currentEmail = authentication.getName();

        // 이메일로 회원 정보를 가져옴
        Member member = memberService.findByEmail(currentEmail);

        // MemberModifyFormDto로 전달
        MemberModifyFormDto memberModifyFormDto = new MemberModifyFormDto();
        memberModifyFormDto.setNickName(member.getNickName());
        memberModifyFormDto.setPhoneNumber(member.getPhoneNumber());
        memberModifyFormDto.setAddress(member.getAddress());

        // 로그인한 사용자의 장바구니 정보 가져오기 (CartService 사용)
        List<CartDetailDto> cartDetailList = cartService.getCartList(principal.getName());

        // 장바구니가 비어있으면 결제 페이지로 이동하지 않고, 경고 메시지 표시
        if (cartDetailList.isEmpty()) {
            model.addAttribute("error", "장바구니에 상품이 없습니다. 상품을 추가해 주세요.");
            return "cart/cartList";  // 장바구니 목록 페이지로 돌아감
        }

        // 선택된 아이템들만 필터링하여 가져오기
        if (selectedCartItems != null && !selectedCartItems.isEmpty()) {
            // 선택된 카트 아이템들의 ID 목록을 ','로 구분하여 리스트로 변환
            List<Long> selectedItemIds = Arrays.stream(selectedCartItems.split(","))
                    .map(Long::parseLong)
                    .collect(Collectors.toList());

            // 선택된 아이템들만 필터링
            cartDetailList = cartDetailList.stream()
                    .filter(cartItem -> selectedItemIds.contains(cartItem.getCartItemId()))
                    .collect(Collectors.toList());
        }

        // 결제할 상품 정보 모델에 추가
        model.addAttribute("cartItems", cartDetailList);

        // 회원의 배송지 정보 모델에 추가
        model.addAttribute("memberModifyFormDto", memberModifyFormDto);

        // selectedCartItems를 모델에 추가하여 폼에서 사용할 수 있도록 함
        model.addAttribute("selectedCartItems", selectedCartItems);

        Member member2 = memberService.findByEmail(principal.getName());
        model.addAttribute("member", member2);

        return "item/itemPayment";  // itemPayment.html을 반환
    }

    @PostMapping(value = "/payment")
    public String handlePayment(@RequestParam(required = false) String selectedCartItems,
                                @ModelAttribute MemberModifyFormDto memberModifyFormDto,
                                Authentication authentication, Model model, HttpSession session) {
        // 현재 로그인된 사용자의 이메일을 가져옵니다.
        String currentEmail = authentication.getName();

        // 이메일로 회원 정보 조회
        Member member = memberService.findByUsername(currentEmail);

        // 비밀번호는 그대로 두고, 나머지 정보만 수정
        memberService.updateAddressOnly(member.getUsername(), memberModifyFormDto);

        // 선택된 아이템을 처리하는 부분
        List<CartDetailDto> selectedItems = new ArrayList<>();

        if (selectedCartItems != null && !selectedCartItems.trim().isEmpty()) {
            try {
                // 선택된 아이템의 ID 목록을 처리하여 해당 아이템만 가져옴
                List<Long> selectedItemIds = Arrays.stream(selectedCartItems.split(","))
                        .map(String::trim)
                        .map(Long::parseLong)
                        .collect(Collectors.toList());

                selectedItems = cartService.getSelectedCartItems(selectedItemIds);

                if (selectedItems != null && !selectedItems.isEmpty()) {
                    // 세션에 선택된 아이템 저장 (배송지 수정 후에도 유지)
                    session.setAttribute("selectedCartItems", selectedItems);
                } else {
                    model.addAttribute("errorMessage", "선택된 장바구니 아이템이 없습니다.");
                }
            } catch (NumberFormatException e) {
                model.addAttribute("errorMessage", "유효하지 않은 장바구니 아이템이 포함되어 있습니다.");
                return "item/itemPayment"; // 에러 페이지로 이동
            }
        } else {
            // 선택된 아이템이 없으면 세션에서 가져옴
            selectedItems = (List<CartDetailDto>) session.getAttribute("selectedCartItems");
            if (selectedItems == null || selectedItems.isEmpty()) {
                model.addAttribute("errorMessage", "선택된 장바구니 아이템이 없습니다.");
                return "item/itemPayment";
            }
        }

        // 선택된 아이템이 있으면 모델에 추가
        if (!selectedItems.isEmpty()) {
            model.addAttribute("cartItems", selectedItems);
        }

        // 배송지 정보가 성공적으로 수정되었음을 모델에 추가
        model.addAttribute("successMessage", "배송지 정보가 성공적으로 수정되었습니다.");

        return "item/itemPayment"; // itemPayment.html로 이동
    }

}
