package com.example.myapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.myapplication.SupportClasses.States;

public class BookListMenuActivity extends BaseActivity {

    @Override
    protected int getLayoutResourceId(){
        return R.layout.activity_book_list_menu;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        States.bookListType arguments = (States.bookListType) getIntent().getSerializableExtra("type");

        boolean isFirstStart = true;

        if (savedInstanceState != null) {
            isFirstStart = savedInstanceState.getBoolean("isFirstStart");
        }

        if(!isFirstStart) return;

        if(arguments != null) {
            ChangeType(arguments);
        } else {
            ChangeType(States.bookListType.all);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean("isFirstStart", false);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed()
    {
        ChangeType(States.bookListType.all);
    }

    public void moveToFavorite(View v) {
        ChangeType(States.bookListType.favorite);
    }

    public void moveToAllBooks(View v) {
        ChangeType(States.bookListType.all);
    }

    public void ChangeType(States.bookListType type) {
        BookListFragment fragment = BookListFragment.newInstance(type);
        changeFragment(type, this.getClass(), fragment, R.id.book_list_fragment);
    }


}