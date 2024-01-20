package com.example.rs4u_v2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MiniMarket extends AppCompatActivity {

    FrameLayout filter;
    private GridLayout gridLayout;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mini_market);

        filter = findViewById(R.id.bottomFrameLayout);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MiniMarket.this, Filter.class);
                startActivity(intent);
            }
        });

        gridLayout = findViewById(R.id.minimarketGridLayout); // Assuming you have a GridLayout in your XML with id "minimarketGridLayout"
        db = FirebaseFirestore.getInstance();
        retrieveDataFromFirestore();
    }

    private void retrieveDataFromFirestore() {
        db.collection("ShopInformation")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Extract data from the document
                                String shopID = document.getString("shop_id");
                                String shopName = document.getString("shop_name");

                                // Query CusReview collection for all reviews of the shop
                                db.collection("CusReview")
                                        .whereEqualTo("shop_id", shopID)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                                    float totalRating = 0;
                                                    int reviewCount = 0;

                                                    // Loop through each review
                                                    for (QueryDocumentSnapshot reviewDocument : task.getResult()) {
                                                        String ratingString = reviewDocument.getString("rating");
                                                        if (ratingString != null && !ratingString.trim().isEmpty()) {
                                                            try {
                                                                float rating = Float.parseFloat(ratingString);
                                                                totalRating += rating;
                                                                reviewCount++;

                                                            } catch (NumberFormatException e) {
                                                                Log.e("Mini Market", "Error parsing rating to float: " + ratingString, e);
                                                            }
                                                        } else {
                                                            Log.e("Mini Market", "Rating string is null or empty");
                                                        }
                                                    }

                                                    // Calculate average rating
                                                    float averageRating = totalRating / reviewCount;

                                                    // Create a GridLayout to hold the data
                                                    GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                                                    params.width = GridLayout.LayoutParams.MATCH_PARENT;
                                                    params.height = GridLayout.LayoutParams.WRAP_CONTENT;
                                                    params.rowSpec = GridLayout.spec(gridLayout.getChildCount(), GridLayout.FILL, 1f);
                                                    params.columnSpec = GridLayout.spec(0, 2, 1f);

                                                    LinearLayout shopLayout = new LinearLayout(MiniMarket.this);
                                                    shopLayout.setOrientation(LinearLayout.VERTICAL);
                                                    shopLayout.setLayoutParams(params);

                                                    // Create TextViews for shop name, average rating, and review count
                                                    TextView nameTextView = new TextView(MiniMarket.this);
                                                    nameTextView.setText("Shop Name: " + shopName);

                                                    TextView ratingTextView = new TextView(MiniMarket.this);
                                                    ratingTextView.setText("Average Rating: " + averageRating);

                                                    TextView reviewCountTextView = new TextView(MiniMarket.this);
                                                    reviewCountTextView.setText("Review Count: " + reviewCount);

                                                    // Set an OnClickListener for the LinearLayout
                                                    shopLayout.setOnClickListener(v -> {
                                                        Intent intent = new Intent(MiniMarket.this, ShopDisplay.class);
                                                        intent.putExtra("shopID", shopID);
                                                        startActivity(intent);
                                                    });

                                                    // Add TextViews to the LinearLayout
                                                    shopLayout.addView(nameTextView);
                                                    shopLayout.addView(ratingTextView);
                                                    shopLayout.addView(reviewCountTextView);

                                                    // Add the LinearLayout to the GridLayout with the specified LayoutParams
                                                    gridLayout.addView(shopLayout, params);
                                                } else {
                                                    Log.e("Mini Market", "Error getting review documents: ", task.getException());
                                                }
                                            }
                                        });
                            }
                        } else {
                            Log.e("Mini Market", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

}
