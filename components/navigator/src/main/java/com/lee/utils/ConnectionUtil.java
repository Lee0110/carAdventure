package com.lee.utils;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import redis.clients.jedis.Jedis;

public class ConnectionUtil {

    public static final String CHANNEL_USERNAME = "请输入...";
    public static final String JEDIS_PASSWORD = "请输入...";
    public static final String CHANNEL_PASSWORD = "请输入...";
    public static final String HOST = "请输入...";
    public static final int JEDIS_PORT = 请输入...;
    public static final int CHANNEL_PORT = 请输入...;
    public static final String VIRTUALHOST = "请输入...";

    public static Connection getConnection() throws Exception{
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(HOST);
        connectionFactory.setPort(CHANNEL_PORT);
        connectionFactory.setUsername(CHANNEL_USERNAME);
        connectionFactory.setPassword(CHANNEL_PASSWORD);
        connectionFactory.setVirtualHost(VIRTUALHOST);
        Connection connection = connectionFactory.newConnection();
        return connection;
    }

    public static Jedis getJedis(){
        Jedis jedis = new Jedis("请输入...);
        jedis.auth("请输入...");
        return jedis;
    }
}
