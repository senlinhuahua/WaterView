package com.forest.waterview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.forest.waterview.view.WaterView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WaterView waterView = findViewById(R.id.waterview);
        waterView.startAniation();
    }
}
