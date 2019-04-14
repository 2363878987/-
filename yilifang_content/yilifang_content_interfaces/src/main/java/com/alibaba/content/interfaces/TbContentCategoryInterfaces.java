package com.alibaba.content.interfaces;

import com.alibaba.commons.bean.E3Result;
import com.alibaba.commons.pagehelperbean.TreeNodeCommonPojo;

import java.util.List;
//商品类目管理
public interface TbContentCategoryInterfaces {
    //树形商品类目数据展示
    public List<TreeNodeCommonPojo> showTbContentCategory(Long parentId);
    //数据节点添加
    E3Result addContentCategory(long parentId,String name);
    //数据节点名称修改
    void updateContentCategory(long id,String name);
}
