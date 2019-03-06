package com.yulich.fasttitlebar;

import android.os.Bundle;

import com.yulich.titlebar.widget.TitleBar;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        TitleBar bar = findViewById(R.id.title_bar);
        bar.addImageBtnToLeft(this, R.drawable.back);
        bar.addTextBtnToRight(this, "Edit");
    }
}
