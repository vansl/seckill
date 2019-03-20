package com.vansl.seckill.service;

import com.vansl.seckill.dataobject.Product;

import java.util.List;

public interface ProductService {

    /**
     * @description 初始化商品库存
     * @param quantity 商品数量
     * @date 2019-03-19 23:12:27
     **/
    void initStock(Long quantity);

    List<Product> findAll();

    /**
     * @description 获取秒杀成功的人数
     * @date 2019-03-20 05:19:12
     **/
    Long findSuccessCount();

    /**
     * @description 执行秒杀程序
     * @param program 秒杀方案：A,B,C...
     * @date 2019-03-20 05:35:14
     **/
    void seckill(String program);

}
