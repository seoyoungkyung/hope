package com.recordshop.service;

import com.recordshop.entity.InquiryImg;
import com.recordshop.repository.InquiryImgRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class InquiryImgService {

    @Value("${inquiryImgLocation}")
    private String inquiryImgLocation;

    private final InquiryImgRepository inquiryImgRepository;

    private final FileService fileService;

    public void saveInquiryImg(InquiryImg inquiryImg, MultipartFile inquiryImgFile) throws Exception {
        String inquiryOriImgName = inquiryImgFile.getOriginalFilename();
        String inquiryImgNm = "";
        String inquiryImgUrl = "";

        //파일 업로드
        if(!StringUtils.isEmpty(inquiryOriImgName)) {
            inquiryImgNm = fileService.uploadFile(inquiryImgLocation, inquiryOriImgName,
                    inquiryImgFile.getBytes());
            log.info("inquiryImgNm : " + inquiryImgNm);

            inquiryImgUrl = "/images/inquiryImg/" + inquiryImgNm;
            log.info("inquiryImgUrl : " + inquiryImgUrl);
        }

        //문의 이미지 정보 저장
        inquiryImg.updateInquiryImg(inquiryOriImgName, inquiryImgNm, inquiryImgUrl);
        inquiryImgRepository.save(inquiryImg);
    }


}
