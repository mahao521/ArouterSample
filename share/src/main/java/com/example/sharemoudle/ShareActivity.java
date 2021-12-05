package com.example.sharemoudle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.mahao_annotation.annotation.Route;
import com.example.mahao_api.Arouter;

@Route(path = "/share/lifes")
public class ShareActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        findViewById(R.id.tv_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Arouter.getInstance().build("/mahao/login").withString("name","那就好好学习吧").navigation();
           //    Arouter.getInstance().navigation(HelloService)
            }
        });
    }
}