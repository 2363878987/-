package com.alibaba.sso.service;

import com.alibaba.bean.TbUser;
import com.alibaba.bean.TbUserExample;
import com.alibaba.commons.bean.E3Result;
import com.alibaba.mapper.TbUserMapper;
import com.alibaba.sso.interfaces.SSOInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SsoInterface_Imp implements SSOInterface {

    @Autowired
    private TbUserMapper userMapper;


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
}
