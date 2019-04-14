package com.alibaba.interfaces;

import com.alibaba.bean.TbItem;
import com.alibaba.commons.bean.E3Result;
import com.alibaba.commons.pagehelperbean.TreeNodeCommonPojo;

import java.util.List;

public interface TbItemCatService {
    public List<TreeNodeCommonPojo> getItemCatTreeNode(Long id);
    public E3Result itemAdd(TbItem item, String desc);
}
