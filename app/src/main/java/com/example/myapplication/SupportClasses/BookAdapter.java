package com.example.myapplication.SupportClasses;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.SupportClasses.BookInfo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BookAdapter extends BaseAdapter {
    private ArrayList<BookInfo> bookInfoArrayList;
    private Context mContext;

    public BookAdapter(ArrayList<BookInfo> bookInfoArrayList, Context context) {
        this.bookInfoArrayList = bookInfoArrayList;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return bookInfoArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return bookInfoArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(R.layout.book_rv_item, parent, false);

        ViewHolder viewHolder = new ViewHolder();
        viewHolder.nameTV = convertView.findViewById(R.id.idTVBookTitle);
        viewHolder.bookIV = convertView.findViewById(R.id.idIVbook);

        convertView.setTag(viewHolder);

        BookInfo bookInfo = bookInfoArrayList.get(position);
        viewHolder.nameTV.setText(bookInfo.getTitle());
        Picasso.get().load(bookInfo.getThumbnail()).into(viewHolder.bookIV);

        return convertView;
    }

    public class ViewHolder {
        TextView nameTV;
        ImageView bookIV;
    }
}