package com.hmdp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.hmdp.dto.Result;
import com.hmdp.entity.Shop;
import com.hmdp.mapper.ShopMapper;
import com.hmdp.service.IShopService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.utils.RedisConstants;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 陈思羽
 * @since 2023-01-18
 */
@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result queryById(Long id) {
        // 1. 从redis中获取店铺信息
        String cacheShopKey = RedisConstants.CACHE_SHOP_KEY + id;
        String shopJson = stringRedisTemplate.opsForValue().get(cacheShopKey);
        // 2. 判断是否存在
        if (StrUtil.isNotBlank(shopJson)) {
            // 3. shopJson存在,将其转换成对象并返回
            Shop shop = JSONUtil.toBean(shopJson, Shop.class);
            return Result.ok(shop);
        }

        // TODO redis中不存在，进行以下操作
        // 4. 从数据库中获取该信息
        Shop shop = this.getById(id);
        // 5. 为查询出该商户信息
        if (shop == null) {
            return Result.fail("未查询到该商户信息！");
        }
        // 6 . 将该数据存储到redis中
        String toShopJson = JSONUtil.toJsonStr(shop);
        stringRedisTemplate.opsForValue().set(cacheShopKey, toShopJson);

        return Result.ok(shop);
    }
}
