package com.lee.navigator;

import com.lee.aStar.Solution;
import com.lee.utils.ConnectionUtil;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import redis.clients.jedis.Jedis;

import java.util.Random;

public class Navigator extends Thread{
    private boolean isWork;
    private Jedis jedis;
    private Channel channel;

    public Navigator(boolean isWork) {
        try {
            this.isWork = isWork;
            jedis = ConnectionUtil.getJedis();
            channel = ConnectionUtil.getConnection().createChannel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setWork(boolean work) {
        isWork = work;
    }

  // 收到消息后,就可以开始任务了,使用Astar算法
  DeliverCallback deliverCallback =
      (consumerTag, message) -> {
        System.out.println("Navigator开始工作...");
        String carId = new String(message.getBody(), "UTF-8");
        // 1.读取地图、根据carId汽车x,y
        int x = Integer.parseInt(jedis.hget(carId, "x"));
        int y = Integer.parseInt(jedis.hget(carId, "y"));
        System.out.printf("导航器:汽车Id:%s x:%d y:%d\n", carId, x, y);

        // 2.A*算法求出路径
        Solution solution = new Solution();
        Random random = new Random();
        int endX = random.nextInt(20);
        int endY = random.nextInt(20);
        System.out.printf("(%d,%d)为随机生成的目标点\n",endY,endX);

        if (jedis.getbit("mapView",endY*20+endX)||jedis.getbit("blockView",endY*20+endX)){
            System.out.printf("(%d,%d)已经点亮,将重新选取...\n",endY,endX);
            if (random.nextInt(2) == 0){
                System.out.println("选择从头数第一个未探索点");
                int offset = 0;
                while (offset<=399){
                    if (!jedis.getbit("mapView",offset)){
                        endX = offset%20;
                        endY = offset/20;
                        System.out.printf("(%d,%d)为重新选择的点\n",endY,endX);
                        break;
                    }else {
                        offset++;
                    }
                }
            }else {
                System.out.println("选择倒数第一个未探索点");
                int offset = 399;
                while (offset>=0){
                    if (!jedis.getbit("mapView",offset)){
                        endX = offset%20;
                        endY = offset/20;
                        System.out.printf("(%d,%d)为重新选择的点\n",endY,endX);
                        break;
                    }else {
                        offset--;
                    }
                }
            }
        }
        //jedis.setbit("mapView",endY*20+endX,true);
        String task = "";
          if (!jedis.getbit("blockView",endY*20+endX)) {
            task = solution.getPath(x, y, endX, endY);
        }

        // 3.修改黑板 汽车的任务
        jedis.hset(carId, "task", task);
        System.out.printf("导航器:将小车%s任务设置为:%s\n", carId, jedis.hget(carId, "task"));
      };

    // 取消消息时的回调
    CancelCallback cancelCallback = consumerTag -> {
        System.out.println("消息消费中断");
    };

    @Override
    public void run() {
        try{

            // 侦听消息队列workQueue
            channel.basicConsume("workQueue",true,deliverCallback,cancelCallback);

            while (isWork){
                if (null == channel || !channel.isOpen()){
                    System.out.println("Navigator: channel断开重连...");
                    channel = ConnectionUtil.getConnection().createChannel();
                    channel.basicConsume("workQueue",true,deliverCallback,cancelCallback);
                }

                Thread.sleep(1000);
                Thread.yield();
            }

            // 断开黑板连接
            jedis.close();

            // 断开消息队列连接
            channel.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
