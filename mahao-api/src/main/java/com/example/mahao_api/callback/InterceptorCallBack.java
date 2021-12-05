package com.example.mahao_api.callback;

import com.example.mahao_api.PostCard;

public interface InterceptorCallBack {

    void onContinue(PostCard postCard);

    void onInterrupt(Throwable throwable);

}
