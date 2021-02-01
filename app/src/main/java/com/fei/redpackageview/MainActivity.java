package com.fei.redpackageview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final RedPackageView redPackageView = findViewById(R.id.red_package_view);
        redPackageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redPackageView.setCurrentProgress(1,4);
            }
        });
    }
}