package com.alibaba.controller;

import com.alibaba.bean.TbItem;
import com.alibaba.commons.bean.E3Result;
import com.alibaba.commons.pagehelperbean.TreeNodeCommonPojo;
import com.alibaba.commons.utils.FastDFSClient;
import com.alibaba.interfaces.TbItemCatService;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.jms.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ItemCat_action {
    @Autowired
   private  TbItemCatService itemCatService;


//    树形结构的商品类目

    @RequestMapping("/item/cat/list")
    @ResponseBody
    public List<TreeNodeCommonPojo> getItemCatTreeNodeList(@RequestParam(value = "id",defaultValue = "0")long id){
        return itemCatService.getItemCatTreeNode(id);
    }

//    上传图片功能
@RequestMapping("/pic/upload")
@ResponseBody
public Map getItemCatTreeNodeList(MultipartFile uploadFile){

    try {

        FastDFSClient fastDFSClient = new FastDFSClient("classpath:conf/fastdfs.conf");
        String originalFilename = uploadFile.getOriginalFilename();
        String fileextension = originalFilename.substring(originalFilename.lastIndexOf(".")+1);
        String url = fastDFSClient.uploadFile(uploadFile.getBytes(), fileextension);
        url="http://192.168.25.133/"+url;
        System.out.println(url);
        HashMap hashMap = new HashMap();
        hashMap.put("error",0);
        hashMap.put("url",url);
        return hashMap;
    } catch (Exception e) {
        // TODO: handle exception
        HashMap hashMap = new HashMap();
        hashMap.put("error",1);
        hashMap.put("message","图片上传失败");
        return hashMap;

    }
}
    /*item表添加功能*/
    @RequestMapping("/item/save")
    @ResponseBody
    public E3Result itemAdd(final TbItem item, String desc){


        return itemCatService.itemAdd(item, desc);

    }
}
