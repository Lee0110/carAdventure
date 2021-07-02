package com.lee.view;

import com.lee.entity.AllData;
import com.lee.entity.Block;
import com.lee.entity.Car;
import com.lee.utils.ConnectionUtil;
import com.lee.utils.EventUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.LinkedList;

public class MainJPanel extends JPanel implements ActionListener {

    private Timer timer;// 40毫米一次
    private JTextArea logsTextArea;
    private String logs = "";
    private LinkedList<Car> carList;// 小车队列
    private LinkedList<Block> blockList;// 障碍物队列
    private HashMap<String, LinkedList<Character>> tasksMapList;// key为小车id，value为这个小车的任务
    private Jedis jedis;// 黑板连接
    private boolean isFinish;
    private int blockNum;
    private int carNum;

    public MainJPanel() {
        setLayout(null);

        // 增加小车按钮
        JButton carAddButton = new JButton("增加小车");
        carAddButton.addActionListener(e -> {
            if (carNum < 4) {
                Car car = EventUtils.carAddActionPerformed(e,carNum + 1);
                if (null != car) {
                    carNum++;
                    carList.add(car);
                    logs = "\n> 增加一辆小车> 小车Id:"+car.getCarID();
                } else {
                    logs = "\n> 增加小车失败,可以再次点击试试看...";
                }
            }else {
                logs = "\n> 本次程序设置的小车最大数量为4...";
            }
            changeLogsTextArea(logs);
        });
        carAddButton.setBounds(668, 87, 97, 23);
        add(carAddButton);

        // 增加障碍物按钮
        JButton blockAddButton = new JButton("增加障碍物");
        blockAddButton.addActionListener(e -> {
            Block block = null;
            if (blockNum < 20){
                block = EventUtils.blockAddActionPerformed(e);
                if (null != block){
                    blockList.add(block);
                    blockNum++;
                    logs = "\n> 增加了一个障碍物";
                }else {
                    logs = "\n> 生成障碍物出现了点问题,可以再次点击试试看...";
                }
            }else {
                logs = "\n> 本次程序设置的障碍物最大数量为20...";
            }
            changeLogsTextArea(logs);
        });
        blockAddButton.setBounds(668, 151, 97, 23);
        add(blockAddButton);

        // 结束按钮
        JButton endButtion = new JButton("结    束");
        endButtion.addActionListener(e -> {
            gameOver();
        });
        endButtion.setBounds(668, 216, 97, 23);
        add(endButtion);

        // 日志文字区
        logsTextArea = new JTextArea();
        logsTextArea.setLineWrap(true);
        logsTextArea.setFont(new Font("楷体", Font.PLAIN, 13));
        logsTextArea.setText("> 初始化...");
        logsTextArea.setEditable(false);
        logsTextArea.setBounds(655, 341, 120, 271);
        add(logsTextArea);

        // 清空按钮
        JButton clearTextAreaButton = new JButton("清    空");
        clearTextAreaButton.addActionListener(e -> {
            logs = EventUtils.clearLogsTextAreaActionPerformed(e);
            changeLogsTextArea(logs);
        });
        clearTextAreaButton.setBounds(668, 275, 97, 23);
        add(clearTextAreaButton);

        // 调用初始化方法
        init();
    }

    /**
     * 改变日志区文本
     */
    private void changeLogsTextArea(String logs) {
        logsTextArea.setText(logs);
    }

    /**
     * 初始化,黑板初始化,消息队列初始化
     */
    private void init() {
        try {
            // 初始化数据
            carList = new LinkedList<Car>();
            blockList = new LinkedList<Block>();
            tasksMapList = new HashMap<String, LinkedList<Character>>();
            blockNum = 0;
            carNum = 0;
            AllData.mapView = new int[400];

            // 初始化黑板连接池
            logs += "\n> 初始化黑板连接池...";
            logsTextArea.setText(logs);
            JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
            jedisPoolConfig.setMaxTotal(20);
            ConnectionUtil.jedisPool = new JedisPool(jedisPoolConfig, "8.141.64.164");

            // 获取一个连接
            logs += "\n> 获取一个黑板连接...";
            logsTextArea.setText(logs);
            jedis = ConnectionUtil.jedisPool.getResource();
            jedis.auth(ConnectionUtil.JEDIS_PASSWORD);

            // flushdb
            logs += "\n> 初始化数据库...";
            logsTextArea.setText(logs);
            jedis.flushDB();

            // mapView 0表示未探索，1表示已经探索过
            logs += "\n> 初始化地图...";
            logsTextArea.setText(logs);
            jedis.setbit("mapView", 399L, "0");

            // blockView 0表示不是障碍物，1表示是障碍物
            logs += "\n> 初始化障碍物...";
            logsTextArea.setText(logs);
            jedis.setbit("blockView", 399L, "0");

            jedis.close();

            // 启动计时器
            logs += "\n> 计时器启动...";
            logsTextArea.setText(logs);
            timer = new Timer(40, this);
            timer.start();

            // 小车大冒险没有结束
            logs += "\n> 小车大冒险开始...";
            logsTextArea.setText(logs);
            isFinish = false;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 画笔,画所有元素
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 画地图
//        for (int i = 0; i < 400; i++) {
//            if (null == jedis || !jedis.isConnected()) {
//                System.out.println("MainJPanel: jedis断开重连...");
//                jedis = ConnectionUtil.getJedis();
//            }
//            boolean flag = jedis.getbit("mapView", i);
//            if (flag) {// 已经探索过，画白色
//                g.setColor(Color.white);
//            } else {// 未探索，画黑色
//                g.setColor(Color.black);
//            }
//            g.fillRect((i % 20) * 32, (i / 20) * 32, 31, 31);
//        }
        for(int i=0;i<400;i++){
            switch (AllData.mapView[i]){
                case -1:
                    g.setColor(Color.WHITE);
                    break;
                case 0:
                    g.setColor(Color.black);
                    break;
                case 1:
                    g.setColor(Color.YELLOW);
                    break;
                case 2:
                    g.setColor(Color.GREEN);
                    break;
                case 3:
                    g.setColor(Color.BLUE);
                    break;
                case 4:
                    g.setColor(Color.MAGENTA);
                    break;
            }
            g.fillRect((i % 20) * 32, (i / 20) * 32, 31, 31);
        }

        // 画障碍物
        for (Block block : blockList) {
            block.getBlockIcon().paintIcon(this, g, block.getX() * 32, block.getY() * 32);
        }

        // 画车
        for (Car car : carList) {
            car.getCarIcon().paintIcon(this, g, (int)car.getX() * 32, (int)car.getY() * 32);
        }

        // TODO 画任务路线,读黑板得到
    }

    /**
     * 项目结束调用的方法
     */
    private void gameOver() {
        // 1.计时器停止
        timer.stop();

        // 2.汽车全部死亡
        for (Car car : carList) {
            car.setWork(false);
        }

        // 3.关掉jedis连接池
        logs += "\n> 关闭jedis连接池";
        changeLogsTextArea(logs);
        ConnectionUtil.jedisPool.destroy();

        // 4.打印日志
        logs += "\n> 小车大冒险结束";
        changeLogsTextArea(logs);
    }



    @Override
    public void actionPerformed(ActionEvent e) {
        // 小车大冒险还没有结束
        if (!isFinish) {
            jedis = ConnectionUtil.jedisPool.getResource();
            jedis.auth(ConnectionUtil.JEDIS_PASSWORD);
//            byte[] bytes = jedis.get("mapView").getBytes(StandardCharsets.UTF_8);
//            int count = 0;
//            while (count<400) {
//                for(Byte b : bytes){
//                    for(int i=7;i>=0;i--){
//                        AllData.mapView[count] = getBit(b,i);
//                        count++;
//                    }
//                }
//            }

            // 判断地图是否全部探索完，全部是1，bitcount有400个，则isFinish=true，计时器停止，汽车全部死亡，断开黑板连接，断开消息队列连接，打印日志
            boolean flag = jedis.bitcount("mapView", 0L, 49L) == 400L;
            jedis.close();
            if (flag) {
                isFinish = true;
                gameOver();
                JOptionPane.showMessageDialog(null,"小车全部探索完毕");
            }
            repaint();
        }
    }

//    /**
//     * byte转bit方法
//     */
//    private int getBit(byte b, int i) {
//        int bit = (int)((b>>i)&0x1);
//        return bit;
//    }
}
