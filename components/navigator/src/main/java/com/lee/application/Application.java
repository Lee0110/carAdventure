package com.lee.application;

import com.lee.navigator.Navigator;
import java.util.Scanner;

public class Application {
    public static void main(String[] args) {
        Navigator navigator1 = new Navigator(true);
        Navigator navigator2 = new Navigator(true);
        Navigator navigator3 = new Navigator(true);
        Navigator navigator4 = new Navigator(true);

        navigator1.start();
        navigator2.start();
        navigator3.start();
        navigator4.start();

        Scanner scanner = new Scanner(System.in);
        if (scanner.hasNext()){
            navigator1.setWork(false);
            navigator2.setWork(false);
            navigator3.setWork(false);
            navigator4.setWork(false);
        }
    }
}
