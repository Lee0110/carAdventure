package com.lee.carTaskJudge;

import com.lee.utils.ConnectionUtil;
import redis.clients.jedis.Jedis;

public class CarTaskJudge extends Thread{

    private boolean isWork;
    private Jedis jedis;
    private String carId = "";

    public CarTaskJudge(boolean isWork,String carId) {
        try {
            this.isWork = isWork;
            this.carId = carId;
            jedis = ConnectionUtil.getJedis();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setWork(boolean work) {
        isWork = work;
    }


    @Override
    public void run() {

        try {

            System.out.printf("CarTaskJudge: 为小车%s服务",carId);

            while(isWork){
                String task = jedis.hget(carId,"task");

                if (!"".equals(task)){
                    int x = Integer.parseInt(jedis.hget(carId,"x"));
                    int y = Integer.parseInt(jedis.hget(carId,"y"));
                    // 计算终点
                    for(int i=0;i<task.length();i++){
                        String t = task.substring(i,i+1);
                        switch (t){
                            case "U":
                                if(y>0){
                                    y -= 1;
                                }
                                break;
                            case "D":
                                if (y<19) {
                                    y += 1;
                                }
                                break;
                            case "L":
                                if (x>0) {
                                    x -= 1;
                                }
                                break;
                            case "R":
                                if (x<19) {
                                    x += 1;
                                }
                                break;
                        }
                    }
                    //System.out.printf("CarTaskJudge: 现在开始判断小车%s的终点(%d,%d)是否被点亮...\n",carId,y,x);
                    if (jedis.getbit("mapView",y*20+x)||jedis.getbit("blockView",y*20+x)){
                        //System.out.printf("CarTaskJudge: (%d,%d)已经点亮,将小车任务清空...\n",y,x);
                        // jedis.hset(carId,"task","");
                    }
                }
                Thread.sleep(400);
                Thread.yield();
            }

            jedis.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
