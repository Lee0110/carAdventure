package com.lee.factory;

import com.lee.controller.Controller;
import com.lee.utils.ConnectionUtil;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

public class ControllerFactory extends Thread{

    private Channel channel;
    private boolean isWork;

    public ControllerFactory(boolean isWork) {
        this.isWork = isWork;
    }

    public void setWork(boolean work) {
        isWork = work;
    }

    // 收到carId,生产一个控制器为之服务
    DeliverCallback deliverCallback = (consumerTag, message) -> {
        String carId = new String(message.getBody(), "UTF-8");
        System.out.println("控制器工厂: 正在生产一个控制器...");
        Controller c = new Controller(true,carId);
        c.start();
    };

    // 取消消息时的回调
    CancelCallback cancelCallback = consumerTag -> {
        System.out.println("消息消费中断");
    };

    @Override
    public void run(){

        try{
            // 1.获取连接
            channel = ConnectionUtil.getConnection().createChannel();

            // 2.侦听队列controller
            channel.basicConsume("controller",true,deliverCallback,cancelCallback);

            while (isWork){
                if (null == channel || !channel.isOpen()){
                    System.out.println("ControllerFactory: channel断开重连...");
                    channel = ConnectionUtil.getConnection().createChannel();
                }

                Thread.sleep(10000);
                Thread.yield();
            }

            channel.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
