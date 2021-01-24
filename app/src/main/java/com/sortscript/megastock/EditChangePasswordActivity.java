package com.sortscript.megastock;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EditChangePasswordActivity extends AppCompatActivity {

    EditText changePasswordEt;
    Button changepasswordBtn;
    FirebaseAuth mAuth;
    FirebaseUser user;
    ProgressDialog loader2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_change_password);

        loader2 = new ProgressDialog(this, R.style.CustomDialogTheme);
        getSupportActionBar().setTitle("Change Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        changePasswordEt = findViewById(R.id.changePasswordId);
        changepasswordBtn = findViewById(R.id.changePasswordBtnId);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        changepasswordBtn.setOnClickListener(v -> {
            final String pass = changePasswordEt.getText().toString().trim();
            if (pass.isEmpty()) {
                changePasswordEt.setError("Enter new password please");
            } else {

                AlertDialog.Builder builder = new AlertDialog.Builder(EditChangePasswordActivity.this);
                builder.setMessage("Are you sure you want to change password?");
                builder.setPositiveButton("Change", (dialog, which) -> {

                    loader2.setTitle("Changing");
                    loader2.setCanceledOnTouchOutside(false);
                    loader2.show();

                    user.updatePassword(pass).addOnSuccessListener(aVoid -> {
                        loader2.dismiss();
                        finish();
                        Toast.makeText(getApplicationContext(), "Password Reset Successfully", Toast.LENGTH_LONG).show();
                    }).addOnFailureListener(e -> {
                        loader2.dismiss();
                        finish();
                        Toast.makeText(getApplicationContext(), "Password Reset Failed", Toast.LENGTH_LONG).show();
                    });

                }).setNegativeButton("Close", (dialog, which) -> dialog.cancel());
                AlertDialog d = builder.create();
                d.show();
            }
        });

    }
}