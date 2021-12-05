package com.example.mahao_api.service;

import com.example.mahao_api.PostCard;
import com.example.mahao_api.callback.InterceptorCallBack;
import com.example.mahao_api.templete.IProvider;

public interface InterceptorService  extends IProvider {

    void doInterceptions(PostCard postCard, InterceptorCallBack callBack);
}
