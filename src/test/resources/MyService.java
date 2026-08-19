package com.example;

import java.lang.annotation.Native;

public class MyService {

    @Native
    public void someMethod(int num) {
        System.out.println(num);
    }

    public void someNewMethod(String arg) {
        System.out.println(arg);
    }
}
