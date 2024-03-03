package com.example.rs4u_v2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class ShopDisplay extends AppCompatActivity {
    private RatingBar ratingBar;
    private EditText reviewEditText;
    private FirebaseFirestore db;
    private FirebaseFirestore firestore;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_display);

        firestore = FirebaseFirestore.getInstance();
        database = FirebaseDatabase.getInstance();

        // Get the passed information from the intent
        Intent intent = getIntent();
        String shopID = intent.getStringExtra("shopID");

        // Find the TextViews in the layout
        TextView shopNameTextView = findViewById(R.id.shopNameTextView);
        TextView shopLocationTextView = findViewById(R.id.shopLocationTextView);
        TextView shopEmailTextView = findViewById(R.id.shopEmailTextView);
        TextView shopPhoneTextView = findViewById(R.id.shopPhoneTextView);
        TextView shopDescTextView = findViewById(R.id.shopDescTextView);
        TextView shopCategoryTextView = findViewById(R.id.shopCategoryTextView);

        // Find views
        TextView ReviewTextView = findViewById(R.id.ReviewTextView);
        ratingBar = findViewById(R.id.ratingBar);
        reviewEditText = findViewById(R.id.reviewEditText);
        Button submitButton = findViewById(R.id.submitButton);

        // Retrieve additional shop details from Firestore
        firestore.collection("ShopInformation")
                .whereEqualTo("shop_id", shopID)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        // Retrieve data from the document
                        String shopName = document.getString("shop_name");
                        String shopCategory = document.getString("shop_cat");
                        String shopLocation = document.getString("shop_location");
                        String shopEmail = document.getString("shop_email");
                        String shopPhone = document.getString("shop_phone");
                        String shopDesc = document.getString("shop_desc");

                        // Set the retrieved information to the TextViews
                        shopNameTextView.setText(String.format("Shop Name: %s", shopName));
                        shopLocationTextView.setText(String.format("Location: %s", shopLocation));
                        shopEmailTextView.setText(String.format("Email: %s", shopEmail));
                        shopPhoneTextView.setText(String.format("Phone Number: %s", shopPhone));
                        shopDescTextView.setText(String.format("Description: %s", shopDesc));
                        shopCategoryTextView.setText(shopCategory);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Shop Display", "Error getting shop details from Firestore", e);
                    // Handle error if necessary
                });


        // Handle submit button click
        submitButton.setOnClickListener(v -> submitReview());
    }

    private void submitReview() {
        float rating = ratingBar.getRating();
        String stars = String.valueOf(rating);
        String texts = reviewEditText.getText().toString();
        String review_id;

        Intent intent = getIntent();
        String shop_id = intent.getStringExtra("shopID");
        Handler handler = new Handler();
        Random random = new Random();
        int[] randomNumbers = new int[4];

        for (int i = 0; i < 4; i++) {
            randomNumbers[i] = random.nextInt(10); // Generate a random number between 0 and 9
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String review_date = dateFormat.format(new Date());

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String review_time = timeFormat.format(new Date());

        review_id = Arrays.toString(randomNumbers) + "_" + review_date + "_" + review_time;

        // Set this boolean to determine whether to use Firestore or Realtime Database
        boolean useFirestore = true; // Set to true for Firestore, false for Realtime Database

        MyThread connectThread = new MyThread(review_id, shop_id, texts, stars, review_date, review_time, handler, useFirestore);
        connectThread.start();

        // For now, let's just show a toast message
        String message = "Submitted: Rating=" + rating + ", Review=" + texts;
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    public class MyThread extends Thread {
        private String review_id;
        private String shop_id;
        private String texts;
        private String stars;
        private String review_date;
        private String review_time;
        private Handler mHandler;
        private boolean useFirestore;

        public MyThread(String review_id, String shop_id, String texts, String stars, String review_date, String review_time, Handler handler, boolean useFirestore) {
            this.review_id = review_id;
            this.shop_id = shop_id;
            this.texts = texts;
            this.stars = stars;
            this.review_date = review_date;
            this.review_time = review_time;
            this.mHandler = handler;
            this.useFirestore = useFirestore;
        }

        public void run() {
            try {
                if (useFirestore) {
                    // Use Firestore
                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                    Map<String, Object> reviewDataFirestore = new HashMap<>();
                    reviewDataFirestore.put("review_id", review_id);
                    reviewDataFirestore.put("shop_id", shop_id);
                    reviewDataFirestore.put("texts", texts);
                    reviewDataFirestore.put("stars", stars);
                    reviewDataFirestore.put("review_date", review_date);
                    reviewDataFirestore.put("review_time", review_time);

                    firestore.collection("CusReview")
                            .document(review_id)
                            .set(reviewDataFirestore)
                            .addOnSuccessListener(aVoid -> {
                                Log.i("Shop display", "Review added successfully to Firestore");
                            })
                            .addOnFailureListener(e -> {
                                Log.e("Shop display", "Error adding review to Firestore", e);
                            });
                }

                // Use Realtime Database
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference reviewRef = database.getReference("CusReview").child(review_id);

                Map<String, Object> reviewDataRealtimeDB = new HashMap<>();
                reviewDataRealtimeDB.put("review_id", review_id);
                reviewDataRealtimeDB.put("shop_id", shop_id);
                reviewDataRealtimeDB.put("texts", texts);
                reviewDataRealtimeDB.put("stars", stars);
                reviewDataRealtimeDB.put("review_date", review_date);
                reviewDataRealtimeDB.put("review_time", review_time);

                reviewRef.setValue(reviewDataRealtimeDB, (error, ref) -> {
                    if (error == null) {
                        Log.i("Shop display", "Review added successfully to Realtime Database");
                    } else {
                        Log.e("Shop display", "Error adding review to Realtime Database", error.toException());
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}