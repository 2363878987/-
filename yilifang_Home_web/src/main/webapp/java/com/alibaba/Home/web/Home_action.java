package com.alibaba.Home.web;

import com.alibaba.bean.TbContent;
import com.alibaba.bean.TbItem;
import com.alibaba.bean.TbItemDesc;
import com.alibaba.commons.bean.SorlBean;
import com.alibaba.content.interfaces.TbContentInterfaces;
import com.alibaba.interfaces.TbItemDescService;
import com.alibaba.interfaces.TbItemService;
import com.alibaba.mapper.ItemMapper;
import com.alibaba.mapper.TbItemDescMapper;
import com.alibaba.solr.interfaces.SolrItemInterfaces;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class Home_action {
    @Autowired
    private TbContentInterfaces contentInterfaces;
    @Autowired
    private SolrItemInterfaces solrItemInterfaces;
    @Autowired
    private TbItemService tbItemService;
    @Autowired
    private TbItemDescService tbItemDescService;
    //加载商城首页
    @RequestMapping("/")
    public String goIndex(Model model){
        List<TbContent> tbContents = contentInterfaces.queryContentListToHome();
        model.addAttribute("ad1List",tbContents);
        return "index";
    }
    //实现商城搜索功能
    @RequestMapping("/search")
    public String selectSolr(@RequestParam(defaultValue = "1") int page,String keyword,Model model) throws SolrServerException {
        int row = 10;
        SorlBean sorlBean = solrItemInterfaces.selectSolr(page, row, keyword);
        model.addAttribute("itemList",sorlBean.getItemList());
        model.addAttribute("page",sorlBean.getPage());
        model.addAttribute("recourdCount",sorlBean.getRecourdCount());
        model.addAttribute("totalPages",sorlBean.getTotalPages());
        model.addAttribute("query",keyword);
        return "search";
    }
    //加载商品展示功能页面
    @RequestMapping("/item/{itemId}")
    public String goItem(@PathVariable long itemId,Model model){
        TbItem tbItem = tbItemService.getItemById(itemId);
        TbItemDesc tbItemDesc = tbItemDescService.getTbItemDescById(itemId);
        model.addAttribute("item",tbItem);
        model.addAttribute("itemDesc",tbItemDesc);
        return "item";
    }
}
