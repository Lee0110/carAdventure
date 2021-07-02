package com.lee.entity;

import javax.swing.*;

public class Block {

    private int x;// x坐标
    private int y;// y坐标
    private ImageIcon blockIcon;// 图片

    public Block(int x, int y) {
        super();
        this.x = x;
        this.y = y;
    this.blockIcon =
        new ImageIcon("D:\\javaWorkSpace_idea\\carAdventureView\\images\\block.png");
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

    public ImageIcon getBlockIcon() {
        return blockIcon;
    }
}
