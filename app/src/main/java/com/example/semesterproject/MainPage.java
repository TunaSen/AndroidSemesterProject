package com.example.semesterproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MainPage extends AppCompatActivity {


    ImageView profileImage;
    BottomNavigationView bottomNavigationView;

    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_page);

        bottomNavigationView = findViewById(R.id.bottomNavView);
        profileImage=findViewById(R.id.imageProfile);
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        Picasso.get().load(sharedPreferences.getString("profileImageUrl","https://isobarscience-1bfd8.kxcdn.com/wp-content/uploads/2020/09/default-profile-picture1.jpg")).into(profileImage);





        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(),Account.class);
                startActivity(intent);

            }
        });

        bottomNavigationView = findViewById(R.id.bottomNavView);
        if (bottomNavigationView != null) {
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    // Öğelerden herhangi birine tıklandığında yapılacak işlemleri burada tanımlayın
                    int ItemId = item.getItemId();

                    if (ItemId == R.id.menu_home) {

                        Toast.makeText(getApplicationContext(), "Home clicked", Toast.LENGTH_SHORT).show();

                    } else if (ItemId == R.id.courses) {
                        Toast.makeText(getApplicationContext(), "menu_search clicked", Toast.LENGTH_SHORT).show();
                        Intent intent= new Intent(getApplicationContext(),courses.class);
                        startActivity(intent);

                    }else if (ItemId == R.id.users) {
                        Toast.makeText(getApplicationContext(), "users clicked", Toast.LENGTH_SHORT).show();
                        Intent intent= new Intent(getApplicationContext(),UserList.class);
                        startActivity(intent);


                    }else if (ItemId == R.id.reporting) {
                        if(sharedPreferences.getString("role","").equals("std")){
                            Intent intent= new Intent(getApplicationContext(),StudentReporting.class);
                            startActivity(intent);
                        }
                        else{
                            Intent intent= new Intent(getApplicationContext(),ListingReports.class);
                            startActivity(intent);
                        }



                    }
                    return false;
                }
            });
        }





        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}