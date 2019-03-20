package com.vansl.seckill.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @description 模拟并发请求
 * @date 2019-03-20 04:28:47
 **/
public class ConcurrentRequestUtil {

    private static volatile List<Thread> threads;

    public static void startConcurrentRequest(Integer concurrent,Runnable task) {
        threads = new ArrayList<>(concurrent);
        for(int i = 0; i < concurrent; i++) {
            threads.add(new Thread(task));
        }
        threads.forEach(Thread::start);
    }

    public static void stopRequest() {
        for(int i = 0; i < threads.size(); i++) {
            threads.forEach(Thread::interrupt);
        }
    }
}
