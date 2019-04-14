package com.alibaba.content.interfaces;

import com.alibaba.bean.TbContent;
import com.alibaba.commons.bean.E3Result;
import com.alibaba.commons.pagehelperbean.PageHelperCommonPojo;

import java.util.List;

//商品管理
public interface TbContentInterfaces {
    //    商品类目管理查询表分页
    PageHelperCommonPojo queryContentList(int page,int rows);
    //    添加商品类目
    E3Result addContent(TbContent content);
    //    商品类目管理数据查询（首页图片轮播）
    List<TbContent> queryContentListToHome();
}
