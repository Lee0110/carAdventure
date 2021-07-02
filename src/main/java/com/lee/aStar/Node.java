package com.lee.aStar;

public class Node implements Comparable<Node> {
    private int x;// x坐标
    private int y;// y坐标
    private int F;// 综合花费的步数 F = G + H
    private int G;// 已经花费的步数
    private int H;// 将要花费的步数
    private Node father;// 这个结点的上一个结点

    public Node(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getF() {
        return F;
    }

    public void setF(int f) {
        F = f;
    }

    public int getG() {
        return G;
    }

    public void setG(int g) {
        G = g;
    }

    public int getH() {
        return H;
    }

    public void setH(int h) {
        H = h;
    }

    public Node getFather() {
        return father;
    }

    public void setFather(Node father) {
        this.father = father;
    }

    /**
     * 通过结点的目标和目标结点,计算出F,G,H三个属性
     */
    public void initNode(Node father,Node dest){
        this.father = father;
        if (this.father != null){// 走过的步数G是父节点加1
            this.G = father.getG() + 1;
        }else {// 如果父节点是空,说明当前节点是第一个结点
            this.G = 0;
        }

        this.H = Math.abs(this.x - dest.getX()) + Math.abs(this.y - dest.getY());
        this.F = this.G + this.H;
    }

    @Override
    public int compareTo(Node o) {
        return Integer.compare(this.F,o.getF());
    }
}
