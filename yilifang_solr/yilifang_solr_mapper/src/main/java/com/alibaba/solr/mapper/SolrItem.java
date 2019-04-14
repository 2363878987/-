package com.alibaba.solr.mapper;

import com.alibaba.commons.bean.SolrItemBean;

import java.util.List;


public interface SolrItem {
    //查询商品索引库集合
    List<SolrItemBean> getSolrItemList();
    //根据id查询商品对应在索引库的SolrItemBean对象
    SolrItemBean getSolrItemBeanByID(long itemId);
}
