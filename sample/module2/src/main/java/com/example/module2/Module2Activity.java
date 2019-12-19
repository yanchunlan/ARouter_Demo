package com.example.module2;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lib_annotation.Path;
import com.example.lib_core.ARouterCons;

@Path({ARouterCons.module2_module2main,ARouterCons.module2_module22main})
public class Module2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module2);
    }
}
