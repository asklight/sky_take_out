package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import com.sky.vo.UserLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    public static final String WX_LOGIN_URL = "https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    private WeChatProperties weChatProperties;
    @Autowired
    private UserMapper userMapper;

    /**
     * 微信登录
     * @param userLoginDTO 用户登录请求参数
     * @return 登录成功的用户信息
     */
    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) {

        //思路：
        //1.根据用户登录请求参数中的code，调用微信登录接口，获取openid
            // 请求微信登录接口需要传递以下参数：
        Map<String,String> map = new HashMap<>();
        map.put("appid", weChatProperties.getAppid());
        map.put("secret", weChatProperties.getSecret());
        map.put("js_code", userLoginDTO.getCode());
        map.put("grant_type", "authorization_code");
        String json = HttpClientUtil.doGet(WX_LOGIN_URL, map);

        JSONObject jsonObject = JSONObject.parseObject(json);
        String openid = jsonObject.getString("openid");

        // 如果openid为空，说明登录失败，抛出业务异常
        if (openid == null) {
            log.error("调用微信接口登录失败，返回结果：{}", json);
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        //2.根据openid查询数据库，判断用户是否已经注册
        User user = userMapper.getByOpenid(openid);

        //3.如果用户没有注册，则自动注册一个新用户，并将openid保存到数据库中
        if (user == null) {
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }

        //4.返回登录成功的用户信息
        return user;
    }
}
