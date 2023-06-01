package com.example.myapplication;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myapplication.SupportClasses.BookInfo;
import com.squareup.picasso.Picasso;

public class BookButton extends FrameLayout {
    public BookButton(Context context, BookInfo bookInfo) {
        super(context);
        initControl(context, bookInfo);
    }
    private void initControl(Context context, BookInfo bookInfo)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.book_rv_item, this);

        TextView textView = (TextView)findViewById(R.id.idTVBookTitle);
        textView.setText(bookInfo.getTitle());

        ImageView bookIV = findViewById(R.id.idIVbook);
        if(bookInfo.getThumbnail() != null)  {
            String url = bookInfo.getThumbnail().replaceFirst("http://", "https://");
            Picasso.get().load(url).into(bookIV);
        } else {
            bookIV.setImageResource(R.drawable.cover);
        }

    }

}