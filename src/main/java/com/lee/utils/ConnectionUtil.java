package com.lee.utils;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import redis.clients.jedis.Jedis;

public class ConnectionUtil {

    public static final String CHANNEL_USERNAME = "lee";
    public static final String JEDIS_PASSWORD = "123Lyl!@#";
    public static final String CHANNEL_PASSWORD = "123";
    public static final String HOST = "8.141.64.164";
    public static final int JEDIS_PORT = 6379;
    public static final int CHANNEL_PORT = 5672;
    public static final String VIRTUALHOST = "/";

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
        Jedis jedis = new Jedis("8.141.64.164", 6379);
        jedis.auth("123Lyl!@#");
        return jedis;
    }
}
