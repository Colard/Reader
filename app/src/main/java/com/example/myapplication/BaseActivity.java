package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.myapplication.SupportClasses.States;

public abstract class BaseActivity extends AppCompatActivity {

    protected abstract int getLayoutResourceId();



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());

        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                View.SYSTEM_UI_FLAG_FULLSCREEN );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater findMenuItems = getMenuInflater();
        findMenuItems.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        States.bookListType type;

        switch(id){
            case R.id.reading :
                type = States.bookListType.read;
                break;
            case R.id.in_the_plans:
                type = States.bookListType.plans;
                break;
            case R.id.has_been_read:
                type = States.bookListType.readed;
                break;
            case R.id.favorite:
                type = States.bookListType.favorite;
                break;
            default:
                type = States.bookListType.all;
                break;
        }

        if(this.getClass() == BookListMenuActivity.class) {
            BookListFragment fragment = BookListFragment.newInstance(type);
            changeFragment(type, BookListMenuActivity.class, fragment, R.id.book_list_fragment);
        } else {
            Intent intent = new Intent(this, BookListMenuActivity.class);
            intent.putExtra("type", type);
            startActivity(intent);
            overridePendingTransition(R.anim.swipe_to_up, R.anim.no_animation);
        }

        return super.onOptionsItemSelected(item);
    }

    public void changeFragment(States.bookListType type, Class<? extends BaseActivity> activity,
                               Fragment fragment, int place) {
        if(States.thisFragment == type && this.getClass() == activity) return;
        writeFragmentType(type);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_in_right);
        ft.replace(place, fragment);
        ft.commit();
    }

    protected void writeFragmentType(States.bookListType type) {
        States.thisFragment = type;
    }
}