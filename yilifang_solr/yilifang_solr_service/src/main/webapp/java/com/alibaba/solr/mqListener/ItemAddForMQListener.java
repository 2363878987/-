package com.alibaba.solr.mqListener;

import com.alibaba.commons.bean.SolrItemBean;
import com.alibaba.solr.mapper.SolrItem;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.io.IOException;

public class ItemAddForMQListener implements MessageListener {
    //绑定查询id的实现接口
    @Autowired
    private SolrItem solrItem;
    @Autowired
    private SolrServer solrServer;
    @Override
    public void onMessage(Message message) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        TextMessage textMessage = (TextMessage)message;
        try {
            String itemId = textMessage.getText();
            System.out.println(itemId);
            //获取solrItemBean对象
            SolrItemBean solrItemBean = solrItem.getSolrItemBeanByID(Long.parseLong(itemId));
            //添加索引
            SolrInputDocument document = new SolrInputDocument();
            document.addField("id",solrItemBean.getId());
            document.addField("item_category_name",solrItemBean.getItem_category_name());
            document.addField("item_image",solrItemBean.getItem_image());
            document.addField("item_price",solrItemBean.getItem_price());
            document.addField("item_sell_point",solrItemBean.getItem_sell_point());
            document.addField("item_title",solrItemBean.getItem_title());
            solrServer.add(document);
            solrServer.commit();
        } catch (JMSException e) {
            e.printStackTrace();
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
