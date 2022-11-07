package com.example.solcalbeach;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.solcalbeach.util.Review;

import java.util.ArrayList;

public class ReviewAdapter extends ArrayAdapter<Review> {
    public ReviewAdapter(Context context, ArrayList<Review> reviews) {
        super(context, 0, reviews);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Review review = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_format, parent, false);
        }
        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.tvBeach);
        TextView tvHome = (TextView) convertView.findViewById(R.id.tvRating);
        // Populate the data into the template view using the data object
        tvName.setText(review.getBeachName());
        tvHome.setText(review.getRating());
        // Return the completed view to render on screen
        return convertView;
    }
}
