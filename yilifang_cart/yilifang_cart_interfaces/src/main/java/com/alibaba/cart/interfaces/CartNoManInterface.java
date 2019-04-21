package com.alibaba.cart.interfaces;

import com.alibaba.bean.TbItem;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
//未登录状态的购物车
public interface CartNoManInterface{
    //添加购物车功能（所有针对item的操作只是购物车而已，暂时并不会对数据库中的item表进行任何处理）
    List<TbItem> getCartListFromCookie(String json);
}
