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
        Product product = productRepository.findById(1L).orElse(null);
        if (product==null) {
            // 新增商品
            product = new Product();
            product.setProductId(1L);
            product.setProductName("iPhone SE");
            product.setProductPrice(new BigDecimal(2999));
            product.setProductQuantity(quantity);
            productRepository.save(product);
        } else {
            product.setProductQuantity(quantity);
            productRepository.save(product);
        }
        // 秒杀成功人数清零
        successCount = new AtomicLong(0);
    }

    @Override
    public Product findById(Long id) {
        return productRepository.findById(1L).orElse(null);
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
        Product product = productRepository.findById(1L).orElse(null);
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
        Product product = productRepository.findById(1L).orElse(null);
        Long oldQuantity = product.getProductQuantity();
        if (oldQuantity<1) {
            throw new RuntimeException("库存不足");
        }
        product.setProductQuantity(oldQuantity-1);
        productRepository.save(product);
        successCount.incrementAndGet();
    }

    /**
     * @description 方案B：使用`Read-Commited`以上隔离级别的事务+库存限制+回滚
     * @date 2019-03-25 00:56:25
     **/
    @Transactional(isolation=Isolation.READ_COMMITTED)
    public void programB() {
        Integer count = productRepository.safeDecreaseProductQuantity(1L,1L);
        if (count==0) {
            throw new RuntimeException("库存不足");
        }
        successCount.incrementAndGet();
    }

    
    
    /**
     * @description 使用`CAS`乐观锁
     * @date 2019-03-25 01:00:32       
     **/
    public void programC() {
        
    }
}
