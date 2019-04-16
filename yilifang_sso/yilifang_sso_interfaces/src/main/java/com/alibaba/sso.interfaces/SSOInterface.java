package com.alibaba.sso.interfaces;

import com.alibaba.bean.TbUser;
import com.alibaba.commons.bean.E3Result;

public interface SSOInterface {
    //对注册页面中的用户名和手机号进行校验(type:1 代表用户名 2 代表手机号)
    E3Result checkusernameORphone(String usernameORphone,int type);
    //用户注册
    E3Result register(TbUser user);
}
