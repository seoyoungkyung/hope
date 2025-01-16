package com.recordshop.entity;

import com.recordshop.constant.Category;
import com.recordshop.constant.ItemSellStatus;
import com.recordshop.dto.ItemFormDto;
import com.recordshop.exception.OutOfStockException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "item")
@Setter
@Getter
@ToString
public class Item extends BaseEntity {

    @Id
    @Column(name = "item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;        //상품코드

    @Column(nullable = false)
    private String itemNm;      //상품명

    @Column(name="price",nullable = false)
    private int price;      // 가격

    @Column(nullable = false)
    private int stockNumber;        //재고수량

    @Lob
    @Column(nullable = false)
    private String itemDetail;      //상품 상세 설명

    @Lob
    @Column(nullable = false , columnDefinition = "TEXT")
    private String itemText;                    //제품 전체설명

    @Enumerated(EnumType.STRING)
    private ItemSellStatus itemSellStatus;      //상품 판매 상태

    @Enumerated(EnumType.STRING)
    private Category category;          //카테고리설정

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemImg> itemImgs = new ArrayList<>(); //상품 삭제 시 이미지파일도 삭제에 필요

    public void updateItem(ItemFormDto itemFormDto){
        this.itemNm = itemFormDto.getItemNm();
        this.price = itemFormDto.getPrice();
        this.stockNumber = itemFormDto.getStockNumber();
        this.itemDetail = itemFormDto.getItemDetail();
        this.itemText = itemFormDto.getItemText();
        this.itemSellStatus = itemFormDto.getItemSellStatus();
        this.category = itemFormDto.getCategory();
    }

    public void removeStock(int stockNumber){
        int restStock = this.stockNumber - stockNumber;
        if(restStock < 0){
            throw new OutOfStockException("상품의 재고가 부족합니다.(현재 재고 수량 : "+ this.stockNumber + ")");
        }
        this.stockNumber = restStock;
    }

    public void addStock(int stockNumber){
        this.stockNumber += stockNumber;
    }
}
