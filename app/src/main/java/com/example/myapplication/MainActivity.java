package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;
import android.os.Bundle;

import com.example.myapplication.SupportClasses.States;

public class MainActivity extends BaseActivity {
    @Override
    protected int getLayoutResourceId(){
        return R.layout.activity_main;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    public void startWorkActivity(View v) {
        Intent intent = new Intent(this, BookListMenuActivity.class);
        intent.putExtra("type", States.bookListType.all);
        startActivity(intent);
        overridePendingTransition(R.anim.swipe_to_up, R.anim.no_animation);
    }

}