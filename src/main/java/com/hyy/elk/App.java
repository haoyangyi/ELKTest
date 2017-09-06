package com.hyy.elk;

import org.apache.log4j.Logger;

/**
 * log4j往logstash输入日志
 * 然后logstash往kafka输入日志
 * Created by root on 2017/6/8.
 */
public class App {
    private static final Logger LOGGER = Logger.getLogger(App.class);

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 100000; i++) {
            LOGGER.info("Test [" + i + "] Hello");
        }

        System.out.println("end");
    }
}
