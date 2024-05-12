package com.example.semesterproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AddStd extends AppCompatActivity {
    LinearLayout AddStudentListingStudentsLayout;
    Button AddStudentButtonUpdate,AddStudentButtonAddWithCSV;

    EditText editTextSearchStudent;


    final static String StdRegex="^[\\w\\-\\.]+@std\\.yildiz\\.edu\\.tr$";

    private static final int PICK_CSV_FILE_REQUEST_CODE = 123;

    List<HashMap<String,String>> StudentList = new ArrayList<>();
    List<HashMap<String,String>> AssignedStudentList = new ArrayList<>();

    private SharedPreferences sharedPreferences;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_std);
        AddStudentButtonUpdate=findViewById(R.id.AddStudentButtonUpdate);
        AddStudentListingStudentsLayout=findViewById(R.id.AddStudentListingStudentsLayout);

        AddStudentButtonAddWithCSV=findViewById(R.id.AddStudentButtonAddWithCSV);

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);


        AddStudentButtonAddWithCSV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("text/csv"); // Sadece CSV dosyalarını seçmek için

                // Dosya seçici uygulamasını başlat
                startActivityForResult(intent, PICK_CSV_FILE_REQUEST_CODE);
            }
        });



        Map<String, ?> allEntries = sharedPreferences.getAll();

        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.d("SharedPreferences", entry.getKey() + ": " + entry.getValue().toString());
        }
        AddStudentButtonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("users-groups")
                        .whereEqualTo("courseId", sharedPreferences.getString("courseId", ""))
                        .whereEqualTo("groupNo", sharedPreferences.getString("CouseAddStdGroupNo", ""))
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    for (HashMap<String, String> student : StudentList) {
                                        if (document.get("userMail").equals(student.get("email"))) {
                                            if (student.get("isActive").equals("red")) {
                                                // Öğrenci veritabanından silinmeli
                                                db.collection("users-groups").document(document.getId())
                                                        .delete()
                                                        .addOnSuccessListener(aVoid -> Log.d("TAG", "DocumentSnapshot successfully deleted"))
                                                        .addOnFailureListener(e -> Log.w("TAG", "Error deleting document", e));
                                            }
                                        }
                                    }

                                }
                                for (HashMap<String, String> student : StudentList){
                                    Log.d("TAG", "Student listekleme"+ student.get("isActive"));

                                    if (student.get("isActive").equals("green")) {
                                        // Yeni öğrenci veritabanına eklenmeli
                                        Map<String, Object> newStudent = new HashMap<>();
                                        newStudent.put("courseId", sharedPreferences.getString("courseId", ""));
                                        newStudent.put("groupNo", sharedPreferences.getString("CouseAddStdGroupNo", ""));
                                        newStudent.put("userMail", student.get("email"));
                                        Log.d("TAG333333", "Student is already assigned to this group");

                                        db.collection("users-groups")
                                                .whereEqualTo("courseId", sharedPreferences.getString("courseId", ""))
                                                .whereEqualTo("groupNo", sharedPreferences.getString("CouseAddStdGroupNo", ""))
                                                .whereEqualTo("userMail", student.get("email"))
                                                .get()
                                                .addOnCompleteListener(task2 -> {
                                                    if (task2.isSuccessful()) {


                                                        if (task2.getResult().isEmpty()) {
                                                            // Belirtilen öğrenci zaten bu gruba atanmamış, ekleme işlemi yapılır
                                                            db.collection("users-groups")
                                                                    .add(newStudent)
                                                                    .addOnSuccessListener(documentReference -> Log.d("TAG", "DocumentSnapshot added with ID: " + documentReference.getId()))
                                                                    .addOnFailureListener(e -> Log.w("TAG", "Error adding document", e));
                                                        } else {
                                                            // Belirtilen öğrenci zaten bu gruba atanmış, eklememe işlemi yapılır
                                                            Log.d("TAG", "Student is already assigned to this group");
                                                        }
                                                    } else {
                                                        Log.d("TAG", "Error getting documents: ", task.getException());
                                                    }
                                                });
                                    }
                                }
                            } else {
                                Log.d("TAG", "Error getting documents: ", task.getException());
                            }
                        });
            }
        });



        //tum studentleri okuma
        Pattern pattern = Pattern.compile(StdRegex);
        db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String email = document.getString("email");
                            Matcher matcher = pattern.matcher(email);
                            if (matcher.matches()){
                                String name = document.getString("name");
                                String stdId = document.getString("stdId");
                                String surname= document.getString("surname");
                                String id = document.getId();
                                String education = document.getString("education");
                                Log.d("User", id + " " + name + " " + stdId + " " + email);
                                HashMap<String, String> userInfo = new HashMap<>();

                                userInfo.put("id", id);
                                userInfo.put("name", name);
                                userInfo.put("surname", surname);
                                userInfo.put("stdId", stdId);
                                userInfo.put("email", email);
                                userInfo.put("education", education);
                                userInfo.put("isActive", "red");
                                // HashMap'i StudentList'e ekle
                                StudentList.add(userInfo);
                            }
                        }
                        db.collection("users-groups")
                                .whereEqualTo("courseId",sharedPreferences.getString("courseId",""))
                                .get()
                                .addOnCompleteListener(task2 -> {
                                    if (task2.isSuccessful()) {
                                        for (QueryDocumentSnapshot document2 : task2.getResult()) {
                                            String AssignedUser_groupNo=document2.getString("groupNo");
                                            String AssignedUser_userMail=document2.getString("userMail");
                                            HashMap<String, String> AssignedUserInfo = new HashMap<>();
                                            AssignedUserInfo.put("groupNo",AssignedUser_groupNo);
                                            AssignedUserInfo.put("userMail",AssignedUser_userMail);
                                            AssignedStudentList.add(AssignedUserInfo);
                                        }
                                        for (HashMap<String, String> student : StudentList) {
                                            String studentEmail = student.get("email");
                                            String studentName = student.get("name");
                                            String studentSurname = student.get("surname");
                                            String stdId = student.get("stdId");
                                            String education = student.get("education");
                                            String id=student.get("id");

                                            View cardView = LayoutInflater.from(AddStd.this).inflate(R.layout.add_student_student_card, AddStudentListingStudentsLayout, false);
                                            cardView.setTag(id);

                                            TextView TextViewUserStudentName= cardView.findViewById(R.id.TextViewUserStudentName);
                                            TextViewUserStudentName.setText(studentName+" "+studentSurname);

                                            TextView TextViewStudentId= cardView.findViewById(R.id.TextViewStudentId);
                                            TextViewStudentId.setText(stdId);

                                            TextView textViewCourseStdEducation= cardView.findViewById(R.id.textViewCourseStdEducation);
                                            textViewCourseStdEducation.setText(education);

                                            for (HashMap<String, String> assignedStudent : AssignedStudentList) {
                                                String assignedStudentEmail = assignedStudent.get("userMail");
                                                if (assignedStudentEmail != null && assignedStudentEmail.equals(studentEmail)) {
                                                    String assignedGroupNo = assignedStudent.get("groupNo");
                                                    if (assignedGroupNo.equals(sharedPreferences.getString("CouseAddStdGroupNo",""))){
                                                        student.put("isActive", "green");
                                                        break;
                                                    }
                                                    student.put("isActive", "yellow");
                                                }
                                            }
                                            String isActive = student.get("isActive");
                                            Button buttonGroupOgrenciEkle=cardView.findViewById(R.id.buttonGroupOgrenciEkle);



                                            ImageView ImageViewCheckedStatus=cardView.findViewById(R.id.ImageViewCheckedStatus);

                                            if (isActive != null) {
                                                switch (isActive) {
                                                    case "green":
                                                        ImageViewCheckedStatus.setImageResource(R.drawable.green_checked);

                                                        break;
                                                    case "yellow":
                                                        buttonGroupOgrenciEkle.setEnabled(false);

                                                        ImageViewCheckedStatus.setImageResource(R.drawable.yellow_checked);
                                                        break;
                                                    default:
                                                        ImageViewCheckedStatus.setImageResource(R.drawable.not_assigned);
                                                        break;
                                                }
                                            } else {
                                                ImageViewCheckedStatus.setImageResource(R.drawable.not_assigned);
                                            }


                                            buttonGroupOgrenciEkle.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    if (student.get("isActive").equals("green")){
                                                        student.put("isActive","red");
                                                        ImageViewCheckedStatus.setImageResource(R.drawable.not_assigned);

                                                    }
                                                    else if (student.get("isActive").equals("red")){
                                                        student.put("isActive","green");
                                                        ImageViewCheckedStatus.setImageResource(R.drawable.green_checked);

                                                    }

                                                }
                                            });




                                            AddStudentListingStudentsLayout.addView(cardView);
                                        }
                                    }
                                });
                    } else {
                        // Hata durumunda işlemler
                    }
                });













        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Dosya seçme işlemi tamamlandı mı kontrol et
        if (requestCode == PICK_CSV_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            // Seçilen dosyanın URI'sini al
            Uri selectedFileUri = data.getData();

            // CSV dosyasını okuma işlemi
            try {
                InputStream inputStream = getContentResolver().openInputStream(selectedFileUri);
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    // İlk satırı okuma, diğerlerinden başla
                    String[] values = line.split(",");

                    // İlk değer courseId, ikinci değer groupNo, üçüncü değer email
                    String courseId = values[0];
                    String groupNo = values[1];
                    String email = values[2];
                    Log.w("TAGHHHHHH", courseId + groupNo + email);

                    // users-groups Firestore'a kaydetme işlemi
                    Map<String, Object> newStudent = new HashMap<>();
                    newStudent.put("courseId", courseId);
                    newStudent.put("groupNo", groupNo);
                    newStudent.put("userMail", email);

                    db.collection("users-groups")
                            .whereEqualTo("courseId", courseId)
                            .whereEqualTo("groupNo", groupNo)
                            .whereEqualTo("userMail", email)
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // Sorgu sonucunda belge bulunamadıysa ekleme işlemini gerçekleştir
                                    if (task.getResult().isEmpty()) {
                                        db.collection("users-groups")
                                                .add(newStudent)
                                                .addOnSuccessListener(documentReference -> {
                                                    Toast.makeText(getApplicationContext(), "CSV dosyası başarıyla yüklendi", Toast.LENGTH_SHORT).show();
                                                    Log.d("TAG", "DocumentSnapshot added with ID: " + documentReference.getId());
                                                })
                                                .addOnFailureListener(e -> Log.w("TAG", "Error adding document", e));
                                    } else {
                                        // Eğer belge bulunduysa, ekleme işlemi yapılmaz ve kullanıcıya bilgi verilir
                                        Toast.makeText(getApplicationContext(), "Bu değerlerle aynı olan bir belge zaten var", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    // Sorguda bir hata oluştuğunda kullanıcıya bilgi verilir
                                    Toast.makeText(getApplicationContext(), "Sorgu yapılırken bir hata oluştu", Toast.LENGTH_SHORT).show();
                                    Log.e("TAG", "Error getting documents: ", task.getException());
                                }
                            });

                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}