package com.alibaba.service;

import com.alibaba.bean.TbContent;
import com.alibaba.bean.TbItem;

import com.alibaba.bean.TbItemExample;
import com.alibaba.commons.jedis.JedisClient;
import com.alibaba.commons.pagehelperbean.PageHelperCommonPojo;
import com.alibaba.commons.utils.JsonUtils;
import com.alibaba.interfaces.TbItemService;
import com.alibaba.mapper.ItemMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.List;

@Service
public class TbItemServiceImp implements TbItemService {
    @Autowired
    private ItemMapper tbItemMapper;
    //添加redis缓存
    @Autowired
    private JedisClient jedisClient;
    @Override
    public TbItem getItemById(long id) {
        //查询之前使用redis缓存
        try {
            String content = jedisClient.get("itemContent");
            if (StringUtils.isNotBlank(content)){
                //如果有缓存就返回缓存中的数据
                return JsonUtils.jsonToPojo(content,TbItem.class);
            }
        }catch (RuntimeException e){
            e.printStackTrace();
        }
        //如果没有缓存查询一遍数据库并添加缓存
        TbItem tbItem = tbItemMapper.selectByPrimaryKey(id);
        jedisClient.set("itemContent",JsonUtils.objectToJson(tbItem));
        System.out.println("我是itemContent的数据");
        return tbItem;
    }

    @Override
    public PageHelperCommonPojo getItemListToJson(int page, int rows) {
        TbItemExample tbItemExample = new TbItemExample();
        PageHelper.startPage(page, rows);


        List<TbItem> list = tbItemMapper.selectByExample(tbItemExample);
        PageInfo pageInfo = new PageInfo(list);
        PageHelperCommonPojo itemPageHelper = new PageHelperCommonPojo();
        itemPageHelper.setTotal(pageInfo.getTotal());
        itemPageHelper.setRows(list);
        return itemPageHelper;
    }
}
