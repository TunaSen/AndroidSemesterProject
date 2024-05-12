package com.example.semesterproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class newCourse extends AppCompatActivity {
    Button saveNewCourse, findCourse;
    EditText editTextCourseId, editTextSearchCourseId, editTextSemester, editTextCourseDay, editTextStatus, editTextCourseName;
    private SharedPreferences sharedPreferences;

    String BaseCourseId="";
    Boolean searchStatus = true;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_course);
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        saveNewCourse = findViewById(R.id.ButtonNewCourseSaveNewCourse);
        findCourse = findViewById(R.id.ButtonNewCourseSearch); // You had a typo in the ID
        editTextSearchCourseId = findViewById(R.id.editTextSearchCourseId);
        editTextCourseId = findViewById(R.id.editTextCourseId);
        editTextSemester = findViewById(R.id.editTextSemester);
        editTextCourseDay = findViewById(R.id.editTextCourseDay);
        editTextStatus = findViewById(R.id.editTextStatus);
        editTextCourseName = findViewById(R.id.editTextCourseName);
        findCourse.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));

        findCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchStatus) {
                    findCourse.setText("Geri");
                    findCourse.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
                    saveNewCourse.setText("Guncelle");
                    editTextCourseId.setText(editTextSearchCourseId.getText().toString());
                    editTextCourseId.setFocusable(false);
                    db.collection("courses")
                            .whereEqualTo("courseId", editTextSearchCourseId.getText().toString())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        QuerySnapshot querySnapshot = task.getResult();
                                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                                Log.d("TAG", "Course found - ID: " + document.getId());
                                                Toast.makeText(newCourse.this, "BU kurs daha once kayıtlıdır", Toast.LENGTH_SHORT).show();
                                                editTextSemester.setText(document.getString("semester"));
                                                editTextCourseDay.setText(document.getString("courseDay"));
                                                editTextStatus.setText(document.getString("status"));
                                                editTextCourseName.setText(document.getString("name"));
                                                BaseCourseId=document.getId();
                                            }
                                        } else {
                                            Toast.makeText(newCourse.this, "BU kurs Bulunamadi", Toast.LENGTH_SHORT).show();
                                            editTextSemester.setText("");
                                            editTextCourseDay.setText("");
                                            editTextStatus.setText("");
                                            editTextCourseName.setText("");
                                        }
                                    }
                                    searchStatus = false;
                                }
                            });
                } else {
                    findCourse.setText("Search");
                    findCourse.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                    saveNewCourse.setText("Yeni olustur");
                    editTextCourseId.setText(editTextSearchCourseId.getText().toString());
                    editTextCourseId.setFocusable(true);
                    editTextSemester.setText("");
                    editTextCourseDay.setText("");
                    editTextStatus.setText("");
                    editTextCourseName.setText("");
                    searchStatus = true;
                }
            }
        });


        saveNewCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!searchStatus) {
                    // Güncelleme işlemi için
                    String courseId = editTextCourseId.getText().toString();
                    String semester = editTextSemester.getText().toString();
                    String courseDay = editTextCourseDay.getText().toString();
                    String status = editTextStatus.getText().toString();
                    String courseName = editTextCourseName.getText().toString();

                    // Güncellenecek verileri Firestore'a gönderme
                    db.collection("courses").document(BaseCourseId)
                            .update(new HashMap<String, Object>() {
                                {
                                    put("semester", semester);
                                    put("courseDay", courseDay);
                                    put("status", status);
                                    put("name", courseName);
                                    put("courseId", courseId);
                                    //put("mainInsId", sharedPreferences.getString("id", ""));
                                    //put("mainInsName",  sharedPreferences.getString("name", ""));

                                }
                            })
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Güncelleme başarılı olduğunda yapılacak işlemler
                                    Log.d("TAG", "Güncelleme başarılı");
                                    Toast.makeText(newCourse.this, "Kurs başarıyla güncellendi", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Güncelleme başarısız olduğunda yapılacak işlemler
                                    Log.w("TAG", "Güncelleme başarısız", e);
                                    Toast.makeText(newCourse.this, "Kurs güncellenirken bir hata oluştu", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    // Yeni kurs ekleme işlemi için
                    String courseId = editTextCourseId.getText().toString();
                    String semester = editTextSemester.getText().toString();
                    String courseDay = editTextCourseDay.getText().toString();
                    String status = editTextStatus.getText().toString();
                    String courseName = editTextCourseName.getText().toString();

                    // Yeni verileri Firestore'a gönderme
                    db.collection("courses").document()
                            .set(new HashMap<String, Object>() {
                                {
                                    put("semester", semester);
                                    put("courseDay", courseDay);
                                    put("status", status);
                                    put("name", courseName);
                                    put("courseId", courseId);
                                    put("mainInsId", sharedPreferences.getString("id", ""));
                                    put("mainInsName",  sharedPreferences.getString("name", "")+ " "+sharedPreferences.getString("surname", ""));

                                }
                            })
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Ekleme başarılı olduğunda yapılacak işlemler
                                    Log.d("TAG", "Ekleme başarılı");
                                    Toast.makeText(newCourse.this, "Yeni kurs başarıyla eklendi", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Ekleme başarısız olduğunda yapılacak işlemler
                                    Log.w("TAG", "Ekleme başarısız", e);
                                    Toast.makeText(newCourse.this, "Yeni kurs eklenirken bir hata oluştu", Toast.LENGTH_SHORT).show();
                                }
                            });

                }
            }
        });



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
