package com.alibaba.cart.service;

import com.alibaba.bean.TbItem;
import com.alibaba.bean.TbUser;
import com.alibaba.cart.interfaces.CartNoManInterface;
import com.alibaba.cart.interfaces.CartOnlineInterface;
import com.alibaba.commons.jedis.JedisClient;
import com.alibaba.commons.utils.JsonUtils;
import com.alibaba.mapper.ItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class CartOnlineInterface_Imp implements CartOnlineInterface {
    @Autowired
    private JedisClient jedisClient;
    @Autowired
    private CartNoManInterface cartNoManInterface;
    @Autowired
    private ItemMapper itemService;
    @Override
    public List<TbItem> addCartOnline(TbUser user, Long itemId, int num) {
        boolean flag = true;
        // 1. 尝试从redis中获取购物车列表
        String cartList = jedisClient.get("Cart_UserId_"+user.getId());
        List<TbItem> redisList = cartNoManInterface.getCartListFromCookie(cartList);
        for(TbItem item : redisList){
            //如果之前购物车中就存在该商品则修改该商品的数量
            if(item.getId()==itemId.longValue()){
                item.setNum(item.getNum()+num);
                //这里设置的标识是为了购物车中既然包含该商品那么商品的数量就应该是 原购物车商品的num + 现在购物车商品的num
                //如果已经在这里修改过了但是代码继续走到下面就会被重新修改为一个 现在商品的num值 这样的添加相同的商品等于没有添加
                flag = false;
                break;
            }
        }
        //根据标识可以得知原购物车中有没有添加过该商品，如果没有则需要我们给出这个商品
        if (flag) {
            TbItem item = itemService.selectByPrimaryKey(itemId);
            item.setId(itemId);
            item.setNum(num);
            redisList.add(item);
        }
        //同步到数据库中
        jedisClient.set("Cart_UserId_"+user.getId(), JsonUtils.objectToJson(redisList));
        return redisList;
    }

    @Override
    public void mergeCartOnline(TbUser user,List<TbItem> cookieCartList) {

        for (TbItem item:cookieCartList
             ) {
            //充分利用java中的封装的思想 大量简化了代码
            //这里调用了添加购物车的方法 将cookie中的集合对象一个一个的合并到redis中
            //通过添加购物车这个方法 之前有的就可以增加num属性 没有的就新增一个 并且修改了redis数据库
            addCartOnline(user, item.getId(), item.getNum());

        }
    }

    @Override
    public List<TbItem> pushCartListOnlineToRedis(TbUser user) {
        String cartList = jedisClient.get("Cart_UserId_"+user.getId());

        return JsonUtils.jsonToList(cartList,TbItem.class);
    }

    @Override
    public List<TbItem> deleteCartOnline(TbUser user) {
        return null;
    }

    @Override
    public List<TbItem> updateCartOnline(TbUser user) {
        return null;
    }
}
