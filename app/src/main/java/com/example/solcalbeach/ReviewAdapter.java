package com.example.solcalbeach;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.solcalbeach.util.Review;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
        TextView tvComments = (TextView) convertView.findViewById(R.id.tvComments);
        Button deletBut = (Button) convertView.findViewById(R.id.delete_button);
        // Populate the data into the template view using the data object
        tvName.setText(review.getBeachName());
        tvHome.setText(review.getRating());
        tvComments.setText(review.getComments());
        deletBut.setTag(review.getReviewId());
        deletBut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // your handler code here
                Log.e("Try to delete:", String.valueOf(deletBut.getTag()));
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                Query applesQuery = ref.child("reviews").orderByChild("reviewId").equalTo(String.valueOf(deletBut.getTag()));

                applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                            appleSnapshot.getRef().removeValue();
                        }
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("Error: ", "onCancelled", databaseError.toException());
                    }
                });

                remove(review);
            }
        });
        // Return the completed view to render on screen
        return convertView;
    }
}
