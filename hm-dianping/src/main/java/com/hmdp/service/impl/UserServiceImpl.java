package com.hmdp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.dto.Result;
import com.hmdp.entity.User;
import com.hmdp.mapper.UserMapper;
import com.hmdp.service.IUserService;
import com.hmdp.utils.RegexUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Override
    public Result sendCode(String phone, HttpSession session) {
        // 1.校验手机号
        if (!RegexUtils.isPhoneInvalid(phone)) {
            // 2.如果不符合返回错误信息
            return Result.fail("手机号格式错误!");
        }

        // 3.符合，生成验证码
        
        // 4.保存验证码到session中

        // 5.发送验证码

        return Result.ok();
    }
}
