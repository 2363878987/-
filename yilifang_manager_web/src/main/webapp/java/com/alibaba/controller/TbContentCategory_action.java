package com.alibaba.controller;

import com.alibaba.commons.bean.E3Result;
import com.alibaba.commons.pagehelperbean.TreeNodeCommonPojo;
import com.alibaba.content.interfaces.TbContentCategoryInterfaces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TbContentCategory_action {
    @Autowired
    private TbContentCategoryInterfaces categoryInterfaces;

//    展示商品分类管理树形结构数据
    @RequestMapping("/content/category/list")
    public List<TreeNodeCommonPojo> showTbContentCategory(@RequestParam(value = "id",defaultValue = "0")long parent){
        return categoryInterfaces.showTbContentCategory(parent);
    }
//   新增节点
    @RequestMapping("/content/category/create")
    public E3Result contentCategoryCreate(long parentId,String name){
        return categoryInterfaces.addContentCategory(parentId,name);
    }
    //   修改节点名称
    @RequestMapping("/content/category/update")
    public void contentCategoryUpdate(long id,String name){
        categoryInterfaces.updateContentCategory(id, name);
    }
}
