package com.lee.entity;

import com.lee.utils.ConnectionUtil;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import redis.clients.jedis.Jedis;

import javax.swing.*;

/**
 * 汽车实体类
 */
public class Car extends Thread {

    public static final char UP = 'U';
    public static final char DOWN = 'D';
    public static final char LEFT = 'L';
    public static final char RIGHT = 'R';

    private String carID;// 车的ID
    private long x;// x坐标
    private long y;// y坐标
    private char status;// 头的朝向
    private ImageIcon carIcon;// 图片
    private boolean isWork;// 是否进行工作
    private Channel channel;
    private Jedis jedis;
    private int carColor;

    public Car(String carID, long x, long y, char status,int carColor) {
        try {
            this.carID = carID;
            this.x = x;
            this.y = y;
            this.status = status;
            this.setCarIcon();
            this.isWork = true;
            this.carColor = carColor;
            this.channel = ConnectionUtil.getConnection().createChannel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 接收任务进行移动
    DeliverCallback deliverCallback =
        (consumerTag, message) -> {
            String taskString = new String(message.getBody(), "UTF-8");
            jedis = ConnectionUtil.getJedis();
            String s = "";
            switch (taskString.charAt(0)) {
                case 'U':
                    s = jedis.hget(carID, "y");
                    System.out.printf("%s: y -> %s\n%s: 即将往上走一步\n",carID,s,carID);
                    if (!"".equals(s)) {
                        if (Integer.parseInt(s) > 0) {
                            jedis.hset(carID, "y", String.valueOf(y - 1));
                            this.y -= 1;
                            this.status = 'U';
                            this.setCarIcon();

                            // 点亮周围
                            jedis.setbit("mapView", y  * 20 + x, true);
                            AllData.mapView[(int) (y * 20 + x)] = carColor;
                            if (y>0) {
                                jedis.setbit("mapView", (y - 1) * 20 + x, true);
                                if (!jedis.getbit("blockView",(y - 1) * 20 + x)) {
                                    AllData.mapView[(int) ((y - 1) * 20 + x)] = carColor;
                                }
                            }
                            if (y<19) {
                                jedis.setbit("mapView", (y + 1) * 20 + x, true);
                                if (!jedis.getbit("blockView",(y + 1) * 20 + x)) {
                                    AllData.mapView[(int) ((y + 1) * 20 + x)] = carColor;
                                }
                            }
                            if (x>0) {
                                jedis.setbit("mapView", y * 20 + x - 1, true);
                                if (!jedis.getbit("blockView",y * 20 + x - 1)) {
                                    AllData.mapView[(int) (y * 20 + x - 1)] = carColor;
                                }
                            }
                            if (x<19) {
                                jedis.setbit("mapView", y * 20 + x + 1, true);
                                if (!jedis.getbit("blockView",y * 20 + x + 1)) {
                                    AllData.mapView[(int) (y * 20 + x + 1)] = carColor;
                                }
                            }

                            channel.basicAck(message.getEnvelope().getDeliveryTag(),false);
                        }
                    }
                    break;
                case 'D':
                    s = jedis.hget(carID, "y");
                    System.out.printf("%s: y -> %s\n%s: 即将往下走一步\n",carID,s,carID);
                    if (!"".equals(s)) {
                        if (Integer.parseInt(s) < 19) {
                            jedis.hset(carID, "y", String.valueOf(y + 1));
                            this.y += 1;
                            this.status = 'D';
                            this.setCarIcon();

                            // 点亮周围
                            jedis.setbit("mapView", y  * 20 + x, true);
                            AllData.mapView[(int) (y * 20 + x)] = carColor;
                            if (y>0) {
                                jedis.setbit("mapView", (y - 1) * 20 + x, true);
                                if (!jedis.getbit("blockView",(y - 1) * 20 + x)) {
                                    AllData.mapView[(int) ((y - 1) * 20 + x)] = carColor;
                                }
                            }
                            if (y<19) {
                                jedis.setbit("mapView", (y + 1) * 20 + x, true);
                                if (!jedis.getbit("blockView",(y + 1) * 20 + x)) {
                                    AllData.mapView[(int) ((y + 1) * 20 + x)] = carColor;
                                }
                            }
                            if (x>0) {
                                jedis.setbit("mapView", y * 20 + x - 1, true);
                                if (!jedis.getbit("blockView",y * 20 + x - 1)) {
                                    AllData.mapView[(int) (y * 20 + x - 1)] = carColor;
                                }
                            }
                            if (x<19) {
                                jedis.setbit("mapView", y * 20 + x + 1, true);
                                if (!jedis.getbit("blockView",y * 20 + x + 1)) {
                                    AllData.mapView[(int) (y * 20 + x + 1)] = carColor;
                                }
                            }
                            channel.basicAck(message.getEnvelope().getDeliveryTag(),false);
                        }
                    }
                    break;
                case 'L':
                    s = jedis.hget(carID, "x");
                    System.out.printf("%s: x -> %s\n%s: 即将往左走一步\n",carID,s,carID);
                    if (!"".equals(s)) {
                        if (Integer.parseInt(s) > 0) {
                            jedis.hset(carID, "x", String.valueOf(x - 1));
                            this.x -= 1;
                            this.status = 'L';
                            this.setCarIcon();

                            // 点亮周围
                            jedis.setbit("mapView", y  * 20 + x, true);
                            AllData.mapView[(int) (y * 20 + x)] = carColor;
                            if (y>0) {
                                jedis.setbit("mapView", (y - 1) * 20 + x, true);
                                if (!jedis.getbit("blockView",(y - 1) * 20 + x)) {
                                    AllData.mapView[(int) ((y - 1) * 20 + x)] = carColor;
                                }
                            }
                            if (y<19) {
                                jedis.setbit("mapView", (y + 1) * 20 + x, true);
                                if (!jedis.getbit("blockView",(y + 1) * 20 + x)) {
                                    AllData.mapView[(int) ((y + 1) * 20 + x)] = carColor;
                                }
                            }
                            if (x>0) {
                                jedis.setbit("mapView", y * 20 + x - 1, true);
                                if (!jedis.getbit("blockView",y * 20 + x - 1)) {
                                    AllData.mapView[(int) (y * 20 + x - 1)] = carColor;
                                }
                            }
                            if (x<19) {
                                jedis.setbit("mapView", y * 20 + x + 1, true);
                                if (!jedis.getbit("blockView",y * 20 + x + 1)) {
                                    AllData.mapView[(int) (y * 20 + x + 1)] = carColor;
                                }
                            }
                            channel.basicAck(message.getEnvelope().getDeliveryTag(),false);
                        }
                    }
                    break;
                case 'R':
                    s = jedis.hget(carID, "x");
                    System.out.printf("%s: x -> %s\n%s: 即将往右走一步\n",carID,s,carID);
                    if (!"".equals(s)) {
                        if (Integer.parseInt(s) < 19) {
                            jedis.hset(carID, "x", String.valueOf(x + 1));
                            this.x += 1;
                            this.status = 'R';
                            this.setCarIcon();

                            // 点亮周围
                            jedis.setbit("mapView", y  * 20 + x, true);
                            AllData.mapView[(int) (y * 20 + x)] = carColor;
                            if (y>0) {
                                jedis.setbit("mapView", (y - 1) * 20 + x, true);
                                if (!jedis.getbit("blockView",(y - 1) * 20 + x)) {
                                    AllData.mapView[(int) ((y - 1) * 20 + x)] = carColor;
                                }
                            }
                            if (y<19) {
                                jedis.setbit("mapView", (y + 1) * 20 + x, true);
                                if (!jedis.getbit("blockView",(y + 1) * 20 + x)) {
                                    AllData.mapView[(int) ((y + 1) * 20 + x)] = carColor;
                                }
                            }
                            if (x>0) {
                                jedis.setbit("mapView", y * 20 + x - 1, true);
                                if (!jedis.getbit("blockView",y * 20 + x - 1)) {
                                    AllData.mapView[(int) (y * 20 + x - 1)] = carColor;
                                }
                            }
                            if (x<19) {
                                jedis.setbit("mapView", y * 20 + x + 1, true);
                                if (!jedis.getbit("blockView",y * 20 + x + 1)) {
                                    AllData.mapView[(int) (y * 20 + x + 1)] = carColor;
                                }
                            }
                            channel.basicAck(message.getEnvelope().getDeliveryTag(),false);
                        }
                    }
                    break;
            }
            jedis.close();
        };

    // 取消消息时的回调
    CancelCallback cancelCallback = consumerTag -> {
        System.out.println(consumerTag+"消息消费中断");
    };

    /**
     * 汽车线程执行体
     */
    @Override
    public void run() {

        // 侦听消息队列，接受任务
        try {
            channel.basicConsume(carID, false, deliverCallback, cancelCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String str = "";
        while (this.isWork) {
            try {
                if (null == channel || !channel.isOpen()){
                    System.out.printf("%s: channel断开重连...\n",carID);
                    channel = ConnectionUtil.getConnection().createChannel();
                    channel.basicConsume(this.carID, false, deliverCallback, cancelCallback);
                }

                Thread.sleep(1000);
                Thread.yield();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            // 删除这个汽车对应的消息队列
            channel.queueDelete(carID);

            // 关闭消息队列连接
            channel.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public long getX() {
        return x;
    }

    public void setX(long x) {
        this.x = x;
    }

    public long getY() {
        return y;
    }

    public void setY(long y) {
        this.y = y;
    }

    public char getStatus() {
        return status;
    }

    public void setStatus(char status) {
        this.status = status;
    }

    public ImageIcon getCarIcon() {
        return carIcon;
    }

    public void setCarIcon() {
        switch (this.status) {
            case 'U':
        this.carIcon =
            new ImageIcon("D:\\javaWorkSpace_idea\\carAdventureView\\images\\carUp.png");
                break;
            case 'D':
        this.carIcon =
            new ImageIcon("D:\\javaWorkSpace_idea\\carAdventureView\\images\\carDown.png");
                break;
            case 'L':
        this.carIcon =
            new ImageIcon("D:\\javaWorkSpace_idea\\carAdventureView\\images\\carLeft.png");
                break;
            case 'R':
        this.carIcon =
            new ImageIcon("D:\\javaWorkSpace_idea\\carAdventureView\\images\\carRight.png");
    }
    }

    public String getCarID() {
        return carID;
    }

    public void setCarID(String carID) {
        this.carID = carID;
    }

    public boolean isWork() {
        return isWork;
    }

    public void setWork(boolean isWork) {
        this.isWork = isWork;
    }

}
