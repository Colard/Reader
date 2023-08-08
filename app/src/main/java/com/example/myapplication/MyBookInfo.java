package com.example.myapplication;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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

public class MyBookInfo extends BaseActivity {
    BookInfo book;

    protected int getLayoutResourceId(){
        return R.layout.activity_main;
    }

    private ArrayList<String> authors;
    private static GoogleSignInAccount acc;

    TextView titleTV, subtitleTV, publisherTV, descTV, pageTV, publishDateTV;
    Button previewBtn, readBtn, buyBtn, deleteBtn;
    private ImageView bookIV;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_book_info);
        acc = GoogleSignIn.getLastSignedInAccount(this);

        titleTV = findViewById(R.id.idTVTitle);
        subtitleTV = findViewById(R.id.idTVSubTitle);
        publisherTV = findViewById(R.id.idTVpublisher);
        descTV = findViewById(R.id.idTVDescription);
        pageTV = findViewById(R.id.idTVNoOfPages);
        publishDateTV = findViewById(R.id.idTVPublishDate);
        previewBtn = findViewById(R.id.idBtnPreview);
        buyBtn = findViewById(R.id.idBuyBtn);
        readBtn = findViewById(R.id.idBtnRead);
        deleteBtn = findViewById(R.id.idDeleteBtn);
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

        addSpiner();

        readBtn.setOnClickListener(v -> {
            if (book.getWebLink().isEmpty()) {
                Toast.makeText(MyBookInfo.this, "No read Link present", Toast.LENGTH_SHORT).show();
                return;
            }

            Uri uri = Uri.parse(book.getWebLink());
            Intent i = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(i);
        });

        previewBtn.setOnClickListener(v -> {
            if (book.getPreviewLink().isEmpty()) {
                Toast.makeText(MyBookInfo.this, "No preview Link present", Toast.LENGTH_SHORT).show();
                return;
            }

            Uri uri = Uri.parse(book.getPreviewLink());
            Intent i = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(i);
        });

        buyBtn.setOnClickListener(v -> {
            if (book.getBuyLink().isEmpty()) {
                Toast.makeText(MyBookInfo.this, "No buy Link present", Toast.LENGTH_SHORT).show();
                return;
            }

            Uri uri = Uri.parse(book.getBuyLink());
            Intent i = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(i);
        });

        deleteBtn.setOnClickListener(v -> deleteComfirm());

        if(!(Objects.equals(book.getaccessViewStatus(), "SAMPLE") ||
                        Objects.equals(book.getaccessViewStatus(), "NONE"))) {
            buyBtn.setVisibility(View.GONE);
        }

        findViewById(R.id.idReturn).setOnClickListener(this::Return);
    }

    void addSpiner() {
        Spinner spinner = findViewById(R.id.spinner_book_status);
        String[] statusList = {"-SELECT SHELF-" , "IN THE PLANS", "FAVORITE", "I AM READING", "IT HAS BEEN READ"};
        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, statusList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int bookshelfId = -1;
                switch (position) {
                    case 0:
                        bookshelfId = -1;
                        break;
                    case 1:
                        bookshelfId = 2;
                        break;
                    case 2:
                        bookshelfId = 0;
                        break;
                    case 3:
                        bookshelfId = 3;
                        break;
                    case 4:
                        bookshelfId = 4;
                        break;
                }
                if(bookshelfId != -1) {
                    moveBook(bookshelfId);
                    Toast.makeText(getApplicationContext(), "BOOK MOVED", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };
        spinner.setOnItemSelectedListener(itemSelectedListener);
    }

    void moveBook(int ShelfId){
        String url = "https://www.googleapis.com/books/v1/mylibrary/bookshelves/"+book.getShelfID()+"/removeVolume?volumeId="+book.getId();
        RequestQueue queue = Volley.newRequestQueue(this);;
        GoogleSignInAccount acc = GoogleSignIn.getLastSignedInAccount(this);
        TokenCreator.UseAccessToken(this, acc,
                (String key)-> moveBookShelfPost(url, queue ,key, ShelfId));
    }

    private void moveBookShelfPost(String url, RequestQueue queue, String ACCESS_TOKEN, int ShelfId) {
        JsonObjectRequest booksObjrequest =
                new JsonObjectRequest(Request.Method.POST, url,null, (response)->
                {
                    TokenCreator.UseAccessToken(this, acc,
                            (String key2)-> {
                                String newUrl = "https://www.googleapis.com/books/v1/mylibrary/bookshelves/"+ShelfId+"/addVolume?volumeId="+book.getId();
                                addBookShelfPost(newUrl, queue ,key2);
                            } );
                }, Throwable::printStackTrace)
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

    private void deleteBookFromShelf(String url, RequestQueue queue, String ACCESS_TOKEN) {
        JsonObjectRequest booksObjrequest =
                new JsonObjectRequest(Request.Method.POST, url,null, (response)-> {
                    Toast.makeText(MyBookInfo.this, "Book deleted", Toast.LENGTH_SHORT).show();
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

    private void addBookShelfPost(String url, RequestQueue queue, String ACCESS_TOKEN) {
        JsonObjectRequest booksObjrequest =
                new JsonObjectRequest(Request.Method.POST, url,null,
                        (response)->{

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

    private void deleteComfirm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Remove book")
                .setMessage("Do you want to remove the book from the shelf?")
                .setPositiveButton("Yes", (dialog, id) -> {
                    String url = "https://www.googleapis.com/books/v1/mylibrary/bookshelves/"+book.getShelfID()+"/removeVolume?volumeId="+book.getId();
                    RequestQueue queue = Volley.newRequestQueue(this);;
                    GoogleSignInAccount acc = GoogleSignIn.getLastSignedInAccount(this);
                    TokenCreator.UseAccessToken(this, acc,
                            (String key)-> deleteBookFromShelf(url, queue ,key));
                })
                .setNegativeButton("No", (dialog, id) -> {});

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}