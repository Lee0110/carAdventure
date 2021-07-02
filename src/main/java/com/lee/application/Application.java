package com.lee.application;

import com.lee.factory.ControllerFactory;
import com.lee.navigator.Navigator;

import java.util.Scanner;

public class Application {
    public static void main(String[] args) {
        ControllerFactory controllerFactory = new ControllerFactory(true);
//        CarTaskJudgeFactory carTaskJudgeFactory = new CarTaskJudgeFactory(true);
        Navigator navigator1 = new Navigator(true);
        Navigator navigator2 = new Navigator(true);
        Navigator navigator3 = new Navigator(true);
        Navigator navigator4 = new Navigator(true);

        controllerFactory.start();
//        carTaskJudgeFactory.start();
        navigator1.start();
        navigator2.start();
        navigator3.start();
        navigator4.start();

        Scanner scanner = new Scanner(System.in);
        if (scanner.hasNext()){
            controllerFactory.setWork(false);
//            carTaskJudgeFactory.setWork(false);
            navigator1.setWork(false);
            navigator2.setWork(false);
            navigator3.setWork(false);
            navigator4.setWork(false);
        }
    }
}
