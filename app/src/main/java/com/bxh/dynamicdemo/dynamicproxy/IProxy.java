package com.bxh.dynamicdemo.dynamicproxy;

/**
 * Created by buxiaohui on 6/14/17.
 */

public interface IProxy {
    int requestAndReturn(String arg);
    void request(String arg);
}
