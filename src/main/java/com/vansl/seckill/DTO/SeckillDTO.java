package com.vansl.seckill.DTO;

import lombok.Data;

@Data
public class SeckillDTO {

    /** 商品名称 **/
    private String productName;

    /** 商品库存 **/
    private Long productQuantity;

    /** 成功下单人数 **/
    private Long successCount;
}
