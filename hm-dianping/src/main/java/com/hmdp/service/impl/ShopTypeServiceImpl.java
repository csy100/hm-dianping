package com.hmdp.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.hmdp.dto.Result;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeMapper;
import com.hmdp.service.IShopTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.utils.RedisConstants;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result queryTypeList() {
        // 1.从redis中查询
        String cacheShopTypeKey = RedisConstants.CACHE_SHOPTYPE_KEY;
        String shopTypeJson = stringRedisTemplate.opsForValue().get(cacheShopTypeKey);
        // 2.查询到数据直接返回
        if (StrUtil.isNotBlank(shopTypeJson)) {
            return Result.ok(JSONUtil.toList(shopTypeJson, ShopType.class));
        }

        // TODO 从redis中未查询到数据
        // 3.从数据库中查询
        List<ShopType> shopTypeList = this.query().orderByAsc("sort").list();
        // 4.判断数据分类
        if (shopTypeList == null) {
            return Result.fail("未查询到分类数据！");
        }
        // 5.将数据保存至redis中
        stringRedisTemplate.opsForValue().set(cacheShopTypeKey, JSONUtil.toJsonStr(shopTypeList));
        // 6.设置失效时间
        stringRedisTemplate.expire(cacheShopTypeKey, RedisConstants.CACHE_SHOPTYPE_TTL, TimeUnit.MINUTES);
        return Result.ok(shopTypeList);
    }
}
