package com.recordshop.service;

import com.recordshop.constant.Category;
import com.recordshop.dto.ItemFormDto;
import com.recordshop.dto.ItemImgDto;
import com.recordshop.dto.ItemSearchDto;
import com.recordshop.dto.MainItemDto;
import com.recordshop.entity.Item;
import com.recordshop.entity.ItemImg;
import com.recordshop.repository.ItemImgRepository;
import com.recordshop.repository.ItemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemImgService itemImgService;
    private final ItemImgRepository itemImgRepository;
    private final FileService fileService;

    public Long saveItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception {

        //상품등록
        Item item = itemFormDto.createItem();
        itemRepository.save(item);

        //이미지 등록
        for(int i=0; i<itemImgFileList.size(); i++){

            ItemImg itemImg = new ItemImg();
            itemImg.setItem(item);
            if(i == 0) {
                itemImg.setRepimgYn("Y");
            }else {
                itemImg.setRepimgYn("N");
            }

            //이미지 정보저장
            itemImgService.saveItemImg(itemImg,itemImgFileList.get(i));
        }
        return item.getId();
    }   //end saveItem

    @Transactional(readOnly = true)
    public ItemFormDto getItemDtl(Long itemId){
        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);
        List<ItemImgDto> itemImgDtoList = new ArrayList<>();

        for(ItemImg itemImg : itemImgList){
            ItemImgDto itemImgDto = ItemImgDto.of(itemImg);
            itemImgDtoList.add(itemImgDto);
        }

        Item item = itemRepository.findById(itemId).orElseThrow(()->new EntityNotFoundException("Item Not Found"));

        ItemFormDto itemFormDto = ItemFormDto.of(item);
        itemFormDto.setItemImgDtoList(itemImgDtoList);
        return itemFormDto;

    }   //end getItemDtl

    public Long updateItem(ItemFormDto itemFormDto , List<MultipartFile> itemImgFileList) throws Exception {

        //상품수정
        Item item = itemRepository.findById(itemFormDto.getId()).orElseThrow(()->new EntityNotFoundException("Item Not Found"));
        item.updateItem(itemFormDto);

        List<Long> itemImgIds = itemFormDto.getItemImgIds();

        //이미지 등록
        for(int i=0; i<itemImgFileList.size(); i++){
            itemImgService.updateItemImg(itemImgIds.get(i), itemImgFileList.get(i));
        }

        return item.getId();

    }   //end updateItem

    @Transactional(readOnly = true)
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable){

        return itemRepository.getAdminItemPage(itemSearchDto,pageable);

    }   //end getAdminItemPage

    public List<MainItemDto> getItems()
    {
        List<Item> items = itemRepository.findAll();

        // 아이템들을 MainItemDto로 변환
        List<MainItemDto> mainItemDtos = new ArrayList<>();

        for(Item item : items){

            ItemImg img = itemImgRepository.findByItemIdAndRepimgYn(item.getId(),"Y");

            // Item을 MainItemDto로 변환
            MainItemDto mainItemDto = new MainItemDto(item , img);

            // 변환된 MainItemDto 목록에 추가
            mainItemDtos.add(mainItemDto);
        }

        return mainItemDtos;  // 변환된 DTO 목록 반환

    }

    public Page<MainItemDto> getItemsByCategory(Category category, Pageable pageable) {
        Page<Item> items = itemRepository.findByCategory(category, pageable);

        // 아이템들을 MainItemDto로 변환
        List<MainItemDto> mainItemDtos = new ArrayList<>();
        for (Item item : items.getContent()) {
            ItemImg img = itemImgRepository.findByItemIdAndRepimgYn(item.getId(), "Y");
            MainItemDto mainItemDto = new MainItemDto(item, img);
            mainItemDtos.add(mainItemDto);
        }

        return new PageImpl<>(mainItemDtos, pageable, items.getTotalElements());
    }

    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        String searchQuery = itemSearchDto.getSearchQuery();

        Page<Item> items;

        if (searchQuery != null && !searchQuery.isEmpty()) {
            items = itemRepository.findByItemNmContaining(searchQuery, pageable);
        } else {
            items = itemRepository.findAll(pageable);
        }

        List<MainItemDto> mainItemDtos = new ArrayList<>();
        for (Item item : items.getContent()) {
            ItemImg img = itemImgRepository.findByItemIdAndRepimgYn(item.getId(), "Y");
            MainItemDto mainItemDto = new MainItemDto(item , img);
            mainItemDtos.add(mainItemDto);
        }

        return new PageImpl<>(mainItemDtos, pageable, items.getTotalElements());
    }

    //상품 삭제 로직
    @Transactional
    public void deleteItem(Long itemId){
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new EntityNotFoundException("상품이 존재하지 않습니다."));

        String basePath = "C:/works/Springboot/itemImg";

        for (ItemImg itemImg : item.getItemImgs()){
            String filepath = itemImg.getImgUrl();

            if(filepath == null || filepath.isEmpty()){
                continue;
            }

            String filename = filepath.replace("/images/itemImg", "");
            String absolutePath = basePath + "/" + filename;

            try {
                fileService.deleteFile(absolutePath);
                itemImgRepository.delete(itemImg);
            } catch (Exception e) {
                log.error("이미지 삭제 실패 : " + itemImg.getImgUrl(), e);
            }
        }

        itemRepository.delete(item);
    }//end deleteItem
}
