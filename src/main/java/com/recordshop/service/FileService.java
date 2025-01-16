package com.recordshop.service;


import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

@Service
@Log4j2
public class FileService {

    public String uploadFile(String uploadPath, String originalFileName, byte[] fileData) throws Exception {

        UUID uuid = UUID.randomUUID();

        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        log.info("extension --->"+extension);

        String savedFileName = uuid.toString() + extension;
        log.info("savedFileName --->"+savedFileName);

        String fileUploadFullUrl = uploadPath + "/" + savedFileName;
        log.info("fileUploadFullUrl --->"+fileUploadFullUrl);

        FileOutputStream fos = new FileOutputStream(fileUploadFullUrl);

        log.info("fos --->"+fos);
        fos.write(fileData);
        fos.close();
        return savedFileName;
    }

    public void deleteFile(String filePath) throws Exception {

        File file = new File(filePath);

        if(file.exists()){
           boolean delete = file.delete();
           if (!delete) {
               throw new RuntimeException("삭제에 실패 하였습니다." + filePath);
           }
        }
    } //end deleteFile
}
