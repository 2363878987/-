package com.alibaba.cart.interceptor;

import com.alibaba.bean.TbUser;
import com.alibaba.commons.bean.E3Result;
import com.alibaba.commons.utils.CookieUtils;
import com.alibaba.sso.interfaces.SSOInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Login_Interceptor implements HandlerInterceptor {
    @Autowired
    private SSOInterface ssoInterface;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //所有的请求都过来验证一下有没有登陆
        //取一下页面里的cookie 就可以判断出用户是否登陆 如果cookie不为空 用户为登录状态 反之 为未登录状态
        String user = CookieUtils.getCookieValue(request, "tokenID",true);
        //当前的方法中已经做过非空验证了因此不必担心空指针问题
        E3Result e3Result = ssoInterface.checkToken(user);
        if (e3Result.getStatus()==200){
            //一旦确定用户登陆，就把用户存到request中
            TbUser tbUser = (TbUser)e3Result.getData();
            request.setAttribute("user",tbUser);
            return true;
        }
        //如果用户没有登陆则不存储request，这样在取Attribute的时候 就可以根据是否有“user”来断定用户是否登陆
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
