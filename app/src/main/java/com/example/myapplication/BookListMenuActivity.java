package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

public class BookListMenuActivity extends BaseActivity {

    @Override
    protected int getLayoutResourceId(){
        return R.layout.activity_book_list_menu;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getIntent().getExtras();
        if(arguments != null) {
            String type = arguments.get("type").toString();
            ChangeType(type);
        } else {
            ChangeType("all");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        String type = "";

        switch(id){
            case R.id.reading :
                type = "read";
                break;
            case R.id.in_the_plans:
                type = "plans";
                break;
            case R.id.has_been_read:
                type = "readed";
                break;
            case R.id.favorite:
                type = "favorite";
                break;
            default:
                type = "all";
                break;
        }

        ChangeType(type);

        return true;
    }

    public void moveToFavorite(View v) {
        ChangeType("favorite");
    }

    public void moveToAllBooks(View v) {
        ChangeType("all");
    }

    public void ChangeType(String type) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_in_right);
        BookListFragment fragment = BookListFragment.newInstance(type);
        ft.replace(R.id.book_list_fragment, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }
}