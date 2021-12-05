package com.example.mahao_api.core;

import com.example.mahao_annotation.model.RouteMeta;
import com.example.mahao_api.templete.IProvider;
import com.example.mahao_api.templete.IRouteGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WareHouse {

    //cache route metas
    static Map<String,Class<? extends IRouteGroup>> groupsIndex = new HashMap<>();
    static Map<String, RouteMeta> routeMetaMap = new HashMap<>();

    //cache provider
    static Map<Class, IProvider> providerMap = new HashMap<>();
    static Map<String,RouteMeta> providerIndex = new HashMap<>();

    //cache Interceptor
    //static Map<Integer, Class<? extends IInterceptor>> interceptorsIndex = new UniqueKeyTreeMap<>("More than one interceptors use same priority [%s]");
    //static List<IInterceptor> interceptors = new ArrayList<>();

    static void clear(){
        routeMetaMap.clear();
        groupsIndex.clear();
        providerIndex.clear();
        providerMap.clear();
    }

}
