package com.alibaba.interfaces;

import com.alibaba.bean.TbItem;
import com.alibaba.commons.pagehelperbean.PageHelperCommonPojo;

public interface TbItemService {
    public TbItem getItemById(long id);
    public PageHelperCommonPojo getItemListToJson(int page, int rows);

}