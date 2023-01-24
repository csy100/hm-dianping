package com.hmdp.service;

import com.hmdp.dto.Result;
import com.hmdp.entity.ShopType;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 陈思羽
 * @since 2023-01-24
 */
public interface IShopTypeService extends IService<ShopType> {

    Result queryTypeList();
}
