package org.iiidev.controller;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
public class StockController {

    @Autowired
    private StringRedisTemplate redisTemplate;
//
//    @GetMapping("/stock")
//    public String stock(){
//        int stock = Integer.parseInt(redisTemplate.opsForValue().get("stock"));
//        if(stock > 0){
//            stock --;
//            redisTemplate.opsForValue().set("stock",stock+"");
//            System.out.println("库存扣减成功，剩余库存：" + stock);
//        }else {
//            System.out.println("库存不足！！！");
//        }
//        return "OK";
//    }


    @Autowired
    private CuratorFramework curatorFramework;

//    @GetMapping("/stock")
//    public String stock() {
//        InterProcessMutex mutex = new InterProcessMutex(curatorFramework,"/mylock");
//
//        try {
//            //尝试获得锁
//            boolean locked = mutex.acquire(0, TimeUnit.SECONDS);
//            if(locked){
//                int stock = Integer.parseInt(redisTemplate.opsForValue().get("stock"));
//                if(stock > 0){
//                    stock --;
//                    redisTemplate.opsForValue().set("stock",stock+"");
//                    System.out.println("库存扣减成功，剩余库存：" + stock);
//                }else {
//                    System.out.println("库存不足！！！");
//                }
//                //释放锁
//                mutex.release();
//            }else{
//                System.out.println("没有获取锁，不能执行减库存操作！！！");
//            }
//        }catch (Exception ex){
//            System.out.println("出现异常！！！");
//        }
//
//        return "OK";
//    }

    @Autowired
    private RedissonClient redissonClient;

    @GetMapping("/stock")
    public String stock() {
        //获得分布式锁对象，注意，此时还没有加锁成功
        RLock lock = redissonClient.getLock("mylock");
        try {
            //尝试加锁，如果加锁成功则后续程序继续执行，如果加锁不成功则阻塞等待
            lock.lock(5000,TimeUnit.MILLISECONDS);

            int stock = Integer.parseInt(redisTemplate.opsForValue().get("stock"));
            if(stock > 0){
                stock --;
                redisTemplate.opsForValue().set("stock",stock+"");
                System.out.println("库存扣减成功，剩余库存：" + stock);
            }else {
                System.out.println("库存不足！！！");
            }
        }catch (Exception ex){
            System.out.println("出现异常！！！");
        }finally {
            //解锁
            lock.unlock();
        }

        return "OK";
    }
}