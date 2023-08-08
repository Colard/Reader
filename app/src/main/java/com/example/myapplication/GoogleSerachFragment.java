package com.example.myapplication;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import com.example.myapplication.SupportClasses.BookAdapter;
import com.example.myapplication.SupportClasses.BookInfo;
import com.example.myapplication.SupportClasses.States;

import java.util.ArrayList;

public class GoogleSerachFragment extends BaseFragment {

    private ViewGroup gridbox;

    public GoogleSerachFragment() {
    }

    public static GoogleSerachFragment newInstance() {
        GoogleSerachFragment fragment = new GoogleSerachFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        android.view.View view = inflater.inflate(R.layout.fragment_google_serach, container, false);
        gridbox = view.findViewById(R.id.google_book_grid);
        return view;
    }


    public void update(Context context, ArrayList<BookInfo> list) {
        gridbox.removeAllViews();
        for (int i = 0; i < list.size(); i++) {
            BookInfo book = list.get(i);
            BookButton btn = new BookButton(context, book);
            GridLayout.LayoutParams parem = new GridLayout.LayoutParams(GridLayout.spec(GridLayout.UNDEFINED, 1f),      GridLayout.spec(GridLayout.UNDEFINED, 1f));;
            btn.setLayoutParams(parem);

            btn.setOnClickListener(v -> {
                openBook(book);
            });
            gridbox.addView(btn);
        }
    }

    void openBook(BookInfo bookInfo) {
        Intent i = new Intent(getContext(), SearchedBook.class);

        i.putExtra("book", bookInfo);

        startActivity(i);
        getActivity().overridePendingTransition(R.anim.swipe_to_up, R.anim.no_animation);
    }
}