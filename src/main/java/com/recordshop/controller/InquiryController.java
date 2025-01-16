package com.recordshop.controller;

import com.recordshop.constant.AnswerStatus;
import com.recordshop.dto.AnswerFormDto;
import com.recordshop.dto.InquiryDto;
import com.recordshop.dto.InquiryFormDto;
import com.recordshop.dto.InquiryModifyFormDto;
import com.recordshop.entity.Answer;
import com.recordshop.entity.Inquiry;
import com.recordshop.entity.Member;
import com.recordshop.repository.MemberRepository;
import com.recordshop.service.AnswerService;
import com.recordshop.service.InquiryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@RequestMapping("/inquiries")
@Controller
@RequiredArgsConstructor
@Log4j2
public class InquiryController {

    private final InquiryService inquiryService;
    private final MemberRepository memberRepository;
    private final AnswerService answerService;

    @GetMapping(value = "/admin/list")
    public String adminList(@RequestParam(value = "answerStatus" ,required = false) AnswerStatus answerStatus,
                            Optional<Integer> page, Model model) {

        //한 페이지당 10개의 문의글 보여주기
        Pageable pageable = PageRequest.of(page.isPresent()?page.get():0, 10);

        Page<Inquiry> allInquiries;
        if (answerStatus != null) {
            allInquiries = inquiryService.getInquiriesByAnswerStatus(answerStatus, pageable);
        } else {
            allInquiries = inquiryService.getAllInquiry(pageable);
        }

        model.addAttribute("allInquiries", allInquiries);
        model.addAttribute("maxPage", 5);
        model.addAttribute("answerStatus", answerStatus);
        model.addAttribute("currentPage", page);

        return "inquiry/inquiryAdminList";
    }

    @GetMapping(value = "/list")
    public String list(@RequestParam(value = "answerStatus" ,required = false) AnswerStatus answerStatus,
                       Optional<Integer> page, Model model, Principal principal) {

        String username = principal.getName();
        //한 페이지당 10개의 문의글 보여주기
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 10);

        Page<Inquiry> userInquiries = inquiryService.getUserInquiry(username, answerStatus, pageable);

        model.addAttribute("inquiryList", userInquiries);
        model.addAttribute("maxPage", 5);
        model.addAttribute("answerStatus", answerStatus);
        model.addAttribute("currentPage", page);

        return "inquiry/inquiryList";
    }

    @GetMapping(value = "/new")
    public String inquiryForm(Model model) {

        model.addAttribute("inquiryFormDto", new InquiryFormDto());

        return "inquiry/inquiryForm";
    }

    @PostMapping(value = "/new")
    public String newInquiry(@Valid InquiryFormDto inquiryFormDto, BindingResult bindingResult, Model model, Principal principal) {

        if (bindingResult.hasErrors()) {
            return "inquiry/inquiryForm";
        }

        try {
            String username = principal.getName();

            Inquiry inquiry = Inquiry.createInquiry(inquiryFormDto);
            inquiryService.saveInquiry(inquiry, username);
            log.info("username: " + username);
        } catch (IllegalStateException e) {
            // 오류 메시지 출력
            model.addAttribute("errorMessage", e.getMessage());
            return "inquiry/inquiryForm";
        }

        return "redirect:/inquiries/list";
    }

    //글 상세보기
    @GetMapping(value = "/{inquiryId}")
    public String inquiryDtl(@PathVariable("inquiryId") Long inquiryId, Model model) {
        InquiryDto inquiryFormDto = inquiryService.getInquiryDtl(inquiryId);
        model.addAttribute("inquiry", inquiryFormDto);
        return "inquiry/inquiryDtl";
    }

    //글 수정 하기
    @GetMapping(value = "/modify/{inquiryId}")
    public String inquiryModify(@PathVariable("inquiryId") Long inquiryId, Model model) {

        Inquiry inquiry = inquiryService.findById(inquiryId);

        InquiryFormDto inquiryFormDto = new InquiryFormDto();
        inquiryFormDto.setId(inquiry.getId());  // inquiryId 사용
        inquiryFormDto.setTitle(inquiry.getTitle());
        inquiryFormDto.setContent(inquiry.getContent());

        // 수정 폼을 모델에 담아 전달
        model.addAttribute("inquiryFormDto", inquiryFormDto);

        return "inquiry/inquiryModifyForm"; // 수정 폼 페이지로 이동
    }

    @PostMapping(value = "/modify/{inquiryId}")
    public String inquiryModify(@PathVariable("inquiryId") Long inquiryId,
                                @ModelAttribute InquiryModifyFormDto inquiryModifyFormDto,
                                Authentication authentication) {

        Inquiry inquiry = inquiryService.findById(inquiryId);

        String username = authentication.getName();

        inquiry.setTitle(inquiryModifyFormDto.getTitle());
        inquiry.setContent(inquiryModifyFormDto.getContent());

        inquiryService.saveInquiry(inquiry, username);

        return "redirect:/inquiries/list";
    }

    @DeleteMapping(value = "/modify/{inquiryId}")
    public String deleteInquiry(@PathVariable("inquiryId") Long inquiryId) {
        inquiryService.deleteInquiry(inquiryId);
        return "redirect:/inquiries/list";
    }

    //답글 작성하기
    @GetMapping(value = "/answer/{inquiryId}")
    public String inquiryAnswer(@PathVariable("inquiryId") Long inquiryId, Model model) {
        Inquiry inquiry = inquiryService.findById(inquiryId);
        model.addAttribute("inquiry", inquiry);
        model.addAttribute("answerFormDto", new AnswerFormDto());
        return "inquiry/AnswerForm";
    }

    @PostMapping(value = "/answer/{inquiryId}")
    public String inquiryAnswer(@PathVariable("inquiryId") Long inquiryId,
                                @RequestParam String answer){
        answerService.saveAnswer(inquiryId, answer);
        return "redirect:/inquiries/admin/list";
    } //end new answer

    //답글 수정 및 삭제
    @GetMapping(value = "/answer/modify/{answerId}")
    public String modifyAnswer(@PathVariable("answerId") Long answerId, Model model) {

        Answer answer = answerService.findById(answerId);
        model.addAttribute("answerModify", answer);
        model.addAttribute("inquiry", answer.getInquiry());
        return "inquiry/answerModifyForm";
    }

    @PostMapping(value = "/answer/modify/{answerId}")
    public String modifyAnswer(@PathVariable("answerId") Long answerId,
                               @RequestParam String answer, Model model) {
        answerService.modifyAnswer(answerId, answer);
        return "redirect:/inquiries/admin/list";
    }

    @DeleteMapping(value = "/answer/modify/{answerId}")
    public String deleteAnswer(@PathVariable("answerId") Long answerId) {
        answerService.deleteAnswer(answerId);
        return "redirect:/inquiries/admin/list";
    }
}
