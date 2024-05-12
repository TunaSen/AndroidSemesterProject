package com.example.semesterproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class courses extends AppCompatActivity {

    Button newCourse;

    LinearLayout LayoutAllCourses;
    private SharedPreferences sharedPreferences;
    FirebaseFirestore db = FirebaseFirestore.getInstance();







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_courses);

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String role = sharedPreferences.getString("role","std");
        newCourse=findViewById(R.id.buttonNewCourse);
        Toast.makeText(getApplicationContext(), "Rolünüz " + role ,Toast.LENGTH_SHORT).show();
        LinearLayout layoutNewCourse = findViewById(R.id.LayoutNewCourse);
        LayoutAllCourses=findViewById(R.id.LayoutAllCourses);



        db.collection("courses").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            // courses_inf_card.xml dosyasından kartın görünümünü oluştur
                            View cardView = LayoutInflater.from(courses.this).inflate(R.layout.courses_info_card, LayoutAllCourses, false);

                            // Kartın içindeki TextView nesnelerini bul ve verileri atayın
                            TextView textViewCourseName = cardView.findViewById(R.id.textViewCourseName);
                            textViewCourseName.setText(document.getString("name"));


                            TextView textViewCourseInstructor = cardView.findViewById(R.id.textViewCourseInstructor);
                            textViewCourseInstructor.setText(document.getString("mainInsName"));



                            TextView textViewCourseId = cardView.findViewById(R.id.textViewCourseId);
                            textViewCourseId.setText(document.getString("courseId"));

                            TextView textViewCourseDate = cardView.findViewById(R.id.textViewCourseDate);
                            textViewCourseDate.setText(document.getString("courseDay"));


                            TextView textViewSemester = cardView.findViewById(R.id.textViewSemester);
                            textViewSemester.setText(document.getString("semester"));




                            TextView textViewCourseStatus = cardView.findViewById(R.id.textViewCourseStatus);
                            textViewCourseStatus.setText(document.getString("status"));



                            cardView.setOnClickListener(v -> {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("courseId", document.getString("courseId"));
                                editor.putString("courseName", document.getString("name"));
                                editor.putString("courseDate", document.getString("courseDay"));
                                editor.putString("status", document.getString("status"));
                                editor.putString("semester", document.getString("semester"));
                                editor.apply(); // Don't forget to apply changes

                                // Start CourseDetail activity
                                Intent intent = new Intent(getApplicationContext(), CourseDetail.class);
                                startActivity(intent);
                            });

                            // LayoutAllCourses içine kartı ekle
                            LayoutAllCourses.addView(cardView);
                        }
                    }
                }
            }
        });









        if (role.equals("std")){
            layoutNewCourse.setVisibility(View.GONE);
            layoutNewCourse.setEnabled(false);
        }





        newCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(),newCourse.class);
                startActivity(intent);
            }
        });









        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}