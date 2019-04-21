package com.alibaba.cart.interfaces;

import com.alibaba.bean.TbItem;
import com.alibaba.bean.TbUser;

import java.util.List;

//登录状态的购物车
public interface CartOnlineInterface {
    //添加购物车(和未登录状态相同只不过存在了redis中)
    List<TbItem> addCartOnline(TbUser user, Long itemId, int num);
    //合并购物车（cookie 和 redis 合并）
    void mergeCartOnline(TbUser user,List<TbItem> cookieCartList);
    //查询redis中购物车集合
    List<TbItem> pushCartListOnlineToRedis(TbUser user);
    //删除购物车
    List<TbItem> deleteCartOnline(TbUser user);
    //修改购物车（购物车中的物品数量）
    List<TbItem> updateCartOnline(TbUser user);
}
