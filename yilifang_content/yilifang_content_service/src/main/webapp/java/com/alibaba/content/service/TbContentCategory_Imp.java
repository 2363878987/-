package com.alibaba.content.service;

import com.alibaba.bean.TbContentCategory;
import com.alibaba.bean.TbContentCategoryExample;
import com.alibaba.commons.bean.E3Result;
import com.alibaba.commons.pagehelperbean.TreeNodeCommonPojo;
import com.alibaba.content.interfaces.TbContentCategoryInterfaces;
import com.alibaba.mapper.TbContentCategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Service
public class TbContentCategory_Imp implements TbContentCategoryInterfaces{
    @Autowired
    private TbContentCategoryMapper tbContentCategoryMapper;
    @Override
    public List<TreeNodeCommonPojo> showTbContentCategory(Long parentId) {
        TbContentCategoryExample categoryExample = new TbContentCategoryExample();
        TbContentCategoryExample.Criteria criteria = categoryExample.createCriteria();
        criteria.andParentIdEqualTo(parentId);
        List<TbContentCategory> tbContentCategories = tbContentCategoryMapper.selectByExample(categoryExample);

        List<TreeNodeCommonPojo> list = new ArrayList<>();
        for (TbContentCategory tbContentCategory:tbContentCategories
                ) {
            TreeNodeCommonPojo itemCatTreeNode = new TreeNodeCommonPojo();
            itemCatTreeNode.setId(tbContentCategory.getId());
            itemCatTreeNode.setText(tbContentCategory.getName());
            itemCatTreeNode.setState(tbContentCategory.getIsParent()?"closed":"open");
            list.add(itemCatTreeNode);
        }
        return list;
    }
    //数据节点添加
    @Override
    public E3Result addContentCategory(long parentId, String name) {
        TbContentCategory category = new TbContentCategory();
        //设置该节点的父节点
        category.setParentId(parentId);
        //设置节点名称
        category.setName(name);
        //设置节点的状态  状态。可选值:1(正常),2(删除)
        category.setStatus(1);
        //设置排列序号
        category.setSortOrder(1);
        //该类目是否为父类目，1为true，0为false
        category.setIsParent(false);
        category.setCreated(new Date());
        category.setUpdated(new Date());
        //节点添加
        tbContentCategoryMapper.insert(category);
        //节点生成后我们 要判断上一级目录是否是父类目 并将他设置为父类目
        TbContentCategory parent = tbContentCategoryMapper.selectByPrimaryKey(parentId);
        if (!parent.getIsParent()){
            parent.setIsParent(true);
            //这里修改后 数据库中的数据还没有修改 所以我们要将数据同步到数据库 这里使用主键回填
            tbContentCategoryMapper.updateByPrimaryKey(parent);
        }

        return E3Result.ok(category);
    }

    @Override
    public void updateContentCategory(long id, String name) {
        TbContentCategory tbContentCategory = tbContentCategoryMapper.selectByPrimaryKey(id);
        tbContentCategory.setName(name);
        tbContentCategoryMapper.updateByPrimaryKey(tbContentCategory);
    }
}
