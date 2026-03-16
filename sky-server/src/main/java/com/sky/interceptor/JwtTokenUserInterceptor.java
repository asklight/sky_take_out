package com.sky.interceptor;

import com.sky.constant.JwtClaimsConstant;
import com.sky.context.BaseContext;
import com.sky.properties.JwtProperties;
import com.sky.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class JwtTokenUserInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 该方法在请求处理之前被调用，用于进行JWT验证。
     * @param request 当前HTTP请求对象
     * @param response 当前HTTP响应对象
     * @param handler 处理请求的处理器对象（通常是一个Controller方法）
     * @return 如果返回true，表示继续处理请求；如果返回false，表示请求被拦截，后续处理将被终止。
     * @throws Exception 如果发生任何异常，将会被Spring MVC捕获并处理。
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 判断请求路径是否需要进行JWT验证
        if(!(handler instanceof HandlerMethod)){
            // 如果不是HandlerMethod，直接放行
            // 什么是HandlerMethod？它是Spring MVC中用于处理HTTP请求的方法的封装，包含了方法的相关信息，如方法名、参数等。
            return true;
        }

        //思路：
        //1. 从请求头中获取JWT token
        String token = request.getHeader(jwtProperties.getUserTokenName());
        //2. 验证token的有效性（如过期、签名错误等）
        try {
            Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), token);
            Long userId = Long.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());
            log.info("用户ID：{}", userId);
            //3. 如果token有效，将用户ID存储到BaseContext中，以便后续处理使用
            BaseContext.setCurrentId(userId);
            return true;
        }catch (Exception ex){
            //4. 如果token无效，返回401 Unauthorized响应
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
    }
}
