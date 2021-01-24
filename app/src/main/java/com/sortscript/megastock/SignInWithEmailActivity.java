package com.sortscript.megastock;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sortscript.megastock.Vendor.SignInVendorActivity;
import com.sortscript.megastock.Vendor.VendorProfileActivity;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class SignInWithEmailActivity extends AppCompatActivity {

    ImageView googleIconSignIn;
    GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 101;
    ProgressDialog loader, loader2;
    DatabaseReference refCustomers, refVendors;
    Button btn1, gotosignuppagebtn, forgetpassword;
    FirebaseAuth mAuth;
    TextInputLayout ed5, ed6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_with_email);

        getSupportActionBar().hide();

        loader = new ProgressDialog(this);
        loader2 = new ProgressDialog(this, R.style.CustomDialogTheme);
        mAuth = FirebaseAuth.getInstance();
        refCustomers = FirebaseDatabase.getInstance().getReference().child("Members").child("Customers");
        refVendors = FirebaseDatabase.getInstance().getReference().child("Members").child("Vendors");

        forgetpassword = findViewById(R.id.forgetPasswordPatientId);
        googleIconSignIn = findViewById(R.id.googleIconSignInId);
        gotosignuppagebtn = findViewById(R.id.gotosignuppagebtnId);
        btn1 = findViewById(R.id.LoginPatientBtnId);
        ed5 = findViewById(R.id.emailloginPatient);
        ed6 = findViewById(R.id.emailpasswordPatient);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        forgetpassword.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), ForgetPasswordActivity.class)));

        gotosignuppagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignUpWithEmailActivity.class));
                finish();
            }
        });
        googleIconSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        btn1.setOnClickListener(v -> {

            loader.setTitle("Signing In");
            loader.setMessage("Please wait while we check your credentials");
            loader.setCanceledOnTouchOutside(false);
            loader.show();

            try {

                String userLEmail = ed5.getEditText().getText().toString();
                String userLPassword = ed6.getEditText().getText().toString();

                if (userLEmail.isEmpty()) {
                    ed5.setError("Enter your Email");
                    loader.dismiss();
                } else if (userLPassword.isEmpty()) {
                    ed6.setError("Enter your Password");
                    loader.dismiss();
                } else {

                    refCustomers.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                if (dataSnapshot.exists()) {
                                    try {
                                        String idUser = dataSnapshot.child("User_Email").getValue().toString();
                                        if (idUser.contains(userLEmail)) {

                                            mAuth.signInWithEmailAndPassword(userLEmail, userLPassword)
                                                    .addOnCompleteListener(task -> {
                                                        if (task.isSuccessful()) {
                                                            loader.dismiss();
                                                            Toast.makeText(SignInWithEmailActivity.this, "User LogIn Successfully", Toast.LENGTH_LONG).show();
                                                            startActivity(new Intent(getApplicationContext(), ProfileUserActivity.class));
                                                            finish();
                                                        } else {
                                                            loader.dismiss();
                                                            Toast.makeText(getApplicationContext(), "User Failed to Log In", Toast.LENGTH_LONG).show();
                                                        }
                                                    });
                                        } else {
                                            loader.dismiss();
                                            Toast.makeText(SignInWithEmailActivity.this, "Try again", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (Exception e) {
                                        loader.dismiss();
                                        Toast.makeText(SignInWithEmailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    loader.dismiss();
                                    Toast.makeText(SignInWithEmailActivity.this, "User not exists", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }
            } catch (Exception e) {
                loader.dismiss();
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void firebaseAuthWithGoogle(String idToken) {

        loader.setTitle("Logging In with Google...");
        loader.setCanceledOnTouchOutside(false);
        loader.show();

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        final FirebaseUser user = mAuth.getCurrentUser();
                        Log.e("unameemail", user.getEmail() + " " + user.getDisplayName());

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                loader.dismiss();
                                updateUI(user);
                            }
                        }, 2000);
                    } else {
                        // If sign in fails, display a message to the user.

                        loader.dismiss();
                        Toast.makeText(SignInWithEmailActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        Log.e("googleee", task.getException().toString());
                        updateUI(null);
                    }
                });
    }

    private void updateUI(FirebaseUser user) {

        refVendors.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    try {

                        String idCustomer = dataSnapshot.child("Vendor_Email").getValue().toString();
                        if (idCustomer.contains(user.getEmail())) {
                            loader.dismiss();
                            Toast.makeText(SignInWithEmailActivity.this, "User Failed to Authenticate", Toast.LENGTH_LONG).show();
                        } else {

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    Map<String, Object> map = new HashMap<>();
                                    map.put("User_Email", user.getEmail());
                                    map.put("User_UID", mAuth.getCurrentUser().getUid());

                                    refCustomers.child(mAuth.getCurrentUser().getUid()).setValue(map)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        loader.dismiss();
                                                        startActivity(new Intent(getApplicationContext(), ProfileUserActivity.class));
                                                        Toast.makeText(SignInWithEmailActivity.this, "User Authenticate Successfully", Toast.LENGTH_LONG).show();
                                                        finish();
                                                    } else {
                                                        loader.dismiss();
                                                        Toast.makeText(SignInWithEmailActivity.this, task.toString(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            }, 2000);
                        }

                    } catch (Exception e) {
                        loader.dismiss();
                        Toast.makeText(SignInWithEmailActivity.this, "User Failed to Authenticate", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loader.dismiss();
                Toast.makeText(SignInWithEmailActivity.this, "User Failed to Authenticate", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.e("firebaseAuthWithGoogle:", account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException ignored) {
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null) {

            LinearLayout llPatient = findViewById(R.id.llPatientSignInId);
            llPatient.setVisibility(View.GONE);
            loader2.setCancelable(false);
            loader2.setCanceledOnTouchOutside(false);
            loader2.show();

            refCustomers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        try {
                            if (dataSnapshot.exists()) {

                                String idUser = dataSnapshot.getKey();
                                if (idUser.contains(mAuth.getCurrentUser().getUid())) {

                                    loader2.dismiss();
                                    Intent i = new Intent(SignInWithEmailActivity.this, ProfileUserActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(i);

                                } else {
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            loader2.dismiss();
                                            llPatient.setVisibility(View.VISIBLE);
                                        }
                                    }, 2000);

                                }
                            } else {
                                loader2.dismiss();
                                llPatient.setVisibility(View.VISIBLE);
                                Toast.makeText(getApplicationContext(), "User Not exits", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            loader2.dismiss();
                            llPatient.setVisibility(View.VISIBLE);
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    loader2.dismiss();
                    llPatient.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}