package com.talon.screen.quick.util;

import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author by Talon, Date on 19/1/22.
 * note: 线程的优化，线程池
 */
public class ThreadPool {

    private static final String TAG = "ThreadProxy";
    private final Executor executor;
    private final ExecutorService singleThreadExecutor;

    static class InnerClass {
        static ThreadPool instance = new ThreadPool();
    }

    private ThreadPool() {

        executor = new ThreadPoolExecutor(2, 8,
                15, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(Integer.MAX_VALUE), new ThreadFactory() {
            @Override
            public Thread newThread(@NonNull Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("ThreadProxy");
                return thread;
            }
        });
        singleThreadExecutor = Executors.newSingleThreadExecutor();
    }

    public void execute(final Runnable run) {

        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    run.run();
                }catch (Exception e){
                    if(LogWrapper.DEBUG){
                        e.printStackTrace();
                    }
                    LogWrapper.e(TAG,"failed to run task "+e.getMessage());
                }
            }
        });

    }

    public void executeInSingle(final Runnable run) {
        singleThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    run.run();
                }catch (Exception e){
                    LogWrapper.d(TAG,"failed to run task "+e.getMessage());
                }
            }
        });
    }

    public static ThreadPool getInstance() {
        return InnerClass.instance;
    }

}
