package com.example.rs4u_v2;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.rs4u_v2.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private TextView cat1;
    private TextView cat2;
    private TextView cat3;
    private TextView cat4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cat1 = findViewById(R.id.cat1);
        cat1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // hhhButton to a new activity for user to enter location information
                Intent intent = new Intent(MainActivity.this, MiniMarket.class);
                startActivity(intent);
            }
        });

        cat2 = findViewById(R.id.cat2);
        cat2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Button to a new activity for user to enter location information
                Intent intent = new Intent(MainActivity.this, Healthcare.class);
                startActivity(intent);
            }
        });
        cat3 = findViewById(R.id.cat3);
        cat3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Button to a new activity for user to enter location information
                Intent intent = new Intent(MainActivity.this, Stationary.class);
                startActivity(intent);
            }
        });
        cat4 = findViewById(R.id.cat4);
        cat4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Button to a new activity for user to enter location information
                Intent intent = new Intent(MainActivity.this, Tech.class);
                startActivity(intent);
            }
        });

    }
}