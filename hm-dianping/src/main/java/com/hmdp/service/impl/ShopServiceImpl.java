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
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

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

    /**
     * 获取店铺信息
     * @param id
     * @return
     */
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

        if (shopJson != null) {
            return Result.fail("未查询到店铺！");
        }

        // TODO redis中不存在，进行以下操作
        // 4. 从数据库中获取该信息
        Shop shop = this.getById(id);
        // 5. 为查询出该商户信息
        if (shop == null) {
            //向redis中存入空值
            stringRedisTemplate.opsForValue().set(cacheShopKey, "", RedisConstants.CACHE_NULL_TTL, TimeUnit.MINUTES);
            return Result.fail("未查询到该商户信息！");
        }
        // 6. 将该数据存储到redis中
        String toShopJson = JSONUtil.toJsonStr(shop);
        Long cacheShopTtl = RedisConstants.CACHE_SHOP_TTL;
        stringRedisTemplate.opsForValue().set(cacheShopKey, toShopJson, cacheShopTtl, TimeUnit.MINUTES);

        return Result.ok(shop);
    }

    /**
     * 更新店铺信息
     * @param shop
     * @return
     */
    @Override
    @Transactional
    public Result update(Shop shop) {
        Long shopId = shop.getId();
        if (shopId == null) {
            return Result.fail("未查询到店铺信息!");
        }

        // 1. 更新数据库
        updateById(shop);
        // 2. redis中的数据进行删除
        String cacheShopKey = RedisConstants.CACHE_SHOP_KEY + shopId;
        stringRedisTemplate.delete(cacheShopKey);
        return Result.ok(shop);
    }

}
