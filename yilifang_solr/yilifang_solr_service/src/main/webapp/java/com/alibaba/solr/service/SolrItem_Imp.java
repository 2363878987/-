package com.alibaba.solr.service;

import com.alibaba.commons.bean.E3Result;
import com.alibaba.commons.bean.SolrItemBean;
import com.alibaba.commons.bean.SorlBean;
import com.alibaba.solr.interfaces.SolrItemInterfaces;
import com.alibaba.solr.mapper.SolrItem;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class SolrItem_Imp implements SolrItemInterfaces {
    @Autowired
    private SolrItem solrItem;
    //添加数据库信息到索引库
    @Override
    public E3Result addData2Solr() throws IOException, SolrServerException {
        List<SolrItemBean> list = solrItem.getSolrItemList();
        //连接solr
        SolrServer solrServer = new HttpSolrServer("http://192.168.25.129:8080/solr");

        for (SolrItemBean solrItemBean:list
             ) {
            //添加索引
            SolrInputDocument document = new SolrInputDocument();
            document.addField("id",solrItemBean.getId());
            document.addField("item_category_name",solrItemBean.getItem_category_name());
            document.addField("item_image",solrItemBean.getItem_image());
            document.addField("item_price",solrItemBean.getItem_price());
            document.addField("item_sell_point",solrItemBean.getItem_sell_point());
            document.addField("item_title",solrItemBean.getItem_title());
            solrServer.add(document);
        }
        solrServer.commit();
        return E3Result.ok();
    }

    @Override
    public SorlBean selectSolr(int page, int rows, String keyword) throws SolrServerException {
        //声明要返回的SolrBean对象
        SorlBean sorlBean = new SorlBean();
        //声明一个List存储从索引库获取的SolrItemBean
        List<SolrItemBean> solrItemBeanList = new ArrayList<SolrItemBean>();
        //获取solr连接
        HttpSolrServer solrServer = new HttpSolrServer("http://192.168.25.129:8080/solr");
        //获取solr查询对象solrquery
        SolrQuery solrQuery = new SolrQuery();
        //设置solr查询字段
        solrQuery.set("q","item_keywords:"+keyword);
/*        //设置副查询字段
        solrQuery.set("fq","id:1220059");*/


//计算起始索引值 page只是代表了起始页 他并不等于起始索引
        int start =(page-1)*rows;
        solrQuery.set("start",start);
        solrQuery.set("rows",rows);
        QueryResponse response = solrServer.query(solrQuery);
        //返回DocumentList对象
        SolrDocumentList documentList = response.getResults();
        //遍历Document对象得到solr服务器中的数据
        for (SolrDocument document:documentList
                ) {
            SolrItemBean solrItemBean = new SolrItemBean();
            solrItemBean.setId((String) document.get("id"));
            solrItemBean.setItem_category_name((String) document.get("item_category_name"));
            solrItemBean.setItem_image((String) document.get("item_image"));
            solrItemBean.setItem_price((Long) document.get("item_price"));
            solrItemBean.setItem_sell_point((String) document.get("item_sell_point"));
            solrItemBean.setItem_title((String) document.get("item_title"));
            solrItemBeanList.add(solrItemBean);
        }
        sorlBean.setItemList(solrItemBeanList);
        sorlBean.setPage(page);
        int recourdCount = (int) documentList.getNumFound();
        sorlBean.setRecourdCount(recourdCount);
        int totalPages = recourdCount/rows;
        if (totalPages%rows!=0){
            totalPages++;
        }
        sorlBean.setTotalPages(totalPages);
        return sorlBean;
    }

}
