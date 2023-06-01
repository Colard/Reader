package com.example.myapplication;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.os.Bundle;
import android.widget.Toast;

import com.example.myapplication.SupportClasses.States;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;

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

    @Override
    public void onStart() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("410211102598-ug99uocplb3m1f45q5na5p4ia1eaig02.apps.googleusercontent.com")
                .requestScopes(new Scope("https://www.googleapis.com/auth/books"))
                .requestServerAuthCode("410211102598-ug99uocplb3m1f45q5na5p4ia1eaig02.apps.googleusercontent.com")
                .requestEmail().build();

        GoogleSignInClient gsc = GoogleSignIn.getClient(this, gso);
        gsc.signOut();
        gsc = GoogleSignIn.getClient(this, gso);
        Intent signInIntetent = gsc.getSignInIntent();
        startActivityForResult(signInIntetent, 1000);

        super.onStart();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1000) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                task.getResult(ApiException.class);
                Toast.makeText(getApplicationContext(), "Autentification Done", Toast.LENGTH_SHORT).show();
            } catch (ApiException e) {
                Toast.makeText(getApplicationContext(), "Autentification Error", Toast.LENGTH_SHORT).show();
                e.printStackTrace();

            }
        }
    }

    public void startWorkActivity(View v) {
        Intent intent = new Intent(this, BookListMenuActivity.class);
        intent.putExtra("type", States.bookListType.all);
        startActivity(intent);
        overridePendingTransition(R.anim.swipe_to_up, R.anim.no_animation);
    }

}