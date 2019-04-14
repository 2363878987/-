package com.alibaba.solr.interfaces;

import com.alibaba.commons.bean.E3Result;
import com.alibaba.commons.bean.SorlBean;
import org.apache.solr.client.solrj.SolrServerException;

import java.io.IOException;

public interface SolrItemInterfaces {
    //添加数据库信息到索引库
    E3Result addData2Solr() throws IOException, SolrServerException;
    //查询solr索引库
    SorlBean selectSolr(int page,int rows,String keyword) throws SolrServerException;
}
