package com.lee.controller;

import com.lee.utils.ConnectionUtil;
import com.rabbitmq.client.Channel;
import redis.clients.jedis.Jedis;

import java.nio.charset.StandardCharsets;

public class Controller extends Thread {
    private boolean isWork;
    private Jedis jedis;
    private Channel channel;
    private String carId;

    public Controller(boolean isWork,String carId) {
        try {
            this.isWork = isWork;
            this.carId = carId;
            jedis = ConnectionUtil.getJedis();
            channel = ConnectionUtil.getConnection().createChannel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setWork(boolean work) {
        this.isWork = work;
    }

    @Override
    public void run() {
        try {
            System.out.println("Controller开始工作...");
            System.out.printf("该控制器为小车%s服务\n",carId);

            while (isWork){

                if (null == channel || !channel.isOpen()){
                    System.out.println("Controller: channel断开重连...");
                    channel = ConnectionUtil.getConnection().createChannel();
                }

                // 判断地图是否还有未探索区域
                boolean flag = jedis.bitcount("mapView", 0, 49) != 400L;
                if (flag) {
                    // 如果还有地图未探索,并且当前小车任务为空,给队列workQueue发消息,消息是carId,通知导航器给这个小车一个任务
                    if (jedis.hget(carId,"task").equals("")){
                        System.out.printf("控制器%s:小车%s任务为空,请求任务,通知导航器开始工作\n",carId,carId);
                        channel.basicPublish("","workQueue",null,carId.getBytes(StandardCharsets.UTF_8));
                        Thread.sleep(9000);
                    }

                }

                if (flag) {
                    // 读小车任务,并且通知小车移动,然后把这个小车的任务变成substring(1)
                    String task = jedis.hget(carId,"task");
                    System.out.printf("控制器%s:读到小车%s,其任务是%s\n",carId,carId,task);
                    if (!task.equals("")){
                        System.out.printf("控制器%s:小车%s任务不为空,通知其移动\n",carId,carId);
                        System.out.printf("控制器%s:给交换机carDirectExchange发消息,消息为%s\n",carId,task.substring(0,1));
                        // 控制小车移动
                        channel.basicPublish("carDirectExchange",carId,null,task.substring(0,1).getBytes(StandardCharsets.UTF_8));

                        // 改变小车的任务
                        jedis.hset(carId,"task",task.substring(1));
                        System.out.printf("控制器%s:将小车%s的任务改变为%s\n",carId,carId,jedis.hget(carId,"task"));
                    }
                }else{
                    System.out.printf("控制器%s:地图探索完毕...\n",carId);
                    this.isWork = false;
                }

                Thread.sleep(500);
                Thread.yield();
            }

            // 断开黑板连接
            jedis.close();

            // 断开消息队列连接
            channel.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
