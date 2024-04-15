package com.example.semesterproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    TextView TextChangeSignUpArea, TextChangeLogInArea;
    Button LoginButton, SignUpButton;
    ScrollView LoginScrollView , SignUpScrollView;

    TextInputEditText editTextLoginEmail,editTextLoginPassword;
    TextInputEditText editTextPassword,editTextName,editTextSurname,editTextStudentID,editTextEmail;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    final static String InsRegex="^[\\w\\-\\.]+@yildiz\\.edu\\.tr$";
    final static String StdRegex="^[\\w\\-\\.]+@std\\.yildiz\\.edu\\.tr$";

    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        TextChangeSignUpArea = findViewById(R.id.TextChangeSignUpArea);
        TextChangeLogInArea=findViewById(R.id.TextChangeLogInArea);
        LoginButton=findViewById(R.id.buttonLogin);
        SignUpButton=findViewById(R.id.buttonRegister);

        LoginScrollView=findViewById(R.id.scrollViewLogin);
        SignUpScrollView=findViewById(R.id.scrollViewRegister);

        editTextLoginEmail = findViewById(R.id.editTextLoginEmail);
        editTextLoginPassword = findViewById(R.id.editTextLoginPassword);

        editTextPassword = findViewById(R.id.editTextPassword);
        editTextName = findViewById(R.id.editTextName);
        editTextSurname = findViewById(R.id.editTextSurname);
        editTextStudentID = findViewById(R.id.editTextStudentID);
        editTextEmail = findViewById(R.id.editTextEmail);
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);





        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                db.collection("users")
                        .whereEqualTo("email", editTextLoginEmail.getText().toString())
                        .whereEqualTo("password", editTextLoginPassword.getText().toString())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    QuerySnapshot querySnapshot = task.getResult();
                                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                        // Kullanıcı bulundu
                                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                            String userId = document.getId();
                                            String userEmail = document.getString("email");
                                            String userPassword = document.getString("password");
                                            Log.d("TAG", "User found - ID: " + userId + ", Email: " + userEmail + ", Password: " + userPassword);

                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("id", String.valueOf(document.getId()));

                                            editor.putString("email", String.valueOf(document.getString("email")));
                                            editor.putString("name", String.valueOf(document.getString("name")));
                                            editor.putString("surname", String.valueOf(document.getString("surname")));
                                            editor.putString("password", String.valueOf(document.getString("password")));
                                            editor.putString("stdId", String.valueOf(document.getString("stdId")));

                                            // Varsayılan değerler
                                            String defaultStringValue = ""; // String değerler için varsayılan değer
                                            int defaultIntValue = 1; // Integer değerler için varsayılan değer

// Firestore'dan alınacak değerler

                                            String tel = document.getString("tel") != null ? document.getString("tel") : defaultStringValue;
                                            String instagram = document.getString("instagram") != null ? document.getString("instagram") : defaultStringValue;
                                            String twitter = document.getString("twitter") != null ? document.getString("twitter") : defaultStringValue;
                                            String facebook = document.getString("facebook") != null ? document.getString("facebook") : defaultStringValue;
                                            String education = document.getString("education") != null ? document.getString("education") : defaultStringValue;
                                            String profileImageUrl = document.getString("profileImageUrl") != null ? document.getString("profileImageUrl") : "https://isobarscience-1bfd8.kxcdn.com/wp-content/uploads/2020/09/default-profile-picture1.jpg";
                                            String role = document.getString("role") != null ? document.getString("role") : defaultStringValue;

                                            int facebookActive = document.getLong("facebookActive") != null ? document.getLong("facebookActive").intValue() : defaultIntValue;
                                            int mailActive = document.getLong("mailActive") != null ? document.getLong("mailActive").intValue() : defaultIntValue;
                                            int twitterActive = document.getLong("twitterActive") != null ? document.getLong("twitterActive").intValue() : defaultIntValue;
                                            int telActive = document.getLong("telActive") != null ? document.getLong("telActive").intValue() : defaultIntValue;
                                            int instagramActive = document.getLong("instagramActive") != null ? document.getLong("instagramActive").intValue() : defaultIntValue;

                                            editor.putString("tel", tel);
                                            editor.putString("instagram", instagram);
                                            editor.putString("twitter", twitter);
                                            editor.putString("facebook", facebook);
                                            editor.putString("education", education);
                                            editor.putString("profileImageUrl", profileImageUrl);
                                            editor.putString("role", role);


                                            editor.putInt("facebookActive", facebookActive);
                                            editor.putInt("mailActive", mailActive);
                                            editor.putInt("twitterActive", twitterActive);
                                            editor.putInt("telActive", telActive);
                                            editor.putInt("instagramActive", instagramActive);
                                            editor.apply();





                                            Toast.makeText(MainActivity.this,"User  found",Toast.LENGTH_SHORT).show();
                                            Intent intent= new Intent(getApplicationContext(),MainPage.class);
                                            startActivity(intent);

                                        }
                                    } else {
                                        // Kullanıcı bulunamadı
                                        Log.d("TAG", "User not found");
                                        Toast.makeText(MainActivity.this,"User not found",Toast.LENGTH_SHORT).show();


                                    }
                                } else {
                                    Log.w("TAG", "Error getting users from Firestore", task.getException());
                                    Toast.makeText(MainActivity.this,"Error getting users from Firestore",Toast.LENGTH_SHORT).show();

                                }
                            }
                        });




            }
        });
        SignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String role=checkYildizEmail(editTextEmail.getText().toString());
                if (role != null) {


                    HashMap<String, String> newUser = new HashMap<>();
                    newUser.put("name", Objects.requireNonNull(editTextName.getText()).toString());
                    newUser.put("surname", Objects.requireNonNull(editTextSurname.getText()).toString());
                    newUser.put("email", Objects.requireNonNull(editTextEmail.getText()).toString());
                    newUser.put("stdId", Objects.requireNonNull(editTextStudentID.getText()).toString());
                    newUser.put("password", Objects.requireNonNull(editTextPassword.getText()).toString());

                    db.collection("users")
                            .add(newUser)
                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("TAG", "User added with ID: " + task.getResult().getId());
                                        Toast.makeText(MainActivity.this, "Kayıt Başarılı", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Log.w("TAG", "Error adding user to Firestore", task.getException());
                                        Toast.makeText(MainActivity.this, "Kayıt Başarısız", Toast.LENGTH_SHORT).show();

                                    }
                                    return ;
                                }
                            });


                }
                else {
                    Toast.makeText(MainActivity.this, "Sadece Yıldız Maillerinin Kaydı yapılmaktadır", Toast.LENGTH_SHORT).show();

                }
            }
        });





        TextChangeSignUpArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginScrollView.setVisibility(View.INVISIBLE);
                SignUpScrollView.setVisibility(View.VISIBLE);
            }
        });
        TextChangeLogInArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginScrollView.setVisibility(View.VISIBLE);
                SignUpScrollView.setVisibility(View.INVISIBLE);
            }
        });





        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public static String checkYildizEmail(String email) {


        // Deseni derle
        Pattern pattern = Pattern.compile(InsRegex);

        // E-posta adresini eşleştir
        Matcher matcher = pattern.matcher(email);

        if (matcher.matches()){
            return "ins";
        }
        pattern = Pattern.compile(StdRegex);
        matcher = pattern.matcher(email);
        if (matcher.matches()){
            return "std";
        }

        return null;
    }
}