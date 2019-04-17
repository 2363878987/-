package com.alibaba.sso.web;

import com.alibaba.bean.TbUser;
import com.alibaba.commons.bean.E3Result;
import com.alibaba.commons.utils.CookieUtils;
import com.alibaba.commons.utils.JsonUtils;
import com.alibaba.sso.interfaces.SSOInterface;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
    //用户登陆
    @RequestMapping("/user/login")
    @ResponseBody
    public E3Result login(TbUser user, HttpServletRequest request, HttpServletResponse response){
        E3Result e3Result = ssoInterface.login(user);
        String token = (String)e3Result.getData();
        CookieUtils.setCookie(request,response,"tokenID",token);
        return e3Result;
    }
    //验证token令牌
    @RequestMapping(value = "/user/token/{tokenID}",method = RequestMethod.GET)
    @ResponseBody
    public String checkToken(@PathVariable String tokenID,String callback){
        System.out.println("jsonp值 ："+callback);
        //jsonp格式的数据 封装了callback属性
        if (StringUtils.isBlank(callback)){
            return JsonUtils.objectToJson(ssoInterface.checkToken(tokenID));
        }
//StringEscapeUtils.unescapeJson(
        return callback+"("+JsonUtils.objectToJson(ssoInterface.checkToken(tokenID))+")";
    }
}
