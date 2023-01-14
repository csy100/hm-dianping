package com.hmdp.utils;

import com.hmdp.entity.User;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 从请求域中获取session
        HttpSession session = request.getSession();

        // 2. 从session中取到用户
        User user = (User) session.getAttribute("user");

        // 3. 判断用户是否存在
        if (user == null) {
            // 4. 进行拦截
            return false;
        }

        // 5. 用户存在，将用户存在ThreadLocal中，开辟线程


        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
