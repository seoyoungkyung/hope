package com.recordshop.repository;

import com.recordshop.entity.Answer;
import org.aspectj.weaver.patterns.TypePatternQuestions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    List<Answer> findByInquiryId(Long inquiryId);
}
