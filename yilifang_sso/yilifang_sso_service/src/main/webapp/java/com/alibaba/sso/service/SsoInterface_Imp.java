package com.alibaba.sso.service;

import com.alibaba.bean.TbUser;
import com.alibaba.bean.TbUserExample;
import com.alibaba.commons.bean.E3Result;
import com.alibaba.commons.jedis.JedisClient;
import com.alibaba.commons.utils.CookieUtils;
import com.alibaba.commons.utils.JsonUtils;
import com.alibaba.mapper.TbUserMapper;
import com.alibaba.sso.interfaces.SSOInterface;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class SsoInterface_Imp implements SSOInterface {

    @Autowired
    private TbUserMapper userMapper;
    @Autowired
    private JedisClient jedisClient;


    //对注册页面中的用户名和手机号进行校验(type:1 代表用户名 2 代表手机号)
    @Override
    public E3Result checkusernameORphone(String usernameORphone, int type) {
        TbUserExample userExample = new TbUserExample();
        TbUserExample.Criteria criteria = userExample.createCriteria();
        if (type==1){
            //check username
            TbUserExample.Criteria usernameEqualTo = criteria.andUsernameEqualTo(usernameORphone);
            List<TbUser> userList = userMapper.selectByExample(userExample);
            if (userList!=null&&userList.size()>0){
                return E3Result.ok(false);
            }
        }else {
            //check phone
            TbUserExample.Criteria phoneEqualTo = criteria.andPhoneEqualTo(usernameORphone);
            List<TbUser> userList = userMapper.selectByExample(userExample);
            if (userList!=null&&userList.size()>0){
                return E3Result.ok(false);
            }
        }
        return E3Result.ok(true);
    }
    //用户注册
    @Override
    public E3Result register(TbUser user) {
        user.setCreated(new Date());
        user.setUpdated(new Date());
        userMapper.insert(user);
        return E3Result.ok();
    }
    //用户登陆
    @Override
    public E3Result login(TbUser user) {
        TbUserExample userExample = new TbUserExample();
        //用户名
        TbUserExample.Criteria userNameExampleCriteria = userExample.createCriteria();
        userNameExampleCriteria.andUsernameEqualTo(user.getUsername());
        List<TbUser> userNameList = userMapper.selectByExample(userExample);
        if (userNameList!=null&&userNameList.size()>0){
            //这里后面做加密验证
            if (userNameList.get(0).getPassword().equals(user.getPassword())){
                //使用redis模拟session ：token认证
                String tokenID = UUID.randomUUID().toString();
                jedisClient.set("Session:"+tokenID, JsonUtils.objectToJson(userNameList.get(0)));

                return E3Result.ok(tokenID);
            }
        }
/*        //邮箱
        TbUserExample.Criteria  userEmailExampleCriteria= userExample.createCriteria();
        userEmailExampleCriteria.andEmailEqualTo(user.getEmail());
        List<TbUser> userEmailList = userMapper.selectByExample(userExample);
        if (userEmailList!=null&&userEmailList.size()>0){
            //这里后面做加密验证
            if (userEmailList.get(0).getPassword().equals(user.getPassword())){
                //使用redis模拟session ：token认证
                String tokenID = UUID.randomUUID().toString();
                jedisClient.set("Session:"+tokenID, JsonUtils.objectToJson(userNameList.get(0)));
                return E3Result.ok();
            }
        }
        //手机号
        TbUserExample.Criteria  userPhoneExampleCriteria= userExample.createCriteria();
        userPhoneExampleCriteria.andEmailEqualTo(user.getEmail());
        List<TbUser> userPhoneList = userMapper.selectByExample(userExample);
        if (userPhoneList!=null&&userPhoneList.size()>0){
            //这里后面做加密验证
            if (userPhoneList.get(0).getPassword().equals(user.getPassword())){
                //使用redis模拟session ：token认证
                String tokenID = UUID.randomUUID().toString();
                jedisClient.set("Session:"+tokenID, JsonUtils.objectToJson(userNameList.get(0)));
                return E3Result.ok();
            }
        }*/

        return E3Result.build(400,"账户或者密码错误");
    }

    @Override
    public E3Result checkToken(String tokenID) {
        String user = jedisClient.get("Session:"+tokenID);
        if (StringUtils.isNotBlank(user)){
            jedisClient.expire("Session:"+tokenID,300);
            /**这里为了避免在序列化E3Resut的时候把已经就是json格式的user再序列化
             * 造成序列化的重复也就造成了json格式出错娶不到值，也就会在页面上报undefined
             * 所以现在这里将这个user对象序列化，等controller 处理jsonp的时候再被反序列化一遍
            */
            return E3Result.ok(JsonUtils.jsonToPojo(user,TbUser.class));
        }
        return E3Result.build(400,"身份失效");
    }
}
