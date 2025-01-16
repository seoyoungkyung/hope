package com.recordshop.dto;

import com.recordshop.entity.Answer;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter @Setter
public class AnswerDto {

    private Long id;

    private String answer;

    private Date inquiryRegDate;

    public AnswerDto(Answer answer) {
        this.id = answer.getId();
        this.answer = answer.getAnswer();
    }
}
