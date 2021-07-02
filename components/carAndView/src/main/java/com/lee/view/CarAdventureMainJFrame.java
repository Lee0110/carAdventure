package com.lee.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class CarAdventureMainJFrame extends JFrame {

    private JPanel contentPane;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    CarAdventureMainJFrame frame = new CarAdventureMainJFrame();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public CarAdventureMainJFrame() {
        ImageIcon icon = new ImageIcon("D:\\javaWorkSpace_idea\\carAdventure2\\images\\carUp.png");
        setIconImage(icon.getImage());
        setTitle("小车大冒险");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(20, 20, 820, 690);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);
        MainJPanel mainJPanel = new MainJPanel();
        getContentPane().add(mainJPanel);
    }

}
