package com.example.semesterproject;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StudentReporting extends AppCompatActivity {
    final static String StdRegex="^[\\w\\-\\.]+@std\\.yildiz\\.edu\\.tr$";


     EditText editTextSubjectNewReport,editTextMessageNewReport;
     Spinner spinnerScopeNewReport,spinnerRecipientNewReport,spinnerCourseIdNewReport;

     List<String> Courses= new ArrayList<>() ;

     List<String> Instructors=new ArrayList<String>() ;




     Button NewReportButtonSend,NewReportButtonMailSend;
    private SharedPreferences sharedPreferences;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_reporting);
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        spinnerRecipientNewReport=findViewById(R.id.spinnerRecipientNewReport);
        spinnerCourseIdNewReport=findViewById(R.id.spinnerCourseIdNewReport);
        editTextSubjectNewReport=findViewById(R.id.editTextSubjectNewReport);
        editTextMessageNewReport=findViewById(R.id.editTextMessageNewReport);
        spinnerScopeNewReport=findViewById(R.id.spinnerScopeNewReport);

        NewReportButtonSend=findViewById(R.id.NewReportButtonSend);
        NewReportButtonMailSend=findViewById(R.id.NewReportButtonMailSend);
        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.d("SharedPreferences", entry.getKey() + ": " + entry.getValue().toString());
        }


        //db den tum users icinden email degerleri @std.yildiz.edu.tr olmayan maileri Instructors lsitesine ekle
        //db den tum couses lerin courseId degerlerini aal ve Courses listesine ekjle
        db.collection("courses")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Add the courseId to the Courses collection
                            Courses.add(document.getString("courseId"));

                        }
                        Courses.add("None");
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Courses);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerCourseIdNewReport.setAdapter(adapter);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });

        db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Add the courseId to the Courses collection
                            Pattern pattern = Pattern.compile(StdRegex);

                            // E-posta adresini eşleştir
                            Matcher matcher = pattern.matcher(document.getString("email"));

                            if (!matcher.matches()){//student degil
                                Instructors.add(document.getString("email"));
                            }

                        }
                        Instructors.add("None");
                        Instructors.add("All");
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Instructors);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerRecipientNewReport.setAdapter(adapter);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });





        NewReportButtonMailSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, spinnerRecipientNewReport.getSelectedItem().toString());
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, editTextMessageNewReport.getText().toString());
                emailIntent.putExtra(Intent.EXTRA_TEXT, editTextMessageNewReport.getText().toString());

                try {
                    // E-posta gönderme niyetini başlat
                    startActivity(Intent.createChooser(emailIntent, "E-posta gönder"));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(StudentReporting.this, "E-posta istemcisi bulunamadı.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        NewReportButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String recipient = spinnerRecipientNewReport.getSelectedItem().toString();
                String courseId = spinnerCourseIdNewReport.getSelectedItem().toString();
                String subject = editTextSubjectNewReport.getText().toString();
                String scope = spinnerScopeNewReport.getSelectedItem().toString();
                String content= editTextMessageNewReport.getText().toString();

                if (content.isEmpty()|| recipient.isEmpty() || courseId.isEmpty() || subject.isEmpty() || scope.isEmpty()) {
                    // Boş bir alan varsa, uygun bir işlem yapabilirsiniz, örneğin kullanıcıya uyarı gösterebilirsiniz.
                    // Örnek:
                    Toast.makeText(getApplicationContext(), "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show();
                } else {
                // Yeni verileri Firestore'a gönderme
                    db.collection("reports").document()
                            .set(new HashMap<String, Object>() {
                                {

                                        put("recipient", spinnerRecipientNewReport.getSelectedItem().toString());
                                        put("CourseId", spinnerCourseIdNewReport.getSelectedItem().toString());
                                        put("subject", editTextSubjectNewReport.getText().toString());
                                        put("scope", spinnerScopeNewReport.getSelectedItem().toString());
                                        put("date", Timestamp.now());
                                        put("content", editTextMessageNewReport.getText().toString());
                                        put("stdMail", sharedPreferences.getString("email", ""));
                                    }


                            })
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Ekleme başarılı olduğunda yapılacak işlemler
                                    Log.d("TAG", "Ekleme başarılı");
                                    Toast.makeText(StudentReporting.this, "Yeni kurs başarıyla eklendi", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Ekleme başarısız olduğunda yapılacak işlemler
                                    Log.w("TAG", "Ekleme başarısız", e);
                                    Toast.makeText(StudentReporting.this, "Yeni kurs eklenirken bir hata oluştu", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }}
        );



















        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}