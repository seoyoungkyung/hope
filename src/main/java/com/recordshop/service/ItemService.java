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
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemImgService itemImgService;
    private final ItemImgRepository itemImgRepository;

    public Long saveItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception {

        //상품등록
        Item item = itemFormDto.createItem();
        itemRepository.save(item);

        //이미지 등록
        for(int i =0; i<itemImgFileList.size(); i++){
            ItemImg itemImg = new ItemImg();
            itemImg.setItem(item);
            if(i == 0){
                itemImg.setRepimgYn("Y");
            }else {
                itemImg.setRepimgYn("N");

            }
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

    @Transactional(readOnly = true)
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable){

        return itemRepository.getMainItemPage(itemSearchDto,pageable);
    }

//    public List<Item> getItemsByCategory(Category category){
//        return itemRepository.findByCategory(category);
//    }

    // 카테고리별 상품 조회
    public List<MainItemDto> getItemsByCategory(Category category) {
        List<Item> items = itemRepository.findByCategory(category);

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
}
