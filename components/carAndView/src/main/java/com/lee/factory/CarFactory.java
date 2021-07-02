package com.lee.factory;

import com.lee.entity.Car;
import com.lee.utils.ConnectionUtil;
import com.rabbitmq.client.Channel;
import redis.clients.jedis.Jedis;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class CarFactory {

    private static Jedis jedis;
    private static Channel channel;

    /**
     * 生产一个小车
     */
    public static Car getCar(int carColor) {
        Car car = null;
        try {
            // 1.获取黑板连接，获取消息队列连接，如果获取不成功直接返回null
            jedis = ConnectionUtil.jedisPool.getResource();
            jedis.auth(ConnectionUtil.JEDIS_PASSWORD);
            channel = ConnectionUtil.getConnection().createChannel();

            // 2.随机生成一个carID
            String carID = UUID.randomUUID().toString();

            // 3.随机生成x,y坐标，不能和障碍物重叠，黑板里加一条数据，key是carID，值是x,y
            Random random = new Random();
            int x = random.nextInt(20);
            int y = random.nextInt(20);

            int count = 0;
            boolean flag;
            flag = jedis.getbit("blockView", y * 20 + x);
            while (flag) {
                x = random.nextInt(20);
                y = random.nextInt(20);
                count += 1;
                if (count > 5) {
                    return null;
                }
            }
            Map<String, String> carMap = new HashMap<String, String>();
            carMap.put("x", String.valueOf(x));
            carMap.put("y", String.valueOf(y));
            carMap.put("task","");
            jedis.hset(carID, carMap);

            jedis.close();

            // 4.消息队列增加一个队列，队列名字就是carID，然后给carId交换机发生成的汽车id
            channel.queueDeclare(carID, false, false, false, null);
            channel.queueBind(carID, "carDirectExchange", carID);
            channel.basicPublish("carId","",null,carID.getBytes(StandardCharsets.UTF_8));
            channel.close();

            car = new Car(carID, x, y, Car.UP,carColor);
            car.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return car;
    }
}
