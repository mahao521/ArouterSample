package com.example.mahao_api.utils;

import androidx.annotation.NonNull;

import com.example.mahao_api.Arouter;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultThreadFactory implements ThreadFactory {

    private static final AtomicInteger poolNumber = new AtomicInteger(1);
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private ThreadGroup group;
    private String namePrefix;

    public DefaultThreadFactory() {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        namePrefix = "ARouter task pool No" + poolNumber.getAndIncrement() + ", thread No";
    }

    @Override
    public Thread newThread(Runnable r) {
        String threadName = namePrefix + threadNumber.getAndIncrement();
        Thread thread = new Thread(group,r,threadName,0);
        //设置为非后台线程
        if(thread.isDaemon()){
            thread.setDaemon(false);
        }
        //优先级设置为normal
        if(thread.getPriority() != Thread.NORM_PRIORITY){
            thread.setPriority(Thread.NORM_PRIORITY);
        }
        //捕获异常
        thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
            }
        });
        return thread;
    }
}







