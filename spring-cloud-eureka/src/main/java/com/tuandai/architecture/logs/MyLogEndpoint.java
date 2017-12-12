package com.tuandai.architecture.logs;

import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.boot.actuate.endpoint.AbstractEndpoint;

public class MyLogEndpoint extends AbstractEndpoint<Boolean> {

    AtomicBoolean atomicBoolean = new AtomicBoolean();

    // mgrlogging 为该 endpoint 的请求子路径：  http://localhost:8080/mgrlogging
    public MyLogEndpoint() {
        super("mgrlogging", true, true);
    }

    @Override
    public Boolean invoke() {
        // 这里我的本意是 log 开关，一个布尔值。
        // 每次请求 http://localhost:8080/mgrlogging 都会调用该 invoke 方法
        // 通常情况下返回该 endpoint 的监控信息
        return atomicBoolean.getAndSet(!atomicBoolean.get());
    }

}