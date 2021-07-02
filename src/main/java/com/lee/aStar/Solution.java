package com.lee.aStar;

import com.lee.utils.ConnectionUtil;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.PriorityQueue;

public class Solution {

    private PriorityQueue<Node> open = new PriorityQueue<Node>();
    private ArrayList<Node> close = new ArrayList<Node>();
    private ArrayList<Node> exist = new ArrayList<Node>();
    private int[][] map;

    public PriorityQueue<Node> getOpen() {
        return open;
    }

    public void setOpen(PriorityQueue<Node> open) {
        this.open = open;
    }

    public ArrayList<Node> getClose() {
        return close;
    }

    public void setClose(ArrayList<Node> close) {
        this.close = close;
    }

    public ArrayList<Node> getExist() {
        return exist;
    }

    public void setExist(ArrayList<Node> exist) {
        this.exist = exist;
    }

    public int[][] getMap() {
        return map;
    }

    public void setMap(int[][] map) {
        this.map = map;
    }

    /**
     * 判断结点是否出现过
     */
    private boolean isExist(Node node){
        for (Node node1 : exist) {
            if (node.getX() == node1.getX() && node.getY() == node1.getY()){
                return true;
            }
        }
        return false;
    }

    /**
     * 判断结点是否合法
     */
    private boolean isValid(int x,int y){
        if (map[x][y] == 1){
            return false;
        }
        for (Node node : exist) {
            if (isExist(new Node(x,y))){
                return false;
            }
        }
        return true;
    }

    /**
     * 返回合法的邻结点
     */
    private ArrayList<Node> extendCurrentNode(Node currentNode){
        int x = currentNode.getX();
        int y = currentNode.getY();
        ArrayList<Node> neighbourNode = new ArrayList<Node>();

        if (isValid(x + 1, y)){
            Node node = new Node(x + 1, y);
            neighbourNode.add(node);
        }
        if (isValid(x - 1, y)){
            Node node = new Node(x - 1, y);
            neighbourNode.add(node);
        }
        if (isValid(x, y + 1)){
            Node node = new Node(x, y + 1);
            neighbourNode.add(node);
        }
        if (isValid(x, y - 1)){
            Node node = new Node(x, y - 1);
            neighbourNode.add(node);
        }

        return neighbourNode;
    }

    private Node aStarSearch(Node start,Node end){
        this.open.add(start);
        this.exist.add(start);

        while(open.size() > 0){
            // 拿到顶部元素,并从open表中删除
            Node currentNode = open.poll();

            // 将这个结点加入到close表
            this.close.add(currentNode);

            // 对当前结点进行扩展,返回邻居结点表
            ArrayList<Node> neighbourNode = extendCurrentNode(currentNode);

            // 遍历这个数组,看是否有目标结点出现
            for (Node node : neighbourNode) {
                if (node.getX() == end.getX() && node.getY() == end.getY()){
                    node.initNode(currentNode,end);
                    return node;
                }
                if (!isExist(node)){
                    // 对于未出现的结点加入到open表并且设置父节点,计算F,G,H
                    node.initNode(currentNode,end);
                    open.add(node);
                    exist.add(node);
                }
            }
        }

        return null;
    }

    /**
     * 得到一步
     */
    private String getOneStep(Node start,Node end){
        String step = "";
        if (null == end || null == start){
            return step;
        }else {
            if (start.getX() + 1 == end.getX()){
                step = "U";
            }else if(start.getX() - 1 == end.getX()){
                step = "D";
            }else if(start.getY() + 1 == end.getY()){
                step = "L";
            }else if(start.getY() - 1 == end.getY()){
                step = "R";
            }
        }
        return step;
    }

    /**
     * 得到整个路径
     */
    public String getPath(int carX,int carY,int endX,int endY) {
        Jedis jedis = ConnectionUtil.getJedis();

        // 初始化地图
        int[][] map = new int[22][22];
        for(int i = 0;i<22;i++){
            map[0][i] = 1;
            map[i][0] = 1;
            map[21][i] = 1;
            map[i][21] = 1;
        }

        // 读取障碍物
        for (int offset=0; offset<400;offset++){
            if (jedis.getbit("blockView",offset)){
                map[offset/20+1][offset%20+1] = 1;
            }
        }

        Node start = new Node(carY+1,carX+1);
        start.setFather(null);
        Node end = new Node(endY+1,endX+1);
        setMap(map);

        Node resNode = aStarSearch(start,end);

        int count = 0;
        String path = "";
        while (resNode != null){
            map[resNode.getX()][resNode.getY()] = 88;
            Node currentNode = resNode;
            //System.out.printf("%d: x:%d y:%d\n",count,currentNode.getX(),currentNode.getY());
            resNode = resNode.getFather();
            if(resNode!=null){
                System.out.printf("%d: x:%d y:%d\n",count,resNode.getX(),resNode.getY());
            }
            count++;
            path = getOneStep(currentNode,resNode)+path;
        }
        System.out.println(path);
        // 画出来
        for (int i = 0; i < 22; i++){
            for (int j = 0; j < 22; j++)
            {
                System.out.printf("%3d", map[i][j]);
            }
            System.out.println();
        }
        return path;
    }
}
