package com.recordshop.service;

import com.recordshop.constant.AnswerStatus;
import com.recordshop.dto.InquiryDto;
import com.recordshop.dto.InquiryFormDto;
import com.recordshop.dto.InquiryModifyFormDto;
import com.recordshop.entity.Inquiry;
import com.recordshop.entity.InquiryImg;
import com.recordshop.entity.Member;
import com.recordshop.repository.InquiryImgRepository;
import com.recordshop.repository.InquiryRepository;
import com.recordshop.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final InquiryImgRepository inquiryImgRepository;
    private final InquiryImgService inquiryImgService;
    private final MemberRepository memberRepository;

    // 문의글 작성 서비스
    public Long saveInquiry(Inquiry inquiry, String username) {

        // 회원 정보 가져오기
        Member member = memberRepository.findByUsername(username);

        // 회원 정보와 연결된 문의글 저장
        inquiry.setMember(member); // inquiry 객체에 member 정보 설정

        // 문의글 저장
        inquiryRepository.save(inquiry);

        return inquiry.getId(); // 저장된 문의글 ID 반환
    } // end saveInquiry


    //관리자가 모든 사용자의 문의 내역 보기
    @Transactional
    public Page<Inquiry> getAllInquiry(Pageable pageable) {
        return inquiryRepository.findAll(pageable);
    }

    //사용자가 본인이 적은 문의내역 보기
    @Transactional
    public Page<Inquiry> getUserInquiry(String username, AnswerStatus answerStatus, Pageable pageable) {
        if (answerStatus != null) {
            return inquiryRepository.findByMemberUsernameAndAnswerStatus(username, answerStatus, pageable);
        } else {
            return inquiryRepository.findByMemberUsername(username, pageable);
        }
    }

    //문의내역 클릭 시 상세보기
    @Transactional
    public InquiryDto getInquiryDtl(Long inquiryId) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new EntityNotFoundException("이 문의글은 찾을 수 없습니다."));

        return new InquiryDto(inquiry);
    }

    public List<Inquiry> findByEmail(String username) {
        return inquiryRepository.findByMemberUsername(username);
    }

    public Inquiry findById(Long inquiryId) {
        return inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new EntityNotFoundException("문의글을 찾을 수 없습니다."));
    }

    //문의글 수정하기
    public void inquiryUpdate(Long inquiryId, InquiryModifyFormDto inquiryModifyFormDto) {
        Inquiry updateInquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new EntityNotFoundException("문의글 정보를 찾을 수 없습니다."));

        updateInquiry.modifyInquiry(inquiryModifyFormDto);

        inquiryRepository.save(updateInquiry);
    }

    //문의글 삭제하기
    public void deleteInquiry(Long inquiryId) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new EntityNotFoundException("문의글 정보를 찾을 수 없습니다."));

        inquiryRepository.delete(inquiry);
    }

    public Page<Inquiry> getInquiriesByAnswerStatus(AnswerStatus answerStatus, Pageable pageable) {
        return inquiryRepository.findByAnswerStatus(answerStatus, pageable);
    }
//
//    public Page<Inquiry> findAllInquiries(Pageable pageable) {
//        return inquiryRepository.findAll(pageable);
//    }

}
