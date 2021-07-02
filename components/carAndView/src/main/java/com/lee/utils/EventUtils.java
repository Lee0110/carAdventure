package com.lee.utils;

import com.lee.entity.AllData;
import com.lee.entity.Block;
import com.lee.entity.Car;
import com.lee.factory.CarFactory;
import redis.clients.jedis.Jedis;

import java.awt.event.ActionEvent;
import java.util.Random;

public class EventUtils {

    /**
     * 清空日志区
     */
    public static String clearLogsTextAreaActionPerformed(ActionEvent evt) {
        return "";
    }

    /**
     * 增加小车
     */
    public static Car carAddActionPerformed(ActionEvent evt,int carColor) {
        // 让小车工厂生产一个小车
        return CarFactory.getCar(carColor);
    }

    /**
     * 增加障碍物
     */
    public static Block blockAddActionPerformed(ActionEvent evt) {
        // 1.获取黑板连接
        Jedis jedis = ConnectionUtil.jedisPool.getResource();
        jedis.auth(ConnectionUtil.JEDIS_PASSWORD);

        // 2.随机生成一个障碍物的x，y坐标
        Random random = new Random();
        int x = random.nextInt(20);
        int y = random.nextInt(20);

        // 3.判断当前已经是障碍物了，则重新生成，如果超过50次还不成功，直接返回null
        int count = 0;
//        if (null == jedis || !jedis.isConnected()) {
//            System.out.println("BlockProducer: jedis断开重连...");
//            jedis = ConnectionUtil.jedisPool.getResource();
//        }
        boolean flag = jedis.getbit("blockView", y * 20 + x);
        while (flag) {
            x = random.nextInt(20);
            y = random.nextInt(20);
            count += 1;
            if (count > 50) {
                return null;
            }
        }

        // 4.根据生成的x和y改变blockView
//        if (null == jedis || !jedis.isConnected()) {
//            System.out.println("BlockProducer: jedis断开重连...");
//            jedis = ConnectionUtil.jedisPool.getResource();
//        }
        jedis.setbit("blockView", y * 20 + x, true);

        // 5.然后将mapView的这个地方点亮
//        if (null == jedis || !jedis.isConnected()) {
//            System.out.println("BlockProducer: jedis断开重连...");
//            jedis = ConnectionUtil.jedisPool.getResource();
//        }
        jedis.setbit("mapView", y * 20 + x, true);
        AllData.mapView[y * 20 + x] = -1;

        // 6.关闭jedis连接
        jedis.close();

        return new Block(x, y);
    }
}
