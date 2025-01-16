package com.recordshop.repository;

import com.recordshop.constant.Category;
import com.recordshop.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> , QuerydslPredicateExecutor<Item> ,ItemRepositoryCustom{

    List<Item> findByItemNm(String itemNm);

    List<Item> findByCategory(Category category);

    Page<Item> findByCategory(Category category, Pageable pageable);

    Page<Item> findByCategoryIn(List<Category> categories, Pageable pageable);

    Page<Item> findAll(Pageable pageable);

    Page<Item> findByItemNmContaining(String searchQuery, Pageable pageable);

    Page<Item> findByItemNmContainingAndCategory(String searchQuery, Category category, Pageable pageable);

}
