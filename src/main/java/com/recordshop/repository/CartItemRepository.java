package com.recordshop.repository;

import com.recordshop.dto.CartDetailDto;
import com.recordshop.entity.CartItem;
import com.recordshop.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    CartItem findByCartIdAndItemId(Long cartId, Long itemId);

    @Query("select new com.recordshop.dto.CartDetailDto(ci.id,i.itemDetail,i.price,ci.count,im.imgUrl) " +
            "from CartItem ci, ItemImg im " +
            "join ci.item i " +
            "where ci.cart.id =:cartId " +
            "and im.item.id = ci.item.id " +
            "and im.repimgYn = 'Y' " +
            "order by ci.regTime desc"
    )
    List<CartDetailDto> findCartDetailDtoList(Long cartId);

    @Query("SELECT new com.recordshop.dto.CartDetailDto(ci.id, i.itemNm, i.price, ci.count, im.imgUrl) " +
            "FROM CartItem ci " +
            "JOIN ci.item i " +
            "JOIN ItemImg im ON im.item.id = i.id " +
            "WHERE ci.id IN :cartItemIds " +
            "AND im.repimgYn = 'Y'")
    List<CartDetailDto> findCartDetailDtoListByCartItemIds(@Param("cartItemIds") List<Long> cartItemIds);


}
