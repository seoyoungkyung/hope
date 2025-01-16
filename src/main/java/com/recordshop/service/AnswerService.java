package com.recordshop.service;

import com.recordshop.constant.AnswerStatus;
import com.recordshop.entity.Answer;
import com.recordshop.entity.Inquiry;
import com.recordshop.entity.Member;
import com.recordshop.repository.AnswerRepository;
import com.recordshop.repository.InquiryRepository;
import com.recordshop.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final InquiryRepository inquiryRepository;
    private final MemberRepository memberRepository;
    private final InquiryService inquiryService;

    //답글 저장
    public void saveAnswer(Long inquiryId, String answer) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("문의글을 찾을 수 없습니다."));

        if (inquiry.getAnswer() != null){
            throw new IllegalStateException("이미 답변이 등록된 문의글 입니다.");
        }

        Answer aw = new Answer();

        aw.setInquiry(inquiry);
        aw.setAnswer(answer);

        answerRepository.save(aw);

        inquiry.setAnswerStatus(AnswerStatus.COMPLETED);
        inquiryRepository.save(inquiry);
    }

    public void modifyAnswer(Long answerId, String newAnswer) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new EntityNotFoundException("해당 답변을 찾을 수 없습니다."));

        answer.setAnswer(newAnswer);
        answerRepository.save(answer);
    }

    public Answer findById(Long answerId) {
        return answerRepository.findById(answerId)
                .orElseThrow(() -> new EntityNotFoundException("답변을 찾을 수 없습니다."));
    }

    @Transactional
    public void deleteAnswer(Long answerId) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new EntityNotFoundException("해당 답글을 찾을 수 없습니다."));

        Inquiry inquiry = answer.getInquiry();

        answerRepository.delete(answer);

        answerRepository.flush();

        inquiry.setAnswerStatus(AnswerStatus.WAITING);

        inquiryRepository.save(inquiry);
    }

    //특정 한 게시물에 대한 답변 조회
    public List<Answer> getAnswersInquiryId(Long inquiryId) {
        return answerRepository.findByInquiryId(inquiryId);
    }


}
