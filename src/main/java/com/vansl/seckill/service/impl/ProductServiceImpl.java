package com.vansl.seckill.service.impl;

import com.vansl.seckill.dataobject.Product;
import com.vansl.seckill.repository.ProductRepository;
import com.vansl.seckill.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ProductServiceImpl implements ProductService {


    @Autowired
    ProductRepository productRepository;

    // 记录成功下单的人数
    private AtomicLong successCount = new AtomicLong(0);

    @Override
    @Transactional(isolation=Isolation.SERIALIZABLE)
    public void initStock(Long quantity) {
        List<Product> productList = findAll();
        if (productList.size()==0) {
            // 新增商品
            Product product = new Product();
            product.setProductName("iPhone SE");
            product.setProductPrice(new BigDecimal(2999));
            product.setProductQuantity(quantity);
            productRepository.save(product);
        } else {
            Product product = productList.get(0);
            product.setProductQuantity(quantity);
            productRepository.save(product);
        }
        // 秒杀成功人数清零
        successCount = new AtomicLong(0);
    }

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Long findSuccessCount() {
        return successCount.longValue();
    }

    @Override
    public void seckill(String program) {
        // 反射执行方法
        try {
            this.getClass().getDeclaredMethod("program"+program).invoke(this);
        } catch (NoSuchMethodException e) {
            // TODO 异常处理
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }


    /**
     * @description 对照组：select+update发生超卖问题
     * @date 2019-03-20 10:00:02       
     **/
    public void program() {
        List<Product> productList = findAll();
        Product product = productList.get(0);
        Long oldQuantity = product.getProductQuantity();
        if (product.getProductQuantity() >= 1) {
            product.setProductQuantity(oldQuantity - 1);
            // TODO 无法原子执行以下代码，查询会有误差
            productRepository.save(product);
            successCount.incrementAndGet();
        } else {
            // TODO 异常处理
            throw new RuntimeException ("库存不足");
        }
    }

    /**
     * @description 方案A：使用`synchronized`串行化执行
     * @date 2019-03-20 10:00:12       
     **/
    public synchronized void programA() {
        List<Product> productList = findAll();
        Product product = productList.get(0);
        Long oldQuantity = product.getProductQuantity();
        product.setProductQuantity(oldQuantity-1);
        productRepository.save(product);
        successCount.incrementAndGet();
    }
}
