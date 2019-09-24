package com.example.module1;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lib_annotation.Path;
import com.example.lib_core.ARouterCons;

@Path(path = ARouterCons.module1_module1main)
public class Module1Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module1);
    }
}
