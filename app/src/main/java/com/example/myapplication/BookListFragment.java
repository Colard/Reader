package com.example.myapplication;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication.SupportClasses.States;

public class BookListFragment extends Fragment {

    private static final String ARG_PARAM1 = "type";
    private States.bookListType mEnumType;

    public BookListFragment() {
    }

    public static BookListFragment newInstance(States.bookListType param) {
        BookListFragment fragment = new BookListFragment();

        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param);
        fragment.setArguments(args);
        return fragment;
    }

    private void typeChanger(View view, States.bookListType param) {
        TextView text = view.findViewById(R.id.headerText);

        if(text != null) {
            switch (param) {
                case read: text.setText("I AM READING"); break;
                case plans: text.setText("IN THE PLANS"); break;
                case readed: text.setText("IT HAS BEEN READ"); break;
                case favorite: text.setText("FAVORITE");break;
                default: text.setText("ALL BOOK"); break;
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mEnumType = (States.bookListType) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        android.view.View view = inflater.inflate(R.layout.fragment_book_list, container, false);
        typeChanger(view, mEnumType);
        return view;
    }
}