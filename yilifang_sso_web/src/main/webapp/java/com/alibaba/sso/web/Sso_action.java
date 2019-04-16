package com.alibaba.sso.web;

import com.alibaba.bean.TbUser;
import com.alibaba.commons.bean.E3Result;
import com.alibaba.sso.interfaces.SSOInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class Sso_action {
    @Autowired
    private SSOInterface ssoInterface;
    //跳转到注册页面
    @RequestMapping("/page/register")
    public String goRegister(){
        return "register";
    }
    //跳转到登陆页面
    @RequestMapping("/page/login")
    public String goLogin(){
        return "login";
    }

    //对注册页面中的用户名和手机号进行校验
    @RequestMapping("/user/check/{usernameORphone}/{type}")
    @ResponseBody
    public E3Result checkusernameORphone(@PathVariable String usernameORphone, @PathVariable int type){
        return ssoInterface.checkusernameORphone(usernameORphone,type);
    }
    //接收用户信息并注册
    @RequestMapping("/user/register")
    @ResponseBody
    public E3Result register(TbUser user){

        return ssoInterface.register(user);
    }
}
