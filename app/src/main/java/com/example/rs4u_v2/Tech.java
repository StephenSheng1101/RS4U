package com.example.rs4u_v2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

public class Tech extends AppCompatActivity {

    FrameLayout filter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tech);
        filter = findViewById(R.id.bottomFrameLayout);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Button to a new activity for user to enter location information
                Intent intent = new Intent(Tech.this, Filter.class);
                startActivity(intent);
            }
        });
    }
}