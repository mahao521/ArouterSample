package com.example.mahao_api.core;

import android.content.Context;

import com.example.mahao_annotation.model.RouteMeta;
import com.example.mahao_api.Consts;
import com.example.mahao_api.PostCard;
import com.example.mahao_api.templete.IProvider;
import com.example.mahao_api.templete.IProviderGroup;
import com.example.mahao_api.templete.IRouteGroup;
import com.example.mahao_api.templete.IRouteRoot;
import com.example.mahao_api.utils.ClassUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

public class LogisticsCenter {

    private static Context context;
    static ThreadPoolExecutor executor;
    private static boolean registerPlugin;

    public synchronized static void init(Context context, ThreadPoolExecutor executor) {
        context = context;
        executor = executor;
        if (registerPlugin) {

        } else {
            try {
                Set<String> routerMap = ClassUtils.getFileNameByPackageName(context, Consts.ROUTE_ROOT_PACKAGE);
                for (String className : routerMap) {
                    if (className.startsWith(Consts.ROUTE_ROOT_PACKAGE + ".ARouter_Root")) {
                        ((IRouteRoot) Class.forName(className).getConstructor().newInstance()).loadInto(WareHouse.groupsIndex);
                    } else if (className.startsWith(Consts.ROUTE_ROOT_PACKAGE + ".ARouter_Provider")) {
                        ((IProviderGroup) (Class.forName(className).getConstructor().newInstance())).loadInto(WareHouse.providerIndex);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void suspend(){
        WareHouse.clear();
    }

    public synchronized static void completion(PostCard postCard){
        if(null == postCard){
            throw  new RuntimeException("no postCard");
        }
        RouteMeta routeMeta = WareHouse.routeMetaMap.get(postCard.getPath());
        if(null == routeMeta){
            if(!WareHouse.groupsIndex.containsKey(postCard.getGroup())){
                throw new RuntimeException("no postcard");
            }else {
                addRouteGroupDynamic(postCard.getGroup(),null);
            }
           completion(postCard);
        }else {
            postCard.setDestination(routeMeta.getDestination());
            postCard.setType(routeMeta.getType());
            postCard.setPriority(routeMeta.getPriority());
            switch (routeMeta.getType()){
                case PROVIDER:
                    Class<? extends IProvider> providerMeta = (Class<? extends IProvider>) routeMeta.getDestination();
                    IProvider instance = WareHouse.providerMap.get(providerMeta);
                    if(instance == null){
                        IProvider provider;
                        try {
                            provider = providerMeta.getConstructor().newInstance();
                            provider.init(context);
                            WareHouse.providerMap.put(providerMeta,provider);
                            instance = provider;
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    postCard.setProvider(instance);
                    break;
            }
        }
    }

    public static PostCard builderProvider(String className){
        RouteMeta routeMeta = WareHouse.providerIndex.get(className);
        if(routeMeta== null){
            return  null;
        }else {
            return  new PostCard(routeMeta.getPath(),routeMeta.getGroup());
        }
    }

    public synchronized static void addRouteGroupDynamic(String groupName, IRouteGroup group){
        if(WareHouse.groupsIndex.containsKey(groupName)){
            try {
                WareHouse.groupsIndex.get(groupName).getConstructor().newInstance().loadInto(WareHouse.routeMetaMap);
                WareHouse.groupsIndex.remove(groupName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(null != group){
            group.loadInto(WareHouse.routeMetaMap);
        }
    }

}









