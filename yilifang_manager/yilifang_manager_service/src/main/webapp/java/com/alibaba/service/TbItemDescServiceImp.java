package com.alibaba.service;

import com.alibaba.bean.TbItem;
import com.alibaba.bean.TbItemDesc;
import com.alibaba.commons.jedis.JedisClient;
import com.alibaba.commons.utils.JsonUtils;
import com.alibaba.interfaces.TbItemDescService;
import com.alibaba.mapper.TbItemDescMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TbItemDescServiceImp implements TbItemDescService {
    @Autowired
    private TbItemDescMapper tbItemDescMapper;
    //添加redis缓存
    @Autowired
    private JedisClient jedisClient;
    @Override
    public TbItemDesc getTbItemDescById(long id) {
        //查询之前使用redis缓存
        try {
            String content = jedisClient.get("itemDescContent:"+id);
            if (StringUtils.isNotBlank(content)){
                //如果有缓存就返回缓存中的数据
                return JsonUtils.jsonToPojo(content,TbItemDesc.class);
            }
        }catch (RuntimeException e){
            e.printStackTrace();
        }
        //如果没有缓存查询一遍数据库并添加缓存
        TbItemDesc tbItemDesc = tbItemDescMapper.selectByPrimaryKey(id);
        jedisClient.set("itemDescContent:"+id,JsonUtils.objectToJson(tbItemDesc));
        System.out.println("我是itemDescContent的数据");
        return tbItemDesc;
    }
}
