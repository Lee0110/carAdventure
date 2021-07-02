package com.lee.application;

import com.lee.factory.ControllerFactory;
import java.util.Scanner;

public class Application {
    public static void main(String[] args) {
        ControllerFactory controllerFactory = new ControllerFactory(true);
      
        controllerFactory.start();

        Scanner scanner = new Scanner(System.in);
      
        if (scanner.hasNext()){
            controllerFactory.setWork(false);
        }
    }
}
