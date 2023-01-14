package com.hmdp.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.dto.LoginFormDTO;
import com.hmdp.dto.Result;
import com.hmdp.entity.User;
import com.hmdp.mapper.UserMapper;
import com.hmdp.service.IUserService;
import com.hmdp.utils.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

import static com.hmdp.utils.SystemConstants.USER_NICK_NAME_PREFIX;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    /**
     * 校验手机号功能
     *
     * @param phone
     * @param session
     * @return
     */
    @Override
    public Result sendCode(String phone, HttpSession session) {
        // 1.校验手机号
        if (RegexUtils.isPhoneInvalid(phone)) {
            // 2.如果不符合返回错误信息
            return Result.fail("手机号格式错误!");
        }

        // 3.符合，生成验证码
        String code = RandomUtil.randomNumbers(6);

        // 4.保存验证码到session中
        session.setAttribute("code", code);

        // 5.发送验证码
        //这里就假装发送，以日志的形式进行记录
        log.debug("发送短信成功，验证码为：{}", code);

        return Result.ok();
    }

    /**
     * 登录功能
     *
     * @param loginForm
     * @param session
     * @return
     */
    @Override
    public Result login(LoginFormDTO loginForm, HttpSession session) {
        // 1. 校验手机号
        String phone = loginForm.getPhone();
        if (RegexUtils.isPhoneInvalid(phone)) {
            // 2. 不符合，直接返回
            return Result.fail("手机格式错误");
        }

        // 3. 手机号校验成功，校验验证码
        String code = loginForm.getCode(); //前端返回的验证码
        Object cacheCode = session.getAttribute("code"); // 保存在session中的验证码
        if (cacheCode == null || !cacheCode.toString().equals(code)) {
            //session中的验证码为空或者二者比对不成功
            return Result.fail("验证码错误！");
        }

        // 4. 验证码也校验成功，查询用户
        User user = this.query().eq("phone", phone).one();

        // 5. 若用户不存在，则直接创建新的用户即可，并保存至数据库中
        if (user == null) {
            user = createUserWithPhone(phone);
        }

        // 6. 保存用户到session中
        session.setAttribute("user", user);

        return Result.ok();
    }

    private User createUserWithPhone(String phone) {
        User user = new User();
        user.setPhone(phone);
        // 生成随机 nickName
        user.setNickName(USER_NICK_NAME_PREFIX + RandomUtil.randomNumbers(10));
        // 保存用户到数据库中
        this.save(user);
        return user;
    }
}
