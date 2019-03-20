package com.vansl.seckill.controller;

import com.vansl.seckill.DTO.SeckillDTO;
import com.vansl.seckill.VO.ResultVO;
import com.vansl.seckill.dataobject.Product;
import com.vansl.seckill.service.ProductService;
import com.vansl.seckill.util.ConcurrentRequestUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

@RestController
public class SeckillController {

    @Autowired
    ProductService productService;

    Long DEFAULT_PRODUCT_QUANTITY = 10000L;
    Integer DEFAULT_CONCURRENT = 100;

    /**
     * @description 初始化商品和秒杀人数
     * @param1 quantity
     * @date 2019-03-20 02:29:14
     **/
    @PostMapping(value = "/init")
    public ResultVO init(@RequestBody Map<String,String> params) {
        ResultVO result = new ResultVO();
        Long quantity;
        // TODO 不同秒杀方案不同初始化条件
        if (params.get("quantity").isEmpty()) {
            quantity = DEFAULT_PRODUCT_QUANTITY;
        } else {
            quantity = Long.valueOf(params.get("quantity"));
        }
        productService.initStock(quantity);
        return result;
    }

    /**
     * @description 获取秒杀状态
     * @return 商品名称、商品库存、成功下单人数
     * @date 2019-03-20 02:14:21
     **/
    @GetMapping(value = "/status")
    public ResultVO<SeckillDTO> list() {
        ResultVO<SeckillDTO> result = new ResultVO<>();
        SeckillDTO seckillDTO = new SeckillDTO();
        // 只返回一件商品
        List<Product> productList = productService.findAll();
        // TODO 商品尚未初始化异常
        Product product = productList.get(0);
        BeanUtils.copyProperties(product,seckillDTO);
        // 返回成功下单人数
        seckillDTO.setSuccessCount(productService.findSuccessCount());
        result.setData(seckillDTO);
        return result;
    }


    /**
     * @description 开始秒杀
     * @param1 program 秒杀方案，默认为对照组（无数据同步）
     * @param2 concurrent 秒杀并发数量
     * @date 2019-03-20 02:31:04
     **/
    @PostMapping(value = "/start")
    public ResultVO startSeckil(@RequestBody Map<String,String> params){
        String program = params.get("program");
        Integer concurrent;
        if (params.get("concurrent").isEmpty()) {
            concurrent = DEFAULT_CONCURRENT;
        } else {
            concurrent = Integer.valueOf(params.get("concurrent"));
        }
        // 使用CountDownLatch实现同时请求
        CountDownLatch startGate = new CountDownLatch(1);
        ConcurrentRequestUtil.startConcurrentRequest(concurrent,()->{
            try {
                startGate.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (int j = 0; j < 10; j++) {
                if (Thread.currentThread().isInterrupted()) {
                    return;
                }
                productService.seckill(program);
            }
        });
        // 打开CountDownLatch
        startGate.countDown();
        return new ResultVO();
    }

    /**
     * @description 停止秒杀
     * @date 2019-03-20 04:42:12
     **/
    @GetMapping("/stop")
    public ResultVO stopSeckill() {
        ResultVO resultVO = new ResultVO();
        ConcurrentRequestUtil.stopRequest();
        return resultVO;
    }

}
