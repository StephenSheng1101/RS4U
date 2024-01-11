package com.example.rs4u_v2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MiniMarket extends AppCompatActivity {

    FrameLayout filter;
    private LinearLayout container;
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

        container = findViewById(R.id.minimarketView);
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
                                String shopCategory = document.getString("shop_cat");
                                String shopLocation = document.getString("shop_location");
                                String shopEmail = document.getString("shop_email");
                                String shopPhone = document.getString("shop_phone");
                                String shopDesc = document.getString("shop_desc");

                                // Create a LinearLayout to hold the data
                                LinearLayout shopLayout = new LinearLayout(MiniMarket.this);
                                shopLayout.setOrientation(LinearLayout.VERTICAL);

                                // Create TextViews for each data field
                                TextView nameTextView = new TextView(MiniMarket.this);
                                nameTextView.setText("Shop Name: " + shopName);

                                TextView categoryTextView = new TextView(MiniMarket.this);
                                categoryTextView.setText("Category: " + shopCategory);

                                TextView locationTextView = new TextView(MiniMarket.this);
                                locationTextView.setText("Location: " + shopLocation);

                                TextView emailTextView = new TextView(MiniMarket.this);
                                emailTextView.setText("Email: " + shopEmail);

                                TextView phoneTextView = new TextView(MiniMarket.this);
                                phoneTextView.setText("Phone: " + shopPhone);

                                TextView descTextView = new TextView(MiniMarket.this);
                                descTextView.setText("Description: " + shopDesc);

                                // Set an OnClickListener for the LinearLayout
                                shopLayout.setOnClickListener(v -> {
                                    Intent intent = new Intent(MiniMarket.this, ShopDisplay.class);
                                    intent.putExtra("shopID", shopID);
                                    intent.putExtra("shopName", shopName);
                                    intent.putExtra("shopCategory", shopCategory);
                                    intent.putExtra("shopLocation", shopLocation);
                                    intent.putExtra("shopEmail", shopEmail);
                                    intent.putExtra("shopPhone", shopPhone);
                                    intent.putExtra("shopDesc", shopDesc);
                                    startActivity(intent);
                                });

                                // Add TextViews to the LinearLayout
                                shopLayout.addView(nameTextView);
                                shopLayout.addView(categoryTextView);
                                shopLayout.addView(locationTextView);
                                shopLayout.addView(emailTextView);
                                shopLayout.addView(phoneTextView);
                                shopLayout.addView(descTextView);

                                // Add the LinearLayout to the container
                                container.addView(shopLayout);
                            }
                        } else {
                            Log.e("Mini Market", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}