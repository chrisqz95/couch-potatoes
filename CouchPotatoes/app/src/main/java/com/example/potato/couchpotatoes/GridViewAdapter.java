package com.example.potato.couchpotatoes;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GridViewAdapter extends ArrayAdapter<String> {

    private Context context;
    private int layoutResourceId;
    private ArrayList<String> urlList = new ArrayList<String>();

    public GridViewAdapter(Context context, int layoutResourceId, ArrayList<String> urlList) {
        super(context, layoutResourceId, urlList);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.urlList = urlList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ImageView image;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            image = (ImageView) row.findViewById(R.id.image);
            row.setTag(image);
        } else {
            image = (ImageView) row.getTag();
        }

        Picasso.with(context).load(urlList.get(position)).into(image);
        return row;
    }
}