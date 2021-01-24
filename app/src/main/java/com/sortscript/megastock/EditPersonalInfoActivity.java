package com.sortscript.megastock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class EditPersonalInfoActivity extends AppCompatActivity {

    TextInputEditText firstNameEditPersonalEt, lastNameEditPersonalEt, userNameEditPersonalEt, ageEditPersonalEt, contactNumberEditPersonalEt;
    Button updateEditPersonalBtn;
    RadioButton rbOne, rbTwo;
    String genderLink;
    ProgressDialog loader;
    DatabaseReference reference;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_personal_info);


        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("Members").child("Customers").child(mAuth.getCurrentUser().getUid());
        loader = new ProgressDialog(this);
        firstNameEditPersonalEt = findViewById(R.id.firstNameEditPersonalId);
        lastNameEditPersonalEt = findViewById(R.id.lastNameEditPersonalId);
        userNameEditPersonalEt = findViewById(R.id.userNameEditPersonalId);
        ageEditPersonalEt = findViewById(R.id.ageEditPersonalId);
        contactNumberEditPersonalEt = findViewById(R.id.contactNumberEditPersonalId);
        updateEditPersonalBtn = findViewById(R.id.updateEditPersonalBtnId);
        rbOne = findViewById(R.id.rb1EditPersonalId);
        rbTwo = findViewById(R.id.rb2EditPersonalId);

        reference.child("UserDetails").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String pUName = snapshot.child("CustomerUserName").getValue().toString();
                String pfName = snapshot.child("CustomerFirstName").getValue().toString();
                String plName = snapshot.child("CustomerLastName").getValue().toString();
                String pAge = snapshot.child("CustomerAge").getValue().toString();
                String pNumber = snapshot.child("CustomerContactNumber").getValue().toString();
                String pGender = snapshot.child("CustomerGender").getValue().toString();

                userNameEditPersonalEt.setText(pUName);
                firstNameEditPersonalEt.setText(pfName);
                lastNameEditPersonalEt.setText(plName);
                ageEditPersonalEt.setText(pAge);
                contactNumberEditPersonalEt.setText(pNumber);

                if (pGender.equals("Male")) {
                    rbOne.setChecked(true);
                } else {
                    rbTwo.setChecked(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        updateEditPersonalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loader.setTitle("Updating Personal Info");
                loader.setCanceledOnTouchOutside(false);
                loader.show();

                String fName = firstNameEditPersonalEt.getText().toString();
                String lName = lastNameEditPersonalEt.getText().toString();
                String uName = userNameEditPersonalEt.getText().toString();
                String age = ageEditPersonalEt.getText().toString();
                String contactN = contactNumberEditPersonalEt.getText().toString();

                if (rbOne.isChecked()) {
                    genderLink = rbOne.getText().toString();
                }
                if (rbTwo.isChecked()) {
                    genderLink = rbTwo.getText().toString();
                }

                if (fName.isEmpty()) {
                    loader.dismiss();
                    firstNameEditPersonalEt.setError("First Name can't be Empty");
                } else if (lName.isEmpty()) {
                    loader.dismiss();
                    lastNameEditPersonalEt.setError("Last Name can't be Empty");
                } else if (uName.isEmpty()) {
                    loader.dismiss();
                    userNameEditPersonalEt.setError("User Name can't be Empty");
                } else if (contactN.isEmpty()) {
                    loader.dismiss();
                    contactNumberEditPersonalEt.setError("Contact Number can't be Empty");
                } else {
                    Map<String, Object> map = new HashMap<>();
                    map.put("CustomerUserName", uName);
                    map.put("CustomerFirstName", fName);
                    map.put("CustomerLastName", lName);
                    map.put("CustomerAge", age);
                    map.put("CustomerContactNumber", contactN);
                    map.put("CustomerGender", genderLink);

                    reference.child("UserDetails").updateChildren(map)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        loader.dismiss();
                                        Toast.makeText(EditPersonalInfoActivity.this, "Saved!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        loader.dismiss();
                                        Toast.makeText(EditPersonalInfoActivity.this, task.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }
}