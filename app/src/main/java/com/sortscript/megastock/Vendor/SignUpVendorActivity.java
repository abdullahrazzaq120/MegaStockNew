package com.sortscript.megastock.Vendor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sortscript.megastock.R;
import com.sortscript.megastock.SignInWithEmailActivity;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

public class SignUpVendorActivity extends AppCompatActivity {

    TextInputEditText registeremailVendor, registerpassVendor, registerconfrimpassVendor, loginpassVendor, loginemailVendor;
    ProgressDialog loader;
    DatabaseReference refVendors;
    FirebaseAuth mAuth;
    Button btn2, gotosigninpagebtn;
    TextInputLayout ed2, ed3, ed4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_vendor);

        getSupportActionBar().hide();

        loader = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        refVendors = FirebaseDatabase.getInstance().getReference().child("Members").child("Vendors");

        gotosigninpagebtn = findViewById(R.id.gotosigninpagebtnVendorId);
        ed2 = findViewById(R.id.emailVendor);
        ed3 = findViewById(R.id.passwordVendor);
        ed4 = findViewById(R.id.confirmpasswordVendor);
        btn2 = findViewById(R.id.RegisterVendorBtnId);
        registeremailVendor = findViewById(R.id.registerEmailVendor);
        registerpassVendor = findViewById(R.id.registerPasswordVendor);
        registerconfrimpassVendor = findViewById(R.id.registerCPasswordVendor);
        loginemailVendor = findViewById(R.id.loginVendorEmail);
        loginpassVendor = findViewById(R.id.loginVendorPassword);

        gotosigninpagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignInWithEmailActivity.class));
                finish();
            }
        });

        btn2.setOnClickListener(v -> {

            loader.setTitle("Registering");
            loader.setMessage("Please wait while we register your data");
            loader.setCanceledOnTouchOutside(false);
            loader.show();

            final String uEmail = registeremailVendor.getText().toString();
            String uPass = registerpassVendor.getText().toString();
            final String uCPass = registerconfrimpassVendor.getText().toString();

            if (uEmail.isEmpty()) {
                ed2.setError("Enter your Email");
                loader.dismiss();
            } else if (uPass.isEmpty()) {
                ed3.setError("Enter your Password");
                loader.dismiss();
            } else if (uPass.length() < 6) {
                ed3.setError("Password length must greater than 5");
                loader.dismiss();
            } else if (uCPass.isEmpty()) {
                ed4.setError("Re-Enter your Password");
                loader.dismiss();
            } else if (!uPass.equals(uCPass)) {
                Toast.makeText(getApplicationContext(), "Passwords don't match!!", Toast.LENGTH_LONG).show();
                loader.dismiss();
            } else {

                try {
                    mAuth.createUserWithEmailAndPassword(uEmail, uPass)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {

                                    new Handler().postDelayed(() -> {

                                        Map<String, Object> map = new HashMap<>();
                                        map.put("Vendor_Email", uEmail);
                                        map.put("Vendor_UID", uEmail);

                                        refVendors.child(mAuth.getCurrentUser().getUid()).setValue(map)
                                                .addOnCompleteListener(task1 -> {
                                                    if (task1.isSuccessful()) {
                                                        loader.dismiss();
                                                        Toast.makeText(SignUpVendorActivity.this, "Vendor Registered Successfully", Toast.LENGTH_LONG).show();
                                                        startActivity(new Intent(getApplicationContext(), VendorMenu.class));
                                                        finish();
                                                    } else {
                                                        loader.dismiss();
                                                        Toast.makeText(getApplicationContext(), "Failed to register your data", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }, 2000);


                                } else {
                                    loader.dismiss();
                                    Toast.makeText(getApplicationContext(), "Failed to register your data", Toast.LENGTH_SHORT).show();
                                }
                            });
                } catch (Exception ignored) {

                }
            }
        });
    }

}