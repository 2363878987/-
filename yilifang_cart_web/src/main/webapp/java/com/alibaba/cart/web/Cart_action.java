package com.alibaba.cart.web;

import com.alibaba.bean.TbItem;
import com.alibaba.bean.TbOrderItem;
import com.alibaba.bean.TbUser;
import com.alibaba.cart.interfaces.CartNoManInterface;
import com.alibaba.commons.jedis.JedisClient;
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
    @Autowired
    private CartNoManInterface cartNoManInterface;
    @Autowired
    private JedisClient jedisClient;

    @RequestMapping("/cart/add/{itemId}")
    public String cartAdd(@PathVariable Long itemId,@RequestParam(defaultValue="1")int num,HttpServletRequest request,HttpServletResponse response){
        //这里通过拦截器中设置的request 来判断用户的登陆状态（登陆/未登录）
        TbUser user = (TbUser)request.getAttribute("user");
        //登陆状态：
        if (user!=null){
            //声明一个 标识
            boolean isNotItemFlag = true;


            // 1. 从redis中获取购物车列表
            String cartList = jedisClient.get("Cart_UserId_"+user.getId());
            List<TbItem> redisList = cartNoManInterface.getCartListFromCookie(cartList);
            for(TbItem item : redisList){
                if(item.getId()==itemId.longValue()){
                    item.setNum(item.getNum()+num);
                    isNotItemFlag = false;
                    break;
                }
            }
            // 2. 从cookie中取购物车列表
            //声明一个标识
            boolean flag = true;
            String json = CookieUtils.getCookieValue(request, "cart",true);
            List<TbItem> list = cartNoManInterface.getCartListFromCookie(json);
            for (int i =0;i<list.size();i++){
                CookieUtils.deleteCookie(request,response,"cart");
                for (int j = 0;j<redisList.size()-i-1;j++){
                    if (list.get(i).getId()==redisList.get(j).getId()){
                        flag = false;
                    }
                }
                if (flag){
                    if (list.get(i).getId()==itemId.longValue()){
                        list.get(i).setNum(list.get(i).getNum()+num);
                        isNotItemFlag = false;
                        break;
                    }
                    redisList.add(list.get(i));
                    flag = true;
                }
            }
            //如果没有添加过则需要我们给出这个商品
            if (isNotItemFlag) {
                TbItem item = itemService.getItemById(itemId);
                item.setId(itemId);
                item.setNum(num);
                redisList.add(item);
            }

            //将用户信息和对应的购物车信息添加到redis中
            jedisClient.set("Cart_UserId_"+user.getId(),JsonUtils.objectToJson(redisList));
            return "cartSuccess";

        }
        //未登录状态：添加购物车功能（所有针对item的操作只是购物车而已(Cookie)，并不会对数据库中的item表进行任何处理）
        //从cookie中取购物车列表
        String json = CookieUtils.getCookieValue(request, "cart",true);
        List<TbItem> list = cartNoManInterface.getCartListFromCookie(json);
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

    //购物车结算页面
    @RequestMapping("/cart/cart")
    public String showCartList(HttpServletRequest request){
        //这里通过拦截器中设置的request 来判断用户的登陆状态（登陆/未登录）
        TbUser user = (TbUser)request.getAttribute("user");
        //登陆状态：
        if (user!=null){
            String cartList = jedisClient.get("Cart_UserId_"+user.getId());
            List<TbItem> list = cartNoManInterface.getCartListFromCookie(cartList);
            request.setAttribute("cartList", list);
            return "cart";
        }
        String json = CookieUtils.getCookieValue(request, "cart",true);
        List<TbItem> list = cartNoManInterface.getCartListFromCookie(json);
        for(TbItem tbItem : list){
            Long price = tbItem.getPrice();
            System.out.println(price);
        }
        request.setAttribute("cartList", list);
        return "cart";
    }

    //删除购物车
    @RequestMapping("/cart/delete/{cartId}")
    public String deleteCartId(@PathVariable Long cartId,HttpServletRequest request,HttpServletResponse response){
        //这里通过拦截器中设置的request 来判断用户的登陆状态（登陆/未登录）
        TbUser user = (TbUser)request.getAttribute("user");
        //登陆状态：
        if (user!=null){
            String cartList = jedisClient.get("Cart_UserId_"+user.getId());
            List<TbItem> list = cartNoManInterface.getCartListFromCookie(cartList);
            Iterator<TbItem> iterator = list.iterator();
            while (iterator.hasNext()) {
                TbItem tbItem = iterator.next();
                if(tbItem.getId()==cartId.longValue()){
                    iterator.remove();
                }
            }
            //删除完成同步到redis数据库中
            jedisClient.set("Cart_UserId_"+user.getId(),JsonUtils.objectToJson(list));
            request.setAttribute("cartList", list);
            return "cart";
        }
        String json = CookieUtils.getCookieValue(request, "cart",true);
        List<TbItem> list = cartNoManInterface.getCartListFromCookie(json);
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
        //这里通过拦截器中设置的request 来判断用户的登陆状态（登陆/未登录）
        TbUser user = (TbUser)request.getAttribute("user");
        //登陆状态：
        if (user!=null){
            String cartList = jedisClient.get("Cart_UserId_"+user.getId());
            List<TbItem> list = cartNoManInterface.getCartListFromCookie(cartList);
            Iterator<TbItem> iterator = list.iterator();
            while (iterator.hasNext()) {
                TbItem tbItem = iterator.next();
                if(tbItem.getId()==itemId.longValue()){
                    tbItem.setNum(itemNum);
                    break;
                }
            }
            //修改完成同步到redis数据库中
            jedisClient.set("Cart_UserId_"+user.getId(),JsonUtils.objectToJson(list));
            return list;
        }
        String json = CookieUtils.getCookieValue(request, "cart",true);
        List<TbItem> list = cartNoManInterface.getCartListFromCookie(json);
        Iterator<TbItem> iterator = list.iterator();
        while (iterator.hasNext()) {
            TbItem tbItem = iterator.next();
            if(tbItem.getId()==itemId.longValue()){
                tbItem.setNum(itemNum);
                break;
            }
        }
        CookieUtils.setCookie(request, response, "cart", JsonUtils.objectToJson(list),true);
        return list;
    }
}
