package com.alibaba.service;

import com.alibaba.bean.TbItem;
import com.alibaba.bean.TbItemCat;
import com.alibaba.bean.TbItemCatExample;
import com.alibaba.bean.TbItemDesc;
import com.alibaba.commons.bean.E3Result;
import com.alibaba.commons.pagehelperbean.TreeNodeCommonPojo;
import com.alibaba.commons.utils.IDUtils;
import com.alibaba.interfaces.TbItemCatService;
import com.alibaba.mapper.ItemMapper;
import com.alibaba.mapper.TbItemCatMapper;
import com.alibaba.mapper.TbItemDescMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.jms.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Service
public class TbItemCatServiceImp implements TbItemCatService {
    @Autowired
    private TbItemCatMapper tbItemCatMapper;
    @Autowired
    private TbItemDescMapper tbItemDescMapper;
    @Autowired
    private ItemMapper tbItemMapper;
    @Autowired
    private JmsTemplate jmsTemplate;
    //Mq 目的地（对应mq.xml中的ActiveMQTopic Destination是他的父类接口）
    @Resource
    private Destination itemAddSendIdbyTopic;
    @Override
    public List<TreeNodeCommonPojo> getItemCatTreeNode(Long id) {

        TbItemCatExample catExample = new TbItemCatExample();
        TbItemCatExample.Criteria criteria = catExample.createCriteria();
        criteria.andParentIdEqualTo(id);
        List<TbItemCat> tbItemCats = (List<TbItemCat>) tbItemCatMapper.selectByExample(catExample);

        List<TreeNodeCommonPojo> list = new ArrayList<>();
        for (TbItemCat tbItemCat:tbItemCats
             ) {
            TreeNodeCommonPojo itemCatTreeNode = new TreeNodeCommonPojo();
            itemCatTreeNode.setId(tbItemCat.getId());
            itemCatTreeNode.setText(tbItemCat.getName());
            itemCatTreeNode.setState(tbItemCat.getIsParent()?"closed":"open");
            list.add(itemCatTreeNode);
        }

        return list;

    }
    /*添加商品  2019.3.28*/
    @Override
    public E3Result itemAdd(final TbItem item, String desc) {
        // TODO Auto-generated method stub
		/*先添加item表数据 注意一下字段是jsp中没有的值 需要我们生成*/
        long itemID = IDUtils.genItemId();
        item.setId(itemID);
        item.setStatus((byte)1);
        item.setCreated(new Date());
        item.setUpdated(new Date());
        tbItemMapper.insert(item);

		/*添加desc表的数据 同样需要生成jsp中没有的字段*/
        TbItemDesc itemDesc = new TbItemDesc();
        itemDesc.setItemDesc(desc);
        itemDesc.setItemId(itemID);
        itemDesc.setCreated(new Date());
        itemDesc.setUpdated(new Date());
        tbItemDescMapper.insert(itemDesc);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //向索引库同步添加数据，提供添加的ID给MQ
        jmsTemplate.send(itemAddSendIdbyTopic, new MessageCreator() {
            public Message createMessage(Session session) {
                try {
                    System.out.println(String.valueOf(item.getId()));
                    return (TextMessage)session.createTextMessage(String.valueOf(item.getId()));
                } catch (JMSException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
        return E3Result.ok();
    }
}
