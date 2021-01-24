package com.sortscript.megastock.Vendor;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sortscript.megastock.ForgetPasswordActivity;
import com.sortscript.megastock.ProfileUserActivity;
import com.sortscript.megastock.R;
import com.sortscript.megastock.SignInWithEmailActivity;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class SignInVendorActivity extends AppCompatActivity {

    ImageView googleIconSignInVendor;
    GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 101;
    ProgressDialog loader, loader2;
    DatabaseReference refVendors, refCustomers;
    Button btn1, gotosignuppageVendorbtn, forgetpasswordVendor;
    FirebaseAuth mAuth;
    TextInputLayout ed5, ed6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_vendor);


        getSupportActionBar().hide();

        loader = new ProgressDialog(this);
        loader2 = new ProgressDialog(this, R.style.CustomDialogTheme);
        mAuth = FirebaseAuth.getInstance();
        refVendors = FirebaseDatabase.getInstance().getReference().child("Members").child("Vendors");
        refCustomers = FirebaseDatabase.getInstance().getReference().child("Members").child("Customers");

        forgetpasswordVendor = findViewById(R.id.forgetPasswordVendorId);
        googleIconSignInVendor = findViewById(R.id.googleIconSignInVendorId);
        gotosignuppageVendorbtn = findViewById(R.id.gotosignuppageVendorbtnId);
        btn1 = findViewById(R.id.LoginVendorBtnId);
        ed5 = findViewById(R.id.emailloginVendor);
        ed6 = findViewById(R.id.emailpasswordVendor);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        forgetpasswordVendor.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), ForgetPasswordActivity.class)));

        gotosignuppageVendorbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignUpVendorActivity.class));
                finish();
            }
        });
        googleIconSignInVendor.setOnClickListener(new View.OnClickListener() {
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

                    refVendors.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                if (dataSnapshot.exists()) {
                                    try {
                                        String idVendor = dataSnapshot.child("Vendor_Email").getValue().toString();
                                        if (idVendor.contains(userLEmail)) {
                                            mAuth.signInWithEmailAndPassword(userLEmail, userLPassword)
                                                    .addOnCompleteListener(task -> {
                                                        if (task.isSuccessful()) {
                                                            loader.dismiss();
                                                            Toast.makeText(SignInVendorActivity.this, "Vendor LogIn Successfully", Toast.LENGTH_LONG).show();
                                                            startActivity(new Intent(getApplicationContext(), VendorMenu.class));
                                                            finish();
                                                        } else {
                                                            loader.dismiss();
                                                            Toast.makeText(getApplicationContext(), "Vendor Failed to Log In", Toast.LENGTH_LONG).show();
                                                        }
                                                    });
                                        } else {
                                            loader.dismiss();
                                            Toast.makeText(SignInVendorActivity.this, "Try again", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (Exception e) {
                                        loader.dismiss();
                                        Toast.makeText(SignInVendorActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    loader.dismiss();
                                    Toast.makeText(SignInVendorActivity.this, "Data not exists", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(SignInVendorActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        Log.e("googleee", task.getException().toString());
                        updateUI(null);
                    }
                });
    }

    private void updateUI(FirebaseUser user) {


        refCustomers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    try {
                        String idCustomer = dataSnapshot.child("Customer_Email").getValue().toString();
                        if (idCustomer.contains(user.getEmail())) {
                            loader.dismiss();
                            Toast.makeText(SignInVendorActivity.this, "Vendor Failed to Authenticate", Toast.LENGTH_LONG).show();
                        } else {

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    Map<String, Object> map = new HashMap<>();
                                    map.put("Vendor_Email", user.getEmail());
                                    map.put("Vendor_UID", mAuth.getCurrentUser().getUid());

                                    refVendors.child(mAuth.getCurrentUser().getUid()).setValue(map)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        loader.dismiss();
                                                        startActivity(new Intent(getApplicationContext(), VendorMenu.class));
                                                        Toast.makeText(SignInVendorActivity.this, "Vendor Authenticate Successfully", Toast.LENGTH_LONG).show();
                                                        finish();
                                                    } else {
                                                        loader.dismiss();
                                                        Toast.makeText(SignInVendorActivity.this, task.toString(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            }, 2000);
                        }
                    } catch (Exception e) {
                        loader.dismiss();
                        Toast.makeText(SignInVendorActivity.this, "Login Error", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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

            LinearLayout llVendor = findViewById(R.id.llVendorSignInId);
            llVendor.setVisibility(View.GONE);
            loader2.setCancelable(false);
            loader2.setCanceledOnTouchOutside(false);
            loader2.show();

            refVendors.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        try {
                            if (dataSnapshot.exists()) {

                                String idVendor = dataSnapshot.getKey();
                                if (idVendor.contains(mAuth.getCurrentUser().getUid())) {

                                    loader2.dismiss();
                                    Intent i = new Intent(SignInVendorActivity.this, VendorMenu.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(i);
                                } else {
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            loader2.dismiss();
                                            llVendor.setVisibility(View.VISIBLE);
                                        }
                                    }, 2000);

                                }
                            } else {
                                loader2.dismiss();
                                llVendor.setVisibility(View.VISIBLE);
                                Toast.makeText(getApplicationContext(), "Vendor Not exits", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            loader2.dismiss();
                            llVendor.setVisibility(View.VISIBLE);
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    loader2.dismiss();
                    llVendor.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}