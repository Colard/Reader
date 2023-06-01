package com.example.myapplication;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.toolbox.StringRequest;
import com.example.myapplication.SupportClasses.TokenCreator;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.SupportClasses.BookInfo;
import com.example.myapplication.SupportClasses.States;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BookListMenuActivity extends BaseActivity {

    private RequestQueue mRequestQueue;
    private ArrayList<BookInfo> bookInfoArrayList;
    private ProgressBar progressBar;
    @Override
    protected int getLayoutResourceId() {
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

        if (!isFirstStart) return;

        if (arguments != null ) {
            SetType(arguments);
        } else {
            if(States.thisFragment == null)
                SetType(States.bookListType.all);
            else SetType(States.thisFragment);
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
        MenuInflater findMenuItems = getMenuInflater();
        findMenuItems.inflate(R.menu.main_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) menuItem.getActionView();

        menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {

            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                GoogleSerachFragment fragment = GoogleSerachFragment.newInstance();
                changeFragment(fragment, R.id.google_books_list);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                removeGoogleSearch();
                return true;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                progressBar = findViewById(R.id.idLoadingPB);
                progressBar.setVisibility(View.VISIBLE);

                if (query.isEmpty()) {
                    return false;
                }

                try {
                    searchBooks(query);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private boolean removeGoogleSearch() {
        if (this.getClass() == BookListMenuActivity.class) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = fragmentManager.findFragmentById(R.id.google_books_list);
            if (fragment != null) {
                fragmentManager.beginTransaction().remove(fragment).commit();
            }
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
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
        changeBookListFragment(type, this.getClass(), fragment, R.id.book_list_fragment);
    }

    public void SetType(States.bookListType type) {
        BookListFragment fragment = BookListFragment.newInstance(type);
        writeFragmentType(type);
        changeFragment(fragment, R.id.book_list_fragment);
    }

    private void searchBooks(String query) throws JSONException {
        bookInfoArrayList = new ArrayList<>();
        mRequestQueue = Volley.newRequestQueue(this);
        mRequestQueue.getCache().clear();

        GoogleSignInAccount acc = GoogleSignIn.getLastSignedInAccount(this);
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = "https://www.googleapis.com/books/v1/volumes?q=" + query + "&maxResults=40";
        TokenCreator.UseAccessToken(this, acc,
                 (String key)-> {
                     CreateSearchedBookList(url, queue, key);
         });
    }

    private void CreateSearchedBookList(String url, RequestQueue queue, String ACCESS_TOKEN) {
        JsonObjectRequest booksObjrequest =
                new JsonObjectRequest(Request.Method.GET, url,null, this::bookSearchListResponse, this::bookListError)
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
    void bookSearchListResponse(JSONObject response) {
        progressBar.setVisibility(View.GONE);
        try {
            createListOfBooks(response);

            FragmentManager fragmentManager = getSupportFragmentManager();
            GoogleSerachFragment fragment = (GoogleSerachFragment) fragmentManager.findFragmentById(R.id.google_books_list);
            if (fragment != null) {
                fragment.update(this, bookInfoArrayList);
            }

        } catch (JSONException e) {
            Log.d(TAG, "" + e);
            Toast.makeText(this, ("No Book Found"), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    void bookListError(VolleyError error) {
        Toast.makeText(this, "Error found is " + error, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "" + error);
        error.printStackTrace();
    }

    private String checkString(String s) {
        return (s != null) ? s : "";
    }

    private void createListOfBooks(JSONObject response) throws JSONException {
        JSONArray itemsArray = response.getJSONArray("items");
        for (int i = 0; i < itemsArray.length(); i++) {
            JSONObject itemsObj = itemsArray.getJSONObject(i);
            String id = itemsObj.optString("id");
            JSONObject volumeObj = itemsObj.getJSONObject("volumeInfo");
            String title = checkString(volumeObj.optString("title"));
            String subtitle = checkString(volumeObj.optString("subtitle"));
            String publisher = checkString(volumeObj.optString("publisher"));
            String publishedDate = checkString(volumeObj.optString("publishedDate"));
            String description = checkString(volumeObj.optString("description"));
            int pageCount = volumeObj.optInt("pageCount");
            JSONObject imageLinks = volumeObj.optJSONObject("imageLinks");
            String previewLink = checkString(volumeObj.optString("previewLink"));
            String infoLink = checkString(volumeObj.optString("infoLink"));
            JSONObject saleInfoObj = itemsObj.optJSONObject("saleInfo");

            ArrayList<String> authorsArrayList = new ArrayList<>();
            if (volumeObj.has("authors")) {
                JSONArray authorsArray = volumeObj.getJSONArray("authors");
                authorsArrayList = new ArrayList<>();
                if (authorsArray.length() != 0) {
                    for (int j = 0; j < authorsArray.length(); j++) {
                        authorsArrayList.add(authorsArray.optString(i));
                    }
                }
            }

            String thumbnail;
            if (imageLinks != null)
                thumbnail = checkString(imageLinks.optString("thumbnail"));
            else
                thumbnail = null;

            String buyLink;
            if (saleInfoObj != null)
                buyLink = checkString(saleInfoObj.optString("buyLink"));
            else
                buyLink = null;

            BookInfo bookInfo = new BookInfo(id, title, subtitle, authorsArrayList, publisher, publishedDate, description, pageCount, thumbnail, previewLink, infoLink, buyLink);

            bookInfoArrayList.add(bookInfo);
        }
    }
}