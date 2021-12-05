package com.example.mahao_api;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.example.mahao_api.callback.NavigationCallback;
import com.example.mahao_api.core.AutoWiredServiceImpl;
import com.example.mahao_api.core.LogisticsCenter;
import com.example.mahao_api.service.AutoWireService;
import com.example.mahao_api.utils.DefaultPoolExecutor;

import java.util.concurrent.ThreadPoolExecutor;

public class Arouter {

    private volatile static Arouter instance = null;
    private static Context mContext;
    private static Handler handler;
    private volatile static ThreadPoolExecutor executor = DefaultPoolExecutor.getInstance();

    private Arouter() {
    }

    public static void init(Application application) {
        mContext = application;
        handler = new Handler(Looper.getMainLooper());
        LogisticsCenter.init(mContext, executor);
        handler = new Handler(Looper.getMainLooper());
    }

    public static Arouter getInstance() {
        if (instance == null) {
            synchronized (Arouter.class) {
                if (instance == null) {
                    instance = new Arouter();
                }
            }
        }
        return instance;
    }

    public PostCard build(String path) {
        return new PostCard(path, extraGroup(path));
    }

    //provider
    public <T> T navigation(Class<? extends T> service) {
        try {
            PostCard postCard = LogisticsCenter.builderProvider(service.getName());
            if (null == postCard) {
                return null;
            }
            postCard.setContext(mContext);
            LogisticsCenter.completion(postCard);
            return (T) postCard.getProvider();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //activity
    protected Object navigation(final Context context, final PostCard postCard, final NavigationCallback navigationCallback) {
        postCard.setContext(context == null ? mContext : context);
        LogisticsCenter.completion(postCard);
        switch (postCard.getType()) {
            case ACTIVITY:
                final Intent intent = new Intent(postCard.getContext(), postCard.getDestination());
                intent.putExtras(postCard.getBundle());
                if (!(postCard.getContext() instanceof Activity)) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                runInMainThread(new Runnable() {
                    @Override
                    public void run() {
                        postCard.getContext().startActivity(intent);
                    }
                });
                break;
            case PROVIDER:
                return postCard.getProvider();
        }
        return null;
    }

    private void runInMainThread(Runnable runnable) {
        if (Looper.getMainLooper().getThread() != Thread.currentThread()) {
            handler.post(runnable);
        } else {
            runnable.run();
        }
    }


    private String extraGroup(String path) throws RuntimeException {
        if (TextUtils.isEmpty(path) || !path.startsWith("/")) {
            throw new RuntimeException("path error");
        }
        try {
            String defaultGroup = path.substring(1, path.indexOf("/", 1));
            if (TextUtils.isEmpty(defaultGroup)) {
                throw new Exception("path error");
            } else {
                return defaultGroup;
            }
        } catch (Exception e) {
            return null;

        }
    }

    public void inject(Object object) {
        //  AutoWiredServiceImpl navigation1 = (AutoWiredServiceImpl) Arouter.getInstance().navigation(AutoWireService.class);
        AutoWireService navigation = (AutoWireService) Arouter.getInstance().build("/lisi/service/autowired").navigation();
        navigation.autoWire(object);
    }
}





