package com.alibaba.commons.bean;

import java.io.Serializable;
import java.util.List;

public class SorlBean implements Serializable{
    //当前页
    private int page;
    //每页显示的条数
    private int totalPages;
    //符合查询条件的商品的总条数
    private int recourdCount;
    //SolrItemBean集合
    private List<SolrItemBean> itemList;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getRecourdCount() {
        return recourdCount;
    }

    public void setRecourdCount(int recourdCount) {
        this.recourdCount = recourdCount;
    }

    public List<SolrItemBean> getItemList() {
        return itemList;
    }

    public void setItemList(List<SolrItemBean> itemList) {
        this.itemList = itemList;
    }

    public SorlBean() {
    }
}
