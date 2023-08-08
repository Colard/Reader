package com.example.myapplication;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.SupportClasses.BookInfo;
import com.example.myapplication.SupportClasses.States;
import com.example.myapplication.SupportClasses.TokenCreator;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SearchedBook extends BaseActivity {
    BookInfo book;

    protected int getLayoutResourceId(){
        return R.layout.activity_main;
    }

    private ArrayList<String> authors;
    private static GoogleSignInAccount acc;

    TextView titleTV, subtitleTV, publisherTV, descTV, pageTV, publishDateTV;
    Button previewBtn, btnAddToLib, buyBtn;
    private ImageView bookIV;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searched_book);
        acc = GoogleSignIn.getLastSignedInAccount(this);

        titleTV = findViewById(R.id.idTVTitle);
        subtitleTV = findViewById(R.id.idTVSubTitle);
        publisherTV = findViewById(R.id.idTVpublisher);
        descTV = findViewById(R.id.idTVDescription);
        pageTV = findViewById(R.id.idTVNoOfPages);
        publishDateTV = findViewById(R.id.idTVPublishDate);
        previewBtn = findViewById(R.id.idBtnPreview);
        buyBtn = findViewById(R.id.idBuyBtn);
        btnAddToLib = findViewById(R.id.idBtnAddToLib);
        bookIV = findViewById(R.id.idIVbook);

        book = (BookInfo) getIntent().getSerializableExtra("book");

        titleTV.setText(book.getTitle());
        subtitleTV.setText(book.getSubtitle());
        publisherTV.setText(book.getPublisher());
        publishDateTV.setText("Published On : " + book.getPublishedDate());
        descTV.setText(book.getDescription());
        pageTV.setText("No Of Pages : " + book.getPageCount());

        if(book.getThumbnail() != null)  {
            String url = book.getThumbnail().replaceFirst("http://", "https://");
            Picasso.get().load(url).into(bookIV);
        } else {
            bookIV.setImageResource(R.drawable.cover);
        }

        btnAddToLib.setOnClickListener(v -> {
            addBook();
        });

        previewBtn.setOnClickListener(v -> {
            if (book.getPreviewLink().isEmpty()) {
                Toast.makeText(SearchedBook.this, "No preview Link present", Toast.LENGTH_SHORT).show();
                return;
            }

            Uri uri = Uri.parse(book.getPreviewLink());
            Intent i = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(i);
        });

        buyBtn.setOnClickListener(v -> {
            if (book.getBuyLink().isEmpty()) {
                Toast.makeText(SearchedBook.this, "No buy Link present", Toast.LENGTH_SHORT).show();
                return;
            }

            Uri uri = Uri.parse(book.getBuyLink());
            Intent i = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(i);
        });

        if(!(Objects.equals(book.getaccessViewStatus(), "SAMPLE") ||
                Objects.equals(book.getaccessViewStatus(), "NONE"))) {
            buyBtn.setVisibility(View.GONE);
        }

        findViewById(R.id.idReturn).setOnClickListener(this::Return);
    }

    void addBook(){
        String url = "https://www.googleapis.com/books/v1/mylibrary/bookshelves/2/addVolume?volumeId="+book.getId();
        RequestQueue queue = Volley.newRequestQueue(this);;
        GoogleSignInAccount acc = GoogleSignIn.getLastSignedInAccount(this);
        TokenCreator.UseAccessToken(this, acc,
                (String key)-> addBookShelfPost(url, queue ,key));
    }

    private void addBookShelfPost(String url, RequestQueue queue, String ACCESS_TOKEN) {
        JsonObjectRequest booksObjrequest =
                new JsonObjectRequest(Request.Method.POST, url,null,
                        (response)->{
                            Toast.makeText(SearchedBook.this, "Book added", Toast.LENGTH_SHORT).show();
                            Return(null);
                        },
                        Throwable::printStackTrace)
                {
                    @Override
                    public Map<String, String> getHeaders() {

                        Map<String, String> headers = new HashMap<>();
                        headers.put("Authorization", "Bearer " + ACCESS_TOKEN);
                        return headers;
                    }
                };

        queue.add(booksObjrequest);
    }

    public void Return(View view){
        Intent intent = new Intent(this, BookListMenuActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.swipe_to_up, R.anim.no_animation);
    }

}