package com.alibaba.content.service;

import com.alibaba.bean.TbContent;
import com.alibaba.bean.TbContentExample;
import com.alibaba.commons.bean.E3Result;
import com.alibaba.commons.jedis.JedisClient;
import com.alibaba.commons.pagehelperbean.PageHelperCommonPojo;
import com.alibaba.commons.utils.JsonUtils;
import com.alibaba.content.interfaces.TbContentInterfaces;
import com.alibaba.mapper.TbContentMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TbContent_Imp implements TbContentInterfaces {
    @Autowired
    private TbContentMapper contentMapper;
    //添加redis缓存
    @Autowired
    private JedisClient jedisClient;


//    商品类目管理查询表分页
    @Override
    public PageHelperCommonPojo queryContentList(int page, int rows) {
        TbContentExample contentExample = new TbContentExample();
        PageHelper.startPage(page,rows);
        List<TbContent> tbContentList = contentMapper.selectByExample(contentExample);
        PageInfo pageInfo = new PageInfo(tbContentList);
        PageHelperCommonPojo pageHelperCommonPojo = new PageHelperCommonPojo();
        pageHelperCommonPojo.setRows(tbContentList);
        pageHelperCommonPojo.setTotal(pageInfo.getTotal());
        return pageHelperCommonPojo;
    }
    //    添加商品类目
    @Override
    public E3Result addContent(TbContent content) {
        jedisClient.del("content");
contentMapper.insert(content);
/*        //设置内容类目create
        content.setCreated(new Date());
        //设置内容类目update
        content.setUpdated(new Date());*/

        return E3Result.ok();
    }
    //    商品类目管理数据查询（首页图片轮播）
    @Override
    public List<TbContent> queryContentListToHome() {
        //查询缓存
        try {
            String content = jedisClient.get("content");
            if (StringUtils.isNotBlank(content)){
                return JsonUtils.jsonToList(content,TbContent.class);
            }
        }catch (RuntimeException e){
            e.printStackTrace();
        }
        TbContentExample tbContentExample = new TbContentExample();
        List<TbContent> contentList = contentMapper.selectByExample(tbContentExample);
        //没有缓存设置缓存
        jedisClient.set("content",JsonUtils.objectToJson(contentList));
        return contentList;
    }
}
