package com.example.semesterproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class ListingReports extends AppCompatActivity {

    LinearLayout ListingReportsLinearLayout;
    private SharedPreferences sharedPreferences;
    List<DocumentSnapshot> ReportList;

    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_listing_reports);
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        ReportList=new ArrayList<>();
        ListingReportsLinearLayout=findViewById(R.id.ListingReportsLinearLayout);



if(sharedPreferences.getString("role","").equals("ins")) {// instructor
    db.collection("reports").orderBy("date")
            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                Object scopeValue = document.get("scope");
                                Object recipientValue = document.get("recipient");

                                if ((scopeValue != null && (scopeValue.equals("Course") || scopeValue.equals("Cheating") || scopeValue.equals("Misbehavior"))) ||
                                        (recipientValue != null && (recipientValue.equals("All") || recipientValue.equals(sharedPreferences.getString("email",""))))) {
                                    ReportList.add(document);
                                }
                            }
                            for (DocumentSnapshot document :ReportList){
                                View cardView = LayoutInflater.from(ListingReports.this).inflate(R.layout.report_card, ListingReportsLinearLayout, false);

                                TextView TextViewReportTitle=cardView.findViewById(R.id.TextViewReportTitle);
                                TextViewReportTitle.setText(document.getString("subject"));

                                TextView TextViewReportSenderMail=cardView.findViewById(R.id.TextViewReportSenderMail);
                                TextViewReportSenderMail.setText(document.getString("stdMail"));

                                TextView TextViewReportInfoCourseId=cardView.findViewById(R.id.TextViewReportInfoCourseId);
                                TextViewReportInfoCourseId.setText(document.getString("CourseId"));


                                TextView textViewReportDate = cardView.findViewById(R.id.textViewReportDate);
                                Timestamp dateTimestamp = (Timestamp) document.get("date");
                                Date date = dateTimestamp.toDate();
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                String formattedDate = sdf.format(date);
                                textViewReportDate.setText(formattedDate);


                                TextView textViewReportScope=cardView.findViewById(R.id.textViewReportScope);
                                textViewReportScope.setText(document.getString("scope"));

                                TextView textViewReportingContent=cardView.findViewById(R.id.textViewReportingContent);
                                textViewReportingContent.setText(document.getString("content"));

                                ListingReportsLinearLayout.addView(cardView);

                            }
                        }
                    }
                }
            });

}
else{// admin

    db.collection("reports").orderBy("date")
            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                if (document.get("scope").equals("App") || document.get("recipient").equals("All") ){
                                    ReportList.add(document);
                                }
                            }
                            for (DocumentSnapshot document :ReportList){
                                View cardView = LayoutInflater.from(ListingReports.this).inflate(R.layout.report_card, ListingReportsLinearLayout, false);

                                TextView TextViewReportTitle=cardView.findViewById(R.id.TextViewReportTitle);
                                TextViewReportTitle.setText(document.getString("subject"));

                                TextView TextViewReportSenderMail=cardView.findViewById(R.id.TextViewReportSenderMail);
                                TextViewReportSenderMail.setText(document.getString("stdMail"));

                                TextView TextViewReportInfoCourseId=cardView.findViewById(R.id.TextViewReportInfoCourseId);
                                TextViewReportInfoCourseId.setText(document.getString("CourseId"));


                                TextView textViewReportDate = cardView.findViewById(R.id.textViewReportDate);
                                Timestamp dateTimestamp = (Timestamp) document.get("date");
                                Date date = dateTimestamp.toDate();
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                String formattedDate = sdf.format(date);
                                textViewReportDate.setText(formattedDate);


                                TextView textViewReportScope=cardView.findViewById(R.id.textViewReportScope);
                                textViewReportScope.setText(document.getString("scope"));

                                TextView textViewReportingContent=cardView.findViewById(R.id.textViewReportingContent);
                                textViewReportingContent.setText(document.getString("content"));

                                ListingReportsLinearLayout.addView(cardView);

                            }
                        }
                    }
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