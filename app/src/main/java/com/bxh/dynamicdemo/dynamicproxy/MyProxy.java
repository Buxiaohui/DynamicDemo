package com.bxh.dynamicdemo.dynamicproxy;

/**
 * Created by buxiaohui on 6/14/17.
 */

public class MyProxy implements IProxy {
    @Override
    public int requestAndReturn(String arg) {
        System.out.println("MyProxy requestAndReturn arg=" + arg);
        try {
            return Integer.parseInt(arg);
        }catch (Exception e){

        }
        return 0;
    }

    @Override
    public void request(String arg) {
        System.out.println("MyProxy request arg=" + arg);
    }
}
