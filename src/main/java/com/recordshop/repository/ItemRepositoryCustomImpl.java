package com.recordshop.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.recordshop.constant.Category;
import com.recordshop.constant.ItemSellStatus;
import com.recordshop.dto.ItemSearchDto;
import com.recordshop.dto.MainItemDto;
import com.recordshop.dto.QMainItemDto;
import com.recordshop.entity.Item;
import com.recordshop.entity.QItem;
import com.recordshop.entity.QItemImg;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;


import java.time.LocalDateTime;
import java.util.List;

public class ItemRepositoryCustomImpl implements ItemRepositoryCustom {

    private JPAQueryFactory queryFactory;

    public ItemRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }   //end ItemRepositoryCustomImpl

    private BooleanExpression searchSellStatusEq(ItemSellStatus searchSellStatus) {

        return searchSellStatus == null ? null : QItem.item.itemSellStatus.eq(searchSellStatus);
    }   //end searchSellStatusEq

    private BooleanExpression regDtsAfter(String searchDateType){

        LocalDateTime dateTime = LocalDateTime.now();

        if(StringUtils.equals("all", searchDateType) || searchDateType == null){
            return null;
        } else if(StringUtils.equals("1d",searchDateType)){
            dateTime = dateTime.minusDays(1);
        } else if (StringUtils.equals("1w",searchDateType)) {
            dateTime = dateTime.minusWeeks(1);
        } else if (StringUtils.equals("1m",searchDateType)) {
            dateTime = dateTime.minusMonths(1);
        } else if (StringUtils.equals("6m",searchDateType)) {
            dateTime = dateTime.minusMonths(6);
        }

        return QItem.item.regTime.after(dateTime);
    }   //end regDtsAfter

    private BooleanExpression searchByLike(String searchBy, String searchQuery){

        if(StringUtils.equals("itemNm", searchBy)){
            return QItem.item.itemNm.like("%"+searchQuery+"%");
        } else if (StringUtils.equals("createBy",searchBy)) {
            return QItem.item.createdBy.like("%"+searchQuery+"%");
        }

        return null;
    }   //end searchByLike

    @Override
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        QueryResults<Item> results = queryFactory
                .selectFrom(QItem.item)
                .where(regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(),
                                itemSearchDto.getSearchQuery()))
                .orderBy(QItem.item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<Item> content = results.getResults();
        long total = results.getTotal();
        return new PageImpl<>(content, pageable, total);
    }   //end getAdminItemPage


    private BooleanExpression itemNmLike(String searchQuery){

        return StringUtils.isEmpty(searchQuery) ? null : QItem.item.itemNm.like("%"+searchQuery+"%");

    }   //end itemNmLike


    @Override
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {

        QItem item = QItem.item;
        QItemImg itemImg = QItemImg.itemImg;

        List<MainItemDto> content = queryFactory
                .select(
                        new QMainItemDto(
                                item,
                                itemImg
                        )
                )
                .from(itemImg)
                .join(itemImg.item,item)
                .where(itemImg.repimgYn.eq("Y"))
                .where(itemNmLike(itemSearchDto.getSearchQuery()))
                .orderBy(item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        /* select count(*)
            from item_img img
            join item i
            on item_img.item_id = i.item_id
            where img.repimg_yn = 'Y' and
            i.item_nm like '%%'
         */

        Long total = queryFactory
                .select(Wildcard.count)
                .from(itemImg)
                .join(itemImg.item,item)
                .where(itemImg.repimgYn.eq("Y"))
                .where(itemNmLike(itemSearchDto.getSearchQuery()))
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }   //end getMainItemPage

}
