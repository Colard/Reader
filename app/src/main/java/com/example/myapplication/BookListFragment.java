package com.example.myapplication;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ProgressBar;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BookListFragment extends BaseFragment {
    private ArrayList<BookInfo> bookInfoArrayList;
    private ProgressBar progressBar;
    private static final String ARG_PARAM1 = "type";
    private States.bookListType mEnumType;
    private ViewGroup gridbox;
    private static GoogleSignInAccount acc;
    String url = "https://books.googleapis.com/books/v1/mylibrary/bookshelves/3/volumes";
    RequestQueue queue;
    public BookListFragment() {
    }

    public static BookListFragment newInstance(States.bookListType param) {
        BookListFragment fragment = new BookListFragment();

        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param);
        fragment.setArguments(args);
        return fragment;
    }

    void openMyBook( BookInfo bookInfo) {
        Intent i = new Intent(getContext(), MyBookInfo.class);

        i.putExtra("book", bookInfo);

        startActivity(i);
        getActivity().overridePendingTransition(R.anim.swipe_to_up, R.anim.no_animation);
    }

    private void typeChanger(View view, States.bookListType param) {
        TextView text = view.findViewById(R.id.headerText);
        queue = Volley.newRequestQueue(getActivity());
        if(text != null) {
            switch (param) {
                case read: text.setText("I AM READING");
                showBookShelf(3);break;
                case plans: text.setText("IN THE PLANS");
                showBookShelf(2); break;
                case readed: text.setText("IT HAS BEEN READ");
                showBookShelf(4);break;
                case favorite: text.setText("FAVORITE");
                showBookShelf(0);break;
                default: text.setText("ALL BOOK");
                showAllBooks();break;
            }
        }
    }

    void showAllBooks(){
        url = "https://books.googleapis.com/books/v1/mylibrary/bookshelves/3/volumes";
        TokenCreator.UseAccessToken(getContext(), acc,
                (String key)-> {
                    CreateShelfBookList(url, queue, key, 3);
                    url = "https://books.googleapis.com/books/v1/mylibrary/bookshelves/0/volumes";
                    TokenCreator.UseAccessToken(getContext(), acc,
                            (String key1)-> {
                                CreateShelfBookList(url, queue, key1, 0);
                                url = "https://books.googleapis.com/books/v1/mylibrary/bookshelves/2/volumes";
                                TokenCreator.UseAccessToken(getContext(), acc,
                                        (String key2)-> {
                                            CreateShelfBookList(url, queue, key2, 2);
                                            url = "https://books.googleapis.com/books/v1/mylibrary/bookshelves/4/volumes";
                                            TokenCreator.UseAccessToken(getContext(), acc,
                                                    (String key3)-> {
                                                        CreateShelfBookList(url, queue, key3, 4);
                                                    });
                                        });
                            });
                });
    }

    void showBookShelf(int id){
        url = "https://books.googleapis.com/books/v1/mylibrary/bookshelves/"+id+"/volumes";
        TokenCreator.UseAccessToken(getContext(), acc,
                (String key3)-> {
                    CreateShelfBookList(url, queue, key3, id);
                    setBookShelfId(id);
                });
    }

    void setBookShelfId(int id) {
        for(int i = 0; i < bookInfoArrayList.size(); i++) {
            if(bookInfoArrayList.get(i).getShelfID()==-1)bookInfoArrayList.get(i).setShelfID(id);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        acc = GoogleSignIn.getLastSignedInAccount(getContext());

        if (getArguments() != null) {
            mEnumType = (States.bookListType) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        android.view.View view = inflater.inflate(R.layout.fragment_book_list, container, false);
        typeChanger(view, mEnumType);
        gridbox = view.findViewById(R.id.list_google_book_grid);
        progressBar = view.findViewById(R.id.BookListLoadingPB);
        progressBar.setVisibility(View.VISIBLE);
        return view;
    }

    public void update(Context context, ArrayList<BookInfo> list) {
        gridbox.removeAllViews();
        for (int i = 0; i < list.size(); i++) {
            BookInfo book = list.get(i);
            BookButton btn = new BookButton(context, book);

            GridLayout.LayoutParams parem = new GridLayout.LayoutParams(GridLayout.spec(GridLayout.UNDEFINED, 1f),      GridLayout.spec(GridLayout.UNDEFINED, 1f));
            btn.setLayoutParams(parem);
            btn.setOnClickListener(v -> {
                openMyBook(book);
            });

            gridbox.addView(btn);
        }
    }

    private void CreateShelfBookList(String url, RequestQueue queue, String ACCESS_TOKEN, int ShelfId) {
        bookInfoArrayList = new ArrayList<>();
        JsonObjectRequest booksObjrequest = new JsonObjectRequest(Request.Method.GET, url,null,
                (res) -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                            createListOfBooks(res, ShelfId);
                            this.update(getContext(), bookInfoArrayList);
                        } catch (JSONException e) {
                            Log.d(TAG, "" + e);
                            e.printStackTrace();
                        }
                    },
                this::bookListError)
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

    void bookListError(VolleyError error) {
        Log.d(TAG, "" + error);
        error.printStackTrace();
    }


    private String checkString(String s) {
        return (s != null) ? s : "";
    }

    private void createListOfBooks(JSONObject response, int shelfID) throws JSONException {
        JSONArray itemsArray = response.getJSONArray("items");
        for (int i = 0; i < itemsArray.length(); i++) {
            JSONObject itemsObj = itemsArray.getJSONObject(i);

            BookInfo bookInfo = new BookInfo(itemsObj);
            bookInfo.setShelfID(shelfID);

            Log.d(TAG, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            Log.d(TAG, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            Log.d(TAG, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            Log.d(TAG, bookInfo.getWebLink());
            Log.d(TAG, bookInfo.getaccessViewStatus());
            bookInfoArrayList.add(bookInfo);
        }
    }
}