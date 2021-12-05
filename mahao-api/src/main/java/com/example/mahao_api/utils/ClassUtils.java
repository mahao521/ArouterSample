package com.example.mahao_api.utils;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import dalvik.system.DexFile;

public class ClassUtils {

    private static final String EXTRACTIED_EXT = ".classes";
    private static final String EXTRACTIED_SUFFIX = ".zip";

    public static Set<String> getFileNameByPackageName(Context context, final String packageName) {

        final Set<String> classNames = new HashSet<>();
        List<String> paths = getSourcePaths(context);
        final CountDownLatch countDownLatch = new CountDownLatch(paths.size());
        for (String path : paths) {
            DefaultPoolExecutor.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    DexFile dexFile = null;
                    try {
                        if (path.endsWith(EXTRACTIED_SUFFIX)) {
                            dexFile = DexFile.loadDex(path, path + ".tmp", 0);
                        } else {
                            dexFile = new DexFile(path);
                        }
                        Enumeration<String> dexEntries = dexFile.entries();
                        while (dexEntries.hasMoreElements()) {
                            String className = dexEntries.nextElement();
                            if (className.startsWith(packageName)) {
                                classNames.add(className);
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    } finally {
                        if (null != dexFile) {
                            try {
                                dexFile.close();
                            } catch (Exception e) {
                            }
                        }
                        countDownLatch.countDown();
                    }
                }
            });
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return  classNames;
    }

    public static List<String> getSourcePaths(Context context) {
        List<String> sourcePaths = new ArrayList<>();
        try {
            ApplicationInfo application = context.getPackageManager().getApplicationInfo(context.getPackageName(), 0);
            File sourceApk = new File(application.sourceDir);
            sourcePaths.add(application.sourceDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sourcePaths;
    }


}
