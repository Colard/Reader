package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startWorkActivity(View v) {
        Intent intent = new Intent(this, BookListMenuActivity.class);
        intent.putExtra("type", "all");
        startActivity(intent);
        overridePendingTransition(R.anim.swipe_to_up, R.anim.no_animation);
    }

}