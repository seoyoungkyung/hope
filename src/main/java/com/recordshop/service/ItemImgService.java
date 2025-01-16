package com.recordshop.service;

import com.recordshop.entity.ItemImg;
import com.recordshop.repository.ItemImgRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import java.io.IOException;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class ItemImgService {

    @Value("${itemImgLocation}")
    private String itemImgLocation;

    private final ItemImgRepository itemImgRepository;

    private final FileService fileService;

    public void saveItemImg(ItemImg itemImg, MultipartFile itemImgFile) throws Exception {
        String oriImgName = itemImgFile.getOriginalFilename();
        String imgName = "";
        String imgUrl = "";

        //파일업로드
        if(!StringUtils.isEmpty(oriImgName)){
            imgName = fileService.uploadFile(itemImgLocation,oriImgName,itemImgFile.getBytes());
            log.info("imgName------->"+imgName);

            imgUrl = "/images/itemImg/" + imgName;
            log.info("imgUrl------->"+imgUrl);
        }

        //상품 이미지 정보 저장
        itemImg.updateItemImg(oriImgName,imgName,imgUrl);
        itemImgRepository.save(itemImg);

    }   //end saveItemImg

    public void updateItemImg(Long itemImgId, MultipartFile itemImgFile) throws Exception {

        if (!itemImgFile.isEmpty()) {
            ItemImg savedItemImg = itemImgRepository.findById(itemImgId).orElseThrow(()->new EntityNotFoundException(""));

            //기존 이미지 삭제
            if (!StringUtils.isEmpty(savedItemImg.getImgName())) {
                fileService.deleteFile(itemImgLocation+"/"+savedItemImg.getImgName());
            }

            String oriImgName = itemImgFile.getOriginalFilename();
            String imgName = fileService.uploadFile(itemImgLocation,oriImgName,itemImgFile.getBytes());
            String imgUrl = "/images/itemImg/" + imgName;
            savedItemImg.updateItemImg(oriImgName,imgName,imgUrl);
        }

    }       //end updateItemImg

    //상품 삭제
    public void deleteItemImg(Long itemImgId) throws Exception {
        ItemImg itemImg = itemImgRepository.findById(itemImgId).orElseThrow(() -> new EntityNotFoundException("이미지가 존재하지 않습니다."));

        if (!StringUtils.isEmpty(itemImg.getImgName())) {
            String filePath = itemImgLocation + "/" + itemImg.getImgName();
            try {
                fileService.deleteFile(filePath);
                log.info("이미지 삭제 성공 : " + filePath);
            } catch (Exception e) {
                log.info("이미지 삭제 실패 : " + filePath, e);
            }
        }

        itemImgRepository.delete(itemImg);
//    }//end deleteItemImg

    }

}
