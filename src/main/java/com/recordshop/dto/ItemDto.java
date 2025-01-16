package com.recordshop.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ItemDto {

    private Long id;

    private String itemNm;

    private Integer price;

    private String itemDetail;

    private String itemText;

    private String sellStatCd;

    private String category;


}
