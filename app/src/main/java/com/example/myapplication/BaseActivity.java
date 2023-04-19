package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public abstract class BaseActivity extends AppCompatActivity {

    protected abstract int getLayoutResourceId();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());
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
            default:
                type = "all";
                break;
        }

        Intent intent = new Intent(this, BookListMenuActivity.class);
        intent.putExtra("type", type);
        startActivity(intent);
        overridePendingTransition(R.anim.swipe_to_up, R.anim.no_animation);

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater findMenuItems = getMenuInflater();
        findMenuItems.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

}