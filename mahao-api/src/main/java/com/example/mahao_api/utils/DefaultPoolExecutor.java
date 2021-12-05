package com.example.mahao_api.utils;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DefaultPoolExecutor extends ThreadPoolExecutor {

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int INIT_THREAD_COUNT = CPU_COUNT + 1;
    private static final int MAX_THREAD_COUNT = INIT_THREAD_COUNT;
    private static final long KEEP_ALIVE_TIME = 30L;

    private  static volatile  DefaultPoolExecutor instance;

    public static DefaultPoolExecutor getInstance(){
        if(null == instance){
            synchronized (DefaultPoolExecutor.class){
                if(null == instance){
                    instance = new DefaultPoolExecutor(INIT_THREAD_COUNT,MAX_THREAD_COUNT,KEEP_ALIVE_TIME,
                            TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(64),new DefaultThreadFactory());
                }
            }
        }
        return instance;
    }

    public DefaultPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }


}
