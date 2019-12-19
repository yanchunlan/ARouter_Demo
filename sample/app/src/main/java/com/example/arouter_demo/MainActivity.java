package com.example.arouter_demo;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lib_core.ARouter;
import com.example.lib_core.ARouterCons;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void toModule1(View view) {
        ARouter.getInstance().jumpActivity(ARouterCons.module1_module1main,null);
    }

    public void toModule2(View view) {
        ARouter.getInstance().jumpActivity(ARouterCons.module2_module2main,null);
    }
}
