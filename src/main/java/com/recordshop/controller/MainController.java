package com.recordshop.controller;

import com.recordshop.constant.Category;
import com.recordshop.dto.ItemSearchDto;
import com.recordshop.dto.MainItemDto;
import com.recordshop.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Optional;

@Controller
@Log4j2
@RequiredArgsConstructor
public class MainController {

    private final ItemService itemService;

    @GetMapping(value="/")
    public String main(ItemSearchDto itemSearchDto, Optional<Integer> page, Model model) {

        Pageable pageable = PageRequest.of(page.isPresent()?page.get():0, 6);

        List<MainItemDto> items = itemService.getItems();

        Page<MainItemDto> item = itemService.getMainItemPage(itemSearchDto, pageable);

        log.info("-------->: " + items);

        log.info("-------->: " + item);


        model.addAttribute("items", items);
        model.addAttribute("item", item);

        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("maxPage",5);

        return "main";
    }
}
