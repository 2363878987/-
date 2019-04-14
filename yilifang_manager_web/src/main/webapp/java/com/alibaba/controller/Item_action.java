package com.alibaba.controller;

import com.alibaba.bean.TbItem;
import com.alibaba.commons.pagehelperbean.PageHelperCommonPojo;
import com.alibaba.interfaces.TbItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Hello world!
 *
 */
@Controller
public class Item_action
{
    @Autowired
    private TbItemService tbItemService;
    @RequestMapping("getItem/{id}")
    @ResponseBody
    public TbItem getItem(@PathVariable(value = "id") long id){
        TbItem tbItem = tbItemService.getItemById(id);
        return tbItem;
    }

    @ResponseBody
    @RequestMapping("/item/list")
    public PageHelperCommonPojo getItemList(int page, int rows){
        return tbItemService.getItemListToJson(page, rows);
    }
}
