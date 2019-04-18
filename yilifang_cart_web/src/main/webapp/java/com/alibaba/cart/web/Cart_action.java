package com.alibaba.cart.web;

import com.alibaba.bean.TbItem;
import com.alibaba.commons.utils.CookieUtils;
import com.alibaba.commons.utils.JsonUtils;
import com.alibaba.interfaces.TbItemService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Controller
public class Cart_action {
    @Autowired
    private TbItemService itemService;
    //添加购物车功能（所有针对item的操作只是购物车而已，暂时并不会对数据库中的item表进行任何处理）
    @RequestMapping("/cart/add/{itemId}")
    public String cartAdd(@PathVariable Long itemId,@RequestParam(defaultValue="1")int num,HttpServletRequest request,HttpServletResponse response){
        //从cookie中取购物车列表
        List<TbItem> list = getCartListFromCookie(request);
        //遍历购物车列表，看购物车列表中是否有相同商品，如果有数量相加
        boolean flag = false;
        for(TbItem item : list){
            if(item.getId()==itemId.longValue()){
                item.setNum(item.getNum()+num);
                flag=true;
                break;
            }
        }
        //如果没有相同的商品，则新增商品
        if(!flag){//falg=false
            //TbItem item = new TbItem();
            TbItem item=itemService.getItemById(itemId);
            item.setId(itemId);
            item.setNum(num);
            list.add(item);
        }
        CookieUtils.setCookie(request,response,"cart",JsonUtils.objectToJson(list),true);
        return "cartSuccess";
    }

    public List<TbItem> getCartListFromCookie(HttpServletRequest request){
        String json = CookieUtils.getCookieValue(request, "cart",true);
        //对于从数据库cookie等查询到的结果一般要进行非空验证
        //对于填写的信息要进行有效的验证
        //这是为了防止出现空指针，或非法数据类型等异常
        if(StringUtils.isNoneBlank(json)){
            return JsonUtils.jsonToList(json, TbItem.class);
        }
        return new ArrayList<>();
    }
    @RequestMapping("/cart/cart")
    public String showCartList(HttpServletRequest request){
        List<TbItem> list = getCartListFromCookie(request);
        for(TbItem tbItem : list){
            Long price = tbItem.getPrice();
            System.out.println(price);
        }
        request.setAttribute("cartList", list);
        return "cart";
    }
    @RequestMapping("/cart/delete/{cartId}")
    public String deleteCartId(@PathVariable Long cartId,HttpServletRequest request,HttpServletResponse response){
        List<TbItem> list = getCartListFromCookie(request);
        Iterator<TbItem> iterator = list.iterator();
        while (iterator.hasNext()) {
            TbItem tbItem = iterator.next();
            if(tbItem.getId()==cartId.longValue()){
                iterator.remove();
            }
        }
        request.setAttribute("cartList", list);
        CookieUtils.setCookie(request, response, "cart", JsonUtils.objectToJson(list),true);
        return "cart";
    }
    //购物车 商品数量修改同步金额
    @RequestMapping("/cart/update/num/{itemId}/{itemNum}")
    @ResponseBody
    public List<TbItem> updateCartNum(@PathVariable Long itemId,@PathVariable int  itemNum,HttpServletRequest request,HttpServletResponse response){
        List<TbItem> list = getCartListFromCookie(request);
        Iterator<TbItem> iterator = list.iterator();
        while (iterator.hasNext()) {
            TbItem tbItem = iterator.next();
            if(tbItem.getId()==itemId.longValue()){
                tbItem.setNum(itemNum);
            }
        }
        CookieUtils.setCookie(request, response, "cart", JsonUtils.objectToJson(list),true);
        return list;
    }
}
