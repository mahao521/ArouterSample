package com.example.photo.ui.login;

import android.content.Context;
import android.util.Log;

import com.example.exportmoudler.HelloService;
import com.example.mahao_annotation.annotation.Route;

@Route(path = "/mahao/sayhello")
public class SayHello implements HelloService {

    private Context mContext;

    @Override
    public void init(Context context) {
        this.mContext = context;
    }

    @Override
    public void sayHello() {
        Log.d("mahao", "sayhello");
    }
}
