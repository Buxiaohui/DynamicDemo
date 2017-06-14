package com.bxh.dynamicdemo.dynamicproxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by buxiaohui on 6/14/17.
 */

public class MyProxyHandler implements InvocationHandler {
    private Object obj;

    public MyProxyHandler() {
    }

    public MyProxyHandler(Object obj) {
        this.obj = obj;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
        System.out.println("before calling " + method);

        try {

            result = method.invoke(obj, args);
        } catch (Exception e) {
            System.out.println("calling exception " + e.toString());
        }

        System.out.println("after calling " + method);

        return result;
    }


}
