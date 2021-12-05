package com.example.aroutersample;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.exportmoudler.HelloService;
import com.example.mahao_annotation.annotation.Route;
import com.example.mahao_api.Arouter;
import com.example.photo.ui.login.SayHello;

@Route(path = "/zhangsan/activity")
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_0).setOnClickListener(this);
        findViewById(R.id.btn_1).setOnClickListener(this);
        findViewById(R.id.btn_2).setOnClickListener(this);
        findViewById(R.id.btn_3).setOnClickListener(this);
        Arouter.getInstance().init(getApplication());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_0:
                Arouter.getInstance().init(getApplication());
                break;
            case R.id.btn_1:
                Arouter.getInstance().build("/mahao/login").withString("name","zhangsan").navigation();
                break;
            case R.id.btn_2:
                Arouter.getInstance().navigation(SayHello.class).sayHello();
             //   new SayHello().sayHello();
                break;
            case R.id.btn_3:
                Arouter.getInstance().build("/share/lifes").navigation();
                break;
        }
    }
}