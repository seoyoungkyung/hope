package com.recordshop.repository;

import com.recordshop.constant.Category;
import com.recordshop.dto.ItemSearchDto;
import com.recordshop.dto.MainItemDto;
import com.recordshop.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ItemRepositoryCustom {

    Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable);

    Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable);

//    List<MainItemDto> findItemsByCategory(Category category);

}
