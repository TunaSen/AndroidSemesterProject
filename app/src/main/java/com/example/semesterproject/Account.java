package com.example.semesterproject;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.app.AlertDialog;

import java.util.HashMap;
import java.util.Map;


public class Account extends AppCompatActivity {

    Button buttonUpdate,buttonDelete,buttonBack;
    EditText editTextTel,editTextFacebook,editTextInstagram,editTextTwitter,editTextPassword,editTextMail,editTextEducation;
    Boolean isEditableFlag=Boolean.TRUE;

    TextView TextViewNameSurname,TextViewStdId,TextViewRole;

    ImageView IconPhone,IconWhatsapp,IconFacebook,IconInstagram,IconTwitter,IconMail;

    ImageView profileImage;
    private SharedPreferences sharedPreferences;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    ToggleButton toggleMail,toggleTel,toggleFacebook,toggleInstagram,toggleTwitter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account);

        buttonUpdate=findViewById(R.id.buttonUpdate);
        buttonDelete=findViewById(R.id.buttonDelete);
        buttonBack=findViewById(R.id.buttonBack);


        editTextTel=findViewById(R.id.editTextTel);
        editTextFacebook=findViewById(R.id.editTextFacebook);
        editTextInstagram=findViewById(R.id.editTextInstagram);
        editTextTwitter=findViewById(R.id.editTextTwitter);
        profileImage=findViewById(R.id.profileImage);
        editTextPassword=findViewById(R.id.editTextPassword);
        TextViewNameSurname=findViewById(R.id.textViewName);
        TextViewStdId=findViewById(R.id.textViewId);
        TextViewRole=findViewById(R.id.textViewRole);
        editTextMail=findViewById(R.id.editTextMail);
        editTextEducation=findViewById(R.id.editTextEducation);

        IconPhone=findViewById(R.id.IconPhone);
        IconWhatsapp=findViewById(R.id.IconWhatsapp);
        IconFacebook=findViewById(R.id.IconFacebook);
        IconInstagram=findViewById(R.id.IconInstagram);
        IconTwitter=findViewById(R.id.IconTwitter);
        IconMail=findViewById(R.id.IconMail);


        toggleMail=findViewById(R.id.toggleMail);
        toggleTel=findViewById(R.id.toggleTel);
        toggleFacebook=findViewById(R.id.toggleFacebook);
        toggleInstagram=findViewById(R.id.toggleInstagram);
        toggleTwitter=findViewById(R.id.toggleTwitter);

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        // SharedPreferences'ten kayıtlı metni al
        editTextTel.setText(sharedPreferences.getString("tel", ""));
        editTextFacebook.setText(sharedPreferences.getString("facebook", ""));
        editTextInstagram.setText(sharedPreferences.getString("instagram", ""));
        editTextTwitter.setText(sharedPreferences.getString("twitter", ""));
        editTextPassword.setText(sharedPreferences.getString("password",""));
        editTextMail.setText(sharedPreferences.getString("email", ""));
        TextViewNameSurname.setText(sharedPreferences.getString("name", "") +" "+ sharedPreferences.getString("surname", ""));
        TextViewStdId.setText(sharedPreferences.getString("stdId", ""));
        editTextEducation.setText(sharedPreferences.getString("education", ""));

        setToggleButon(toggleFacebook,sharedPreferences.getInt("facebookActive", 1));
        setToggleButon(toggleInstagram,sharedPreferences.getInt("instagramActive", 1));
        setToggleButon(toggleMail,sharedPreferences.getInt("mailActive", 1));
        setToggleButon(toggleTwitter,sharedPreferences.getInt("twitterActive", 1));
        setToggleButon(toggleTel,sharedPreferences.getInt("telActive", 1));




        Picasso.get().load(sharedPreferences.getString("profileImageUrl","https://isobarscience-1bfd8.kxcdn.com/wp-content/uploads/2020/09/default-profile-picture1.jpg")).into(profileImage);


        if (sharedPreferences.getString("role", "").equals("admin")){
            TextViewRole.setText("Admin");
            TextViewStdId.setText("");


        }
        else {
            String email = sharedPreferences.getString("email", "");
            if (!email.equals("")){
                String role = MainActivity.checkYildizEmail(email);
                if (role.equals("std")){
                    TextViewRole.setText("Student");
                    TextViewStdId.setText(sharedPreferences.getString("stdId", ""));

                }
                else if (role.equals("ins")){
                    TextViewRole.setText("Instructor");
                    TextViewStdId.setText("");

                }
                else {
                    Toast.makeText(Account.this,"Email alma başarısız",Toast.LENGTH_SHORT).show();

                }
            }
        }

buttonBack.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent= new Intent(getApplicationContext(),MainPage.class);
        startActivity(intent);
    }
});

buttonDelete.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Account.this);
        builder.setMessage("Are you sure you want to delete?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.collection("users").document(String.valueOf(sharedPreferences.getString("id","")))
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Deletion successful
                                        Intent intent= new Intent(getApplicationContext(),MainActivity.class);
                                        startActivity(intent);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Deletion failed
                                        Toast.makeText(Account.this, "Error deleting document: " , Toast.LENGTH_SHORT).show();

                                    }
                                });

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Kullanıcı silme işlemini iptal etti.
                        Toast.makeText(Account.this, "Delete canceled", Toast.LENGTH_SHORT).show();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
});


        IconPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);

                intent.setData(Uri.parse("tel:" + editTextTel.getText()));
                startActivity(intent);

            }
        });
        IconWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("whatsapp://send?phone=" + editTextTel.getText());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);

            }
        });
        IconFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // Facebook'un uygulama şeması
                    Uri uri = Uri.parse("fb://profile/" + editTextFacebook.getText());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    // Facebook uygulaması yoksa tarayıcı üzerinden aç
                    Uri uri = Uri.parse("http://www.facebook.com/" + editTextFacebook.getText());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            }
        });
        IconInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://instagram.com/_u/" + editTextInstagram.getText());
                Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

                likeIng.setPackage("com.instagram.android");

                startActivity(likeIng);
            }

        });

        IconTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Uri uri = Uri.parse("twitter://user?user_id=" + editTextTwitter.getText());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);

            }
        });

        IconMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);

                intent.setData(Uri.parse("mailto:"+editTextMail.getText().toString()));
                startActivity(intent);


            }
        });

        toggleMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ToggleButton üzerine tıklandığında yapılacak işlemler
                if (toggleMail.isChecked()) {
                    toggleMail.setBackgroundColor(Color.rgb(0,255,0));
                    toggleMail.setText("Public");
                } else {
                    toggleMail.setBackgroundColor(Color.rgb(255,0,0));
                    toggleMail.setText("Private");
                }
            }
        });
        toggleFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ToggleButton üzerine tıklandığında yapılacak işlemler
                if (toggleFacebook.isChecked()) {
                    toggleFacebook.setBackgroundColor(Color.rgb(0,255,0));
                    toggleFacebook.setText("Public");
                } else {
                    toggleFacebook.setBackgroundColor(Color.rgb(255,0,0));
                    toggleFacebook.setText("Private");
                }
            }
        });
        toggleTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ToggleButton üzerine tıklandığında yapılacak işlemler
                if (toggleTwitter.isChecked()) {
                    toggleTwitter.setBackgroundColor(Color.rgb(0,255,0));
                    toggleTwitter.setText("Public");
                } else {
                    toggleTwitter.setBackgroundColor(Color.rgb(255,0,0));
                    toggleTwitter.setText("Private");
                }
            }
        });
        toggleTel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ToggleButton üzerine tıklandığında yapılacak işlemler
                if (toggleTel.isChecked()) {
                    toggleTel.setBackgroundColor(Color.rgb(0,255,0));
                    toggleTel.setText("Public");
                } else {
                    toggleTel.setBackgroundColor(Color.rgb(255,0,0));
                    toggleTel.setText("Private");
                }
            }
        });
        toggleInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ToggleButton üzerine tıklandığında yapılacak işlemler
                if (toggleInstagram.isChecked()) {
                    toggleInstagram.setBackgroundColor(Color.rgb(0,255,0));
                    toggleInstagram.setText("Public");
                } else {
                    toggleInstagram.setBackgroundColor(Color.rgb(255,0,0));
                    toggleInstagram.setText("Private");
                }
            }
        });










        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEditableFlag){
                    Toast.makeText(Account.this,String.valueOf(profileImage.isClickable()),Toast.LENGTH_SHORT).show();
                    Intent intent= new Intent(getApplicationContext(),TakePicture.class);
                    startActivity(intent);
                }

            }
        });


        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEditableFlag){
                    editTextTel.setEnabled(true);
                    editTextFacebook.setEnabled(true);
                    editTextInstagram.setEnabled(true);
                    editTextTwitter.setEnabled(true);
                    editTextPassword.setEnabled(true);
                    editTextEducation.setEnabled(true);

                    toggleFacebook.setEnabled(true);
                    toggleInstagram.setEnabled(true);
                    toggleMail.setEnabled(true);
                    toggleTel.setEnabled(true);
                    toggleTwitter.setEnabled(true);



                    buttonUpdate.setText("Kaydet");
                    buttonUpdate.setBackgroundColor(Color.parseColor("#303F9F"));
                    profileImage.setClickable(true);
                    editTextEducation.setClickable(true);
                    isEditableFlag=Boolean.FALSE;

                }
                else {
                    editTextTel.setEnabled(false);
                    editTextFacebook.setEnabled(false);
                    editTextInstagram.setEnabled(false);
                    editTextTwitter.setEnabled(false);
                    editTextPassword.setEnabled(false);
                    editTextEducation.setEnabled(false);
                    toggleFacebook.setEnabled(false);
                    toggleInstagram.setEnabled(false);
                    toggleMail.setEnabled(false);
                    toggleTel.setEnabled(false);
                    toggleTwitter.setEnabled(false);

                    isEditableFlag=Boolean.TRUE;
                    buttonUpdate.setText("Güncelle");
                    profileImage.setClickable(false);

                    buttonUpdate.setBackgroundColor(Color.parseColor("#00FF04"));

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("tel", String.valueOf(editTextTel.getText()));
                    editor.putString("facebook", String.valueOf(editTextFacebook.getText()));
                    editor.putString("instagram", String.valueOf(editTextInstagram.getText()));
                    editor.putString("twitter", String.valueOf(editTextTwitter.getText()));
                    editor.putString("password", String.valueOf(editTextPassword.getText()));
                    editor.putString("education", String.valueOf(editTextEducation.getText()));

                    editor.putInt("facebookActive",toggleFacebook.isChecked()? 1 : 0);
                    editor.putInt("instagramActive",toggleInstagram.isChecked()? 1 : 0);
                    editor.putInt("mailActive",toggleMail.isChecked()? 1 : 0);
                    editor.putInt("twitterActive",toggleTwitter.isChecked()? 1 : 0);
                    editor.putInt("telActive",toggleTel.isChecked()? 1 : 0);


                    editor.apply();


                    Map<String, Object> data = new HashMap<>();

                    data.put("tel",sharedPreferences.getString("tel",""));
                    data.put("facebook",sharedPreferences.getString("facebook",""));
                    data.put("instagram",sharedPreferences.getString("instagram",""));
                    data.put("twitter",sharedPreferences.getString("twitter",""));
                    data.put("password",sharedPreferences.getString("password",""));
                    data.put("tel",sharedPreferences.getString("tel",""));
                    data.put("profileImageUrl",sharedPreferences.getString("profileImageUrl",""));
                    data.put("education",sharedPreferences.getString("education",""));

                    data.put("facebookActive",sharedPreferences.getInt("facebookActive",1));
                    data.put("instagramActive",sharedPreferences.getInt("instagramActive",1));
                    data.put("mailActive",sharedPreferences.getInt("mailActive",1));
                    data.put("twitterActive",sharedPreferences.getInt("twitterActive",1));
                    data.put("telActive",sharedPreferences.getInt("telActive",1));



                    //update firebase using
                    String userId=sharedPreferences.getString("id","");
                    db.collection("users")
                            .document(userId)
                            .set(data, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("TAG", "Document updated successfully.");
                                    } else {
                                        // Check if there's an exception before logging it
                                        if (task.getException() != null) {
                                            Log.w("TAG", "Error updating document: ", task.getException());
                                        }
                                    }
                                }
                            });







                    Toast.makeText(Account.this,"Güncelleme Tamamlandı",Toast.LENGTH_SHORT).show();
                }

            }
        });





        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void setToggleButon(ToggleButton button ,  Integer position){
        button.setTextOff("Private");
        button.setTextOn("Public");
        if (position ==1){//public
            button.setBackgroundColor(Color.rgb(0,255,0));
            button.setChecked(true);

        }
        else {//private
            button.setBackgroundColor(Color.rgb(255,0,0));
            button.setChecked(false);
        }



    }




}