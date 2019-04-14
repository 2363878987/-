package com.alibaba.controller;

import com.alibaba.bean.TbContent;
import com.alibaba.commons.bean.E3Result;
import com.alibaba.commons.pagehelperbean.PageHelperCommonPojo;

import com.alibaba.content.interfaces.TbContentInterfaces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class Content_action {
    @Autowired
    private TbContentInterfaces contentImp;
    //商品类目表信息查询
    @RequestMapping("/content/query/list")
    @ResponseBody
    public PageHelperCommonPojo queryContentList(int page,int rows){
        return contentImp.queryContentList(page, rows);
    }
    //商品类目表信息添加
    @RequestMapping("/content/save")
    @ResponseBody
    public E3Result addContent(TbContent content){
        return contentImp.addContent(content);
    }
}
