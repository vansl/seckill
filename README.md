# seckill
用尽可能多的方式解决高并发秒杀系统中的超卖问题

## 一、超卖问题回溯
仔细想来，高并发情况下的秒杀超卖问题的表现形式有两种：库存扣为负数、下单人数与库存扣减数不一致。


## 二、方案

### 概述
- 初始条件：默认商品数量10000，秒杀成功人数0，并发线程数100
- 使用`CountDownLatch`实现模拟同时并发请求
- 为了简化数据库设计，使用`AtomicLong`记录秒杀成功人数，但同时由于该变量的递增与sql语句执行两个操作是非原子性的，会引发查询误差问题。

### 对照组：select+update发生超卖问题

### 方案A：使用`synchronized`串行化执行

### 方案B：使用`Serializable`隔离级别的事务串行化执行

### 方案C：使用消息队列串行化执行

### 方案D：使用redis分布式锁
- 注意事项：
    - 需要设置超时避免死锁
    
### 方案E：使用`Read-Repeatable`隔离级别的事务+单条sql
`update Product set product_quantity=product_quantity-1 where (product_quantity -1 ) >= 0;`

使用MySql的默认隔离级别可重复读（Read-Repeatable，RR）。在该隔离级别下，`update set`语句为当前读

### 方案F：使用`CAS`乐观锁

### 方案G：使用事务+排他锁(`for update`)

### 方案H：使用redis确认+消息队列异步扣减

### others：改数据库源码、`Group commit`.....
参考[秒杀场景下MySQL的低效--原因和改进](https://wenku.baidu.com/view/128b76190722192e4536f6de.html)


## 三、TODO
- 日志
- 统一异常处理 
- 压测
- 方案对比