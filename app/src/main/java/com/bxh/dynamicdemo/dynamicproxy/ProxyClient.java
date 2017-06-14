package com.bxh.dynamicdemo.dynamicproxy;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by buxiaohui on 6/14/17.
 */

public class ProxyClient {
    public void test() {
        IProxy rs = new MyProxy();//这里指定被代理类
        InvocationHandler ds = new MyProxyHandler(rs);
        Class<?> cls = rs.getClass();

        //以下是一次性生成代理
        IProxy subject = (IProxy) Proxy.newProxyInstance(
                cls.getClassLoader(), cls.getInterfaces(), ds);


        /****************************/
        //这里可以通过运行结果证明subject是Proxy的一个实例，这个实例实现了Subject接口
        System.out.println(subject instanceof Proxy);

        //这里可以看出subject的Class类是$Proxy0,这个$Proxy0类继承了Proxy，实现了Subject接口
        System.out.println("subject的Class类是："+subject.getClass().toString());

        System.out.println("rs.getClass="+cls.getCanonicalName());

        Class<?>[] interfaces =  cls.getInterfaces();
        if(interfaces != null){
            System.out.println("rs 的接口有：");
            for (int i = 0; i < interfaces.length; i++) {
                System.out.println(interfaces[i].getCanonicalName());
            }
        }else {
            System.out.println("rs 的接口  null!");
        }

        System.out.println("subject中的属性有：");

        Field[] field=subject.getClass().getDeclaredFields();
        for(Field f:field){
            System.out.print(f.getName()+", ");
        }

        System.out.println("\n"+"subject中的方法有：");

        Method[] method=subject.getClass().getDeclaredMethods();

        for(Method m:method){
            System.out.print(m.getName()+", ");
        }

        System.out.println("\n"+"subject的父类是："+subject.getClass().getSuperclass());

        System.out.println("\n"+"subject实现的接口是：");

        Class<?>[] subjectInterfaces=subject.getClass().getInterfaces();

        for(Class<?> i:subjectInterfaces){
            System.out.print(i.getName()+", ");
        }

        /****************************/
        System.out.println("\n\n"+"运行结果为：");
        int result = subject.requestAndReturn("1234");
        System.out.println("\n\n"+"运行结果为："+result);
        subject.request("ahahahahahhaha");
    }
}
