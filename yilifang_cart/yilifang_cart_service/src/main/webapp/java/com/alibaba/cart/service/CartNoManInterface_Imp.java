package com.alibaba.cart.service;

import com.alibaba.bean.TbItem;
import com.alibaba.cart.interfaces.CartNoManInterface;
import com.alibaba.commons.utils.CookieUtils;
import com.alibaba.commons.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartNoManInterface_Imp implements CartNoManInterface {

    public List<TbItem> getCartListFromCookie(String json){
        //对于从数据库cookie等查询到的结果一般要进行非空验证
        //对于填写的信息要进行有效的验证
        //这是为了防止出现空指针，或非法数据类型等异常
        if(StringUtils.isNoneBlank(json)){
            return JsonUtils.jsonToList(json, TbItem.class);
        }
        return new ArrayList<TbItem>();
    }


/*    @Override
    public List<TbItem> checkCookieFindCartList(long itemID, int num,HttpServletRequest request) {
        List<TbItem> list = new ArrayList<TbItem>();
        Boolean flag = true;
        //查询之前约定的 cookie 如果有item的集合则修改num 属性
        String noManCart = CookieUtils.getCookieValue(request, "noManCart");
        //进行非空判断
        if (noManCart != null) {
            List<TbItem> tbItems = JsonUtils.jsonToList(noManCart, TbItem.class);
            for (TbItem item:tbItems
                 ) {
                //如果之前存过只需要需改他原来的数量为当前数量加现在的num
                if (item.getId().equals(itemID)){
                    item.setNum(item.getNum()+num);

                    return tbItems;
                }
            }
        }
        //如果没有存过则 添加该商品到cookie中

            TbItem item = new TbItem();
            item.setId(itemID);
            item.setNum(num);
            list.add(item);

        return list;
    }*/
}
