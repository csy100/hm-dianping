package com.hmdp.utils;

import com.hmdp.dto.UserDTO;
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
        UserDTO user = (UserDTO) session.getAttribute("user");

        // 3. 判断用户是否存在
        if (user == null) {
            // 4. 设置状态码，状态码 401 Unauthorized
            // 代表客户端错误，指的是由于缺乏目标资源要求的身份验证凭证，发送的请求未得到满足
            response.setStatus(401);
            return false;
        }

        // 5. 用户存在，将用户存在ThreadLocal中，开辟线程
        UserHolder.saveUser(user);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 移除用户，避免内存的泄露
        UserHolder.removeUser();
    }
}
