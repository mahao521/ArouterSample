package com.example.mahao_api;

import android.content.Context;
import android.os.Bundle;

import com.example.mahao_annotation.model.RouteMeta;
import com.example.mahao_api.callback.NavigationCallback;
import com.example.mahao_api.service.SerializationService;
import com.example.mahao_api.templete.IProvider;

import java.io.Serializable;

public class PostCard extends RouteMeta {

    private Bundle bundle;
    private IProvider provider;
    private SerializationService serializationService;
    private Context context;

    public PostCard() {
        this(null, null);
    }

    public PostCard(String path, String group) {
        this(path, group, null);
    }

    public PostCard(String path, String group, Bundle bundle) {
        setPath(path);
        setGroup(group);
        this.bundle = (null == bundle ? new Bundle() : bundle);
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Bundle getBundle() {
        return bundle;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    public IProvider getProvider() {
        return provider;
    }

    public void setProvider(IProvider provider) {
        this.provider = provider;
    }

    public SerializationService getSerializationService() {
        return serializationService;
    }

    public void setSerializationService(SerializationService serializationService) {
        this.serializationService = serializationService;
    }

    public Object navigation() {
        return navigation(null);
    }

    public Object navigation(Context context) {
        return navigation(context, null);
    }

    public Object navigation(Context context, NavigationCallback callback){
        return Arouter.getInstance().navigation(context,this,callback);
    }

    public PostCard withString(String key,String value){
        bundle.putString(key,value);
        return this;
    }

    public PostCard withBoolean(String key,boolean value){
        bundle.putBoolean(key,value);
        return this;
    }

    public PostCard withInt(String key,int value){
        bundle.putInt(key,value);
        return this;
    }
}
