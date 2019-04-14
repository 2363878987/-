package com.alibaba.controller;

import com.alibaba.commons.bean.E3Result;
import com.alibaba.solr.interfaces.SolrItemInterfaces;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@Controller
public class SolrItem_action {
    @Autowired
    private SolrItemInterfaces solrItemInterfaces;
    @RequestMapping("/index/item/import")
    @ResponseBody
    public E3Result addData2Solr() throws IOException, SolrServerException {
        return solrItemInterfaces.addData2Solr();
    }
}
