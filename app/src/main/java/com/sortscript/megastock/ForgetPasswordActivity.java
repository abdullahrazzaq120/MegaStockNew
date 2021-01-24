package com.sortscript.megastock;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;

public class ForgetPasswordActivity extends AppCompatActivity {

    EditText recoverEmailEt;
    Button forget_password_btn;
    ProgressDialog loadingbar;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        getSupportActionBar().setTitle("Forget Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recoverEmailEt = findViewById(R.id.recoverEmailId);
        forget_password_btn = findViewById(R.id.forget_password_btnId);
        loadingbar = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        forget_password_btn.setOnClickListener(v -> {
            try {
                String emailRecover = recoverEmailEt.getText().toString();
                beginRecovery(emailRecover);
            } catch (Exception e) {
                Toast.makeText(ForgetPasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void beginRecovery(String emailRecover) {

        loadingbar.setTitle("Sending new password to this email");
        loadingbar.show();
        loadingbar.setCanceledOnTouchOutside(false);

        mAuth.sendPasswordResetEmail(emailRecover)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        loadingbar.dismiss();
                        finish();
                        Toast.makeText(ForgetPasswordActivity.this, "Check Email", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ForgetPasswordActivity.this, "Failed...", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> Toast.makeText(ForgetPasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}