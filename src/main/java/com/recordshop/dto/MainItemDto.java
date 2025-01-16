package com.recordshop.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.recordshop.constant.Category;
import com.recordshop.entity.Item;
import com.recordshop.entity.ItemImg;
import com.recordshop.entity.QItemImg;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter@Getter @ToString
public class MainItemDto {

    private Long id;

    private String itemNm;

    private String itemDetail;

    private String imgUrl;

    private Integer price;

    private Category category;

    @QueryProjection
    public MainItemDto(Item item, ItemImg imgUrl) {
        this.id = item.getId();
        this.itemNm = item.getItemNm();
        this.itemDetail = item.getItemDetail();
        this.imgUrl = imgUrl.getImgUrl();
        this.price = item.getPrice();
        this.category = item.getCategory();
    }


}
