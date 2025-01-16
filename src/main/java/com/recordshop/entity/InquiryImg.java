package com.recordshop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "inquiry_img")
@Getter @Setter @ToString
public class InquiryImg extends BaseEntity{

    @Id
    @Column(name = "inquiry_img_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String inquiryImgNm;     //이미지 파일명

    private String inquiryOriImgName;      //원본 이미지 파일명

    private String inquiryImgUrl;          //이미지 조회 경로

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inquiry_id")
    private Inquiry inquiry;

    public void updateInquiryImg(String inquiryOriImgName, String inquiryImgNm, String inquiryImgUrl) {
        this.inquiryOriImgName = inquiryOriImgName;
        this.inquiryImgNm = inquiryImgNm;
        this.inquiryImgUrl = inquiryImgUrl;

    }
}
