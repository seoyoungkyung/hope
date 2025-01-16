package com.recordshop.repository;

import com.recordshop.constant.Category;
import com.recordshop.constant.ItemSellStatus;
import com.recordshop.entity.Item;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Test
    @DisplayName("상품 저장 테스트")
    public void createItemTest(){
        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(1000);
        item.setItemDetail("테스트 상품 상세 설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setCategory(Category.ALBUM);
        Item savedItem = itemRepository.save(item);
        System.out.println(savedItem.toString());
    }

    @Test
    @DisplayName("상품명 조회 테스트")
    public void findItemNmTest(){
        this.createItemTest();
        List<Item> itemList = itemRepository.findByItemNm("테스트 상품");
        for(Item item : itemList){
            System.out.println(item.toString());
        }
    }
}