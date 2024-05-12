package com.example.semesterproject;

import static android.content.ContentValues.TAG;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import android.view.ViewGroup;
import android.widget.Toast;


public class CourseDetail extends AppCompatActivity {
    boolean focusable = true;
    LinearLayout LayoutAllCoursesGroups, LinearLayoutCourseDetail;
    Button ButtonUpdateCourse,ButtonAddGroup;
    TextView TextViewCourseGroupPageCourseName, TextViewCourseGroupPageCourseId,TextViewCourseGroupPageCourseSemester,TextViewtCourseGroupPageCourseStatus,TextViewCourseInfoPageDay;
    private SharedPreferences sharedPreferences;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private void setEditTextsFocusable(ViewGroup viewGroup, boolean focusable) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (child instanceof EditText) {
                EditText editText = (EditText) child;
                editText.setFocusable(focusable);
                editText.setFocusableInTouchMode(focusable);
            } else if (child instanceof ViewGroup) {
                setEditTextsFocusable((ViewGroup) child, focusable);
            }
        }
    }

    private void disableButtonsInLinearLayout(ViewGroup viewGroup,boolean status) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (child instanceof Button) {
                Button button = (Button) child;
                button.setEnabled(status);
                if (status){
                    button.setVisibility(View.VISIBLE);
                }
                else {
                    button.setVisibility(View.INVISIBLE);
                }

            } else if (child instanceof ViewGroup) {
                disableButtonsInLinearLayout((ViewGroup) child,status);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        ButtonUpdateCourse = findViewById(R.id.ButtonUpdateCourse);
        LayoutAllCoursesGroups = findViewById(R.id.LayoutAllCoursesGroups);
        TextViewCourseGroupPageCourseName = findViewById(R.id.TextViewCourseGroupPageCourseName);
        TextViewCourseGroupPageCourseId = findViewById(R.id.TextViewCourseGroupPageCourseId);
        LinearLayoutCourseDetail = findViewById(R.id.LinearLayoutCourseDetail);
        TextViewCourseGroupPageCourseName.setText(sharedPreferences.getString("courseName", "Not Found"));
        TextViewCourseGroupPageCourseId.setText(sharedPreferences.getString("courseId", "Not "));
        ButtonUpdateCourse.setBackgroundColor(0xFF0000FF);
        ButtonAddGroup=findViewById(R.id.ButtonAddGroup);
        TextViewCourseGroupPageCourseSemester=findViewById(R.id.TextViewCourseGroupPageCourseSemester);
        TextViewtCourseGroupPageCourseStatus=findViewById(R.id.TextViewCourseGroupPageCourseStatus);
        TextViewCourseInfoPageDay=findViewById(R.id.TextViewCourseInfoPageDay);


        ButtonAddGroup.setEnabled(false);
        ButtonAddGroup.setVisibility(View.INVISIBLE);
        TextViewCourseGroupPageCourseSemester.setText(sharedPreferences.getString("semester",""));
        TextViewtCourseGroupPageCourseStatus.setText(sharedPreferences.getString("status",""));
        TextViewCourseInfoPageDay.setText(sharedPreferences.getString("courseDate",""));




        ButtonAddGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View cardView = LayoutInflater.from(CourseDetail.this).inflate(R.layout.groups_info_card, LayoutAllCoursesGroups, false);

                EditText textViewCourseGroupInstructor = cardView.findViewById(R.id.EditTextCourseGroupInstructor);
                textViewCourseGroupInstructor.setText("Egitmen ismi");
                textViewCourseGroupInstructor.setFocusable(true);
                textViewCourseGroupInstructor.setFocusableInTouchMode(true);

                EditText TextViewGroupNo = cardView.findViewById(R.id.EditTextGroupNo);
                TextViewGroupNo.setText(String.valueOf("Grup numarasi"));
                TextViewGroupNo.setFocusable(true);
                TextViewGroupNo.setFocusableInTouchMode(true);

                EditText textViewCourseGroupDateTime = cardView.findViewById(R.id.EditTextCourseGroupDateTimeStart);
                textViewCourseGroupDateTime.setText(" Baslama zamani");
                textViewCourseGroupDateTime.setFocusable(true);
                textViewCourseGroupDateTime.setFocusableInTouchMode(true);


                EditText textViewCourseGroupDateTimeFinish = cardView.findViewById(R.id.EditTextCourseGroupDateTimeFinish);
                textViewCourseGroupDateTimeFinish.setText(" Bitis zamani ");

                textViewCourseGroupDateTimeFinish.setFocusable(true);
                textViewCourseGroupDateTimeFinish.setFocusableInTouchMode(true);
                Button buttonGroupDelete=cardView.findViewById(R.id.buttonGroupDelete);
                buttonGroupDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // buttonGroupDelete butonunun bulunduğu cardView'yi elde et
                        LayoutAllCoursesGroups.removeView(cardView);
                    }

                });
                LayoutAllCoursesGroups.addView(cardView,0);
                Button buttonGroupOgrenciEkle=cardView.findViewById(R.id.buttonGroupOgrenciEkle);


                buttonGroupOgrenciEkle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {



                        // SharedPreferences'e kaydetme
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("CouseAddStdGroupNo", String.valueOf(TextViewGroupNo.getText()));
                        editor.apply();

                        // Sonraki aktiviteye geçme

                    }
                });



            }
        });



        ButtonUpdateCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (focusable) {
                    setEditTextsFocusable(LinearLayoutCourseDetail, focusable);
                    focusable = !focusable; // Tıklama sonrasında tersine çevirme
                    ButtonUpdateCourse.setText("Kaydet");
                    ButtonUpdateCourse.setBackgroundColor(0xFF00FF00);
                    ButtonAddGroup.setEnabled(true);
                    ButtonAddGroup.setVisibility(View.VISIBLE);
                    disableButtonsInLinearLayout(LayoutAllCoursesGroups,true);






                } else {
                    setEditTextsFocusable(LinearLayoutCourseDetail, focusable);
                    focusable = !focusable; // Tıklama sonrasında tersine çevirme
                    ButtonUpdateCourse.setText("Düzenle");
                    ButtonUpdateCourse.setBackgroundColor(0xFF0000FF);
                    ButtonAddGroup.setEnabled(false);
                    ButtonAddGroup.setVisibility(View.INVISIBLE);
                    disableButtonsInLinearLayout(LayoutAllCoursesGroups,false);

                    for (int i = 0; i < LayoutAllCoursesGroups.getChildCount(); i++) {
                        View cardView = LayoutAllCoursesGroups.getChildAt(i);

                        EditText editTextGroupNo = cardView.findViewById(R.id.EditTextGroupNo);
                        EditText editTextCourseGroupInstructor = cardView.findViewById(R.id.EditTextCourseGroupInstructor);
                        EditText editTextCourseGroupDateTimeStart = cardView.findViewById(R.id.EditTextCourseGroupDateTimeStart);
                        EditText editTextCourseGroupDateTimeFinish = cardView.findViewById(R.id.EditTextCourseGroupDateTimeFinish);
                        TextView textViewCourseGroupStdCount = cardView.findViewById(R.id.textViewCourseGroupStdCount);

                        // Değerleri al

                        String groupNo = editTextGroupNo.getText().toString();
                        String instructor = editTextCourseGroupInstructor.getText().toString();
                        String dateTimeStart = editTextCourseGroupDateTimeStart.getText().toString();
                        String dateTimeFinish = editTextCourseGroupDateTimeFinish.getText().toString();
                        String stdCount = textViewCourseGroupStdCount.getText().toString();

                        String tag = cardView.getTag().toString();
                        Log.d(TAG, tag+groupNo+instructor+dateTimeStart+dateTimeFinish+stdCount);
                        if (!tag.equals("")){
                            db.collection("groups").document(tag)
                                    .update(
                                            "number", groupNo,
                                            "insName", instructor,
                                            "startTime", dateTimeStart,
                                            "finishTime", dateTimeFinish,
                                            "stdCount", stdCount
                                    )
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Güncelleme başarılı olduğunda yapılacak işlemler
                                            Log.d(TAG, "Güncelleme başarılı");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Güncelleme başarısız olduğunda yapılacak işlemler
                                            Log.w(TAG, "Güncelleme başarısız", e);
                                        }
                                    });


                            // Güncelleme işlemini gerçekleştir
                            // Burada groups tablosunu güncellemek için gerekli olan işlemleri yapmalısınız
                        }
                            else {
                            Map<String, Object> newData = new HashMap<>();
                            newData.put("courseId", sharedPreferences.getString("courseId", ""));
                            newData.put("number", groupNo);
                            newData.put("insName", instructor);
                            newData.put("startTime", dateTimeStart);
                            newData.put("finishTime", dateTimeFinish);
                            newData.put("stdCount", stdCount);

                            db.collection("groups")
                                    .add(newData)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Log.d(TAG, "Yeni veri eklendi: " + documentReference.getId());
                                            cardView.setTag(documentReference.getId());

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Yeni veri eklenemedi", e);
                                        }
                                    });

                            }

                        }








                }


            }
        });


        db.collection("groups")
                .whereEqualTo("courseId", sharedPreferences.getString("courseId", ""))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                    View cardView = LayoutInflater.from(CourseDetail.this).inflate(R.layout.groups_info_card, LayoutAllCoursesGroups, false);
                                    cardView.setTag(document.getId());
                                    EditText textViewCourseGroupInstructor = cardView.findViewById(R.id.EditTextCourseGroupInstructor);
                                    textViewCourseGroupInstructor.setText(document.getString("insName"));

                                    EditText TextViewGroupNo = cardView.findViewById(R.id.EditTextGroupNo);
                                    TextViewGroupNo.setText(String.valueOf(document.getString("number")));

                                    EditText textViewCourseGroupDateTime = cardView.findViewById(R.id.EditTextCourseGroupDateTimeStart);
                                    textViewCourseGroupDateTime.setText(" " + document.getString("startTime"));

                                    EditText textViewCourseGroupDateTimeFinish = cardView.findViewById(R.id.EditTextCourseGroupDateTimeFinish);
                                    textViewCourseGroupDateTimeFinish.setText(document.getString("finishTime"));


                                    TextView textViewCourseGroupStdCount = cardView.findViewById(R.id.textViewCourseGroupStdCount);
// users-groups tablosundan ilgili courseId ve groupId'le eşleşen belgeleri sorgula
                                    db.collection("users-groups")
                                            .whereEqualTo("courseId", sharedPreferences.getString("courseId", ""))
                                            .whereEqualTo("groupNo", document.getString("number"))
                                            .get()
                                            .addOnCompleteListener(task2 -> {
                                                if (task2.isSuccessful()) {
                                                    // Sorgu sonucunda dönen belge sayısını al
                                                    int count = task2.getResult().size();
                                                    // TextView'e belge sayısını yazdır
                                                    textViewCourseGroupStdCount.setText(String.valueOf(count));
                                                } else {
                                                    // Sorguda bir hata oluştuysa hata mesajı yazdır
                                                    Log.e("TAG", "Error getting documents: ", task.getException());
                                                }
                                            });

                                    Button buttonGroupDelete=cardView.findViewById(R.id.buttonGroupDelete);
                                    Button buttonGroupOgrenciEkle=cardView.findViewById(R.id.buttonGroupOgrenciEkle);


                                    buttonGroupOgrenciEkle.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {


                                            // SharedPreferences'e kaydetme
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("CouseAddStdGroupNo", document.getString("number"));
                                            editor.apply();

                                            // Toast mesajı gösterme
                                            Toast.makeText(getApplicationContext(), document.getString("number"), Toast.LENGTH_SHORT).show();

                                            // Sonraki aktiviteye geçme
                                            Intent intent = new Intent(getApplicationContext(), AddStd.class);
                                            startActivity(intent);
                                        }
                                    });






                                    buttonGroupDelete.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            // buttonGroupDelete butonunun bulunduğu cardView'yi elde et
                                            db.collection("users-groups")
                                                    .whereEqualTo("courseId", sharedPreferences.getString("courseId", ""))
                                                    .whereEqualTo("groupNo", TextViewGroupNo.getText().toString())
                                                    .get()
                                                    .addOnCompleteListener(task -> {
                                                        if (task.isSuccessful()) {
                                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                                document.getReference().delete()
                                                                        .addOnSuccessListener(aVoid -> Log.d("TAG", "DocumentSnapshot successfully deleted"))
                                                                        .addOnFailureListener(e -> Log.w("TAG", "Error deleting document", e));
                                                            }
                                                        } else {
                                                            Log.d("TAG", "Error getting documents: ", task.getException());
                                                        }
                                                    });












                                            db.collection("groups").document(cardView.getTag().toString())
                                                    .delete()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            // Belge başarıyla silindiğinde yapılacak işlemler
                                                            Log.d(TAG, "Belge başarıyla silindi!");
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            // Belge silinemediğinde yapılacak işlemler
                                                            Log.w(TAG, "Belge silinemedi!", e);
                                                        }
                                                    });
                                            LayoutAllCoursesGroups.removeView(cardView);
                                            }

                                    });


                                    if (sharedPreferences.getString("role", "").equals("std")) {
                                        String stdEmail = sharedPreferences.getString("email", "");
                                        Log.d("std groupno", stdEmail + sharedPreferences.getString("courseId", ""));

                                        db.collection("users-groups")
                                                .whereEqualTo("userMail", stdEmail)
                                                .whereEqualTo("courseId", sharedPreferences.getString("courseId", ""))
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task2) {
                                                        if (task2.isSuccessful()) {
                                                            QuerySnapshot querySnapshot2 = task2.getResult();
                                                            Log.d("std groupno", "log 2");

                                                            if (querySnapshot2 != null && !querySnapshot2.isEmpty()) {
                                                                Log.d("std groupno", "log 3");

                                                                for (DocumentSnapshot document2 : querySnapshot2.getDocuments()) {
                                                                    String stdGroupNo = document2.getString("groupNo");
                                                                    Log.d("std groupno", String.valueOf(stdGroupNo));
                                                                    if (document.getString("number") != null && document.getString("number").equals(stdGroupNo)) {
                                                                        Log.d("std groupno", "esit");

                                                                        TextView textViewCourseGroupStdStatus = cardView.findViewById(R.id.textViewCourseGroupStdStatus);
                                                                        textViewCourseGroupStdStatus.setText("Katıldı");
                                                                        LayoutAllCoursesGroups.addView(cardView);
                                                                        disableButtonsInLinearLayout(LinearLayoutCourseDetail,false);

                                                                    } else {
                                                                        Log.d("std groupno", "esit degil ");
                                                                        TextView textViewCourseGroupStdStatus = cardView.findViewById(R.id.textViewCourseGroupStdStatus);
                                                                        textViewCourseGroupStdStatus.setText("Katılmadı");
                                                                        LayoutAllCoursesGroups.addView(cardView);
                                                                        disableButtonsInLinearLayout(LinearLayoutCourseDetail,false);
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                });
                                    } else {
                                        TextView textViewCourseGroupStdStatus = cardView.findViewById(R.id.textViewCourseGroupStdStatus);
                                        textViewCourseGroupStdStatus.setText("");

                                        LayoutAllCoursesGroups.addView(cardView);
                                        disableButtonsInLinearLayout(LayoutAllCoursesGroups,false);

                                    }


                                }
                            }
                        }
                    }
                });



    }
}
