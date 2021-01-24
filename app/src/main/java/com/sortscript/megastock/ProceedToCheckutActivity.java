package com.sortscript.megastock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sortscript.megastock.Classes.MySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProceedToCheckutActivity extends AppCompatActivity {

    String key, totalKey, quantityKey;
    FirebaseAuth mAuth;
    DatabaseReference referenceProducts, referenceMembers, referenceOrder, referenceVendor;
    ImageView checkoutProductImageIv;
    TextView checkoutTotalTv, checkoutProductNameTv, checkoutProductSalePriceTv, checkoutProductUnitTv, checkoutProductQuantityTv;
    EditText checkoutNameEt, checkoutStreetAddressEt, checkoutPhoneEt, checkoutConfirmPhoneEt, checkoutDescriptionEt;
    Spinner checkoutCitySpinner;
    Button checkoutConfirmBtn;
    String checkoutCityLink;
    String nameF, imageF, unitF, salePriceF, pidF;
    ProgressDialog loader;
    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    private String serverKey;
    final private String contentType = "application/json";
    final String TAG = "NOTIFICATION TAG";
    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE;
    String TOPIC;
    String tkn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proceed_to_checkut);

        serverKey = "key=" + getString(R.string.firebase_server_key);

        loader = new ProgressDialog(ProceedToCheckutActivity.this);
        key = getIntent().getStringExtra("proceedProductKey");
        totalKey = getIntent().getStringExtra("proceedProductTotal");
        quantityKey = getIntent().getStringExtra("proceedProductQuantity");

        mAuth = FirebaseAuth.getInstance();
        referenceProducts = FirebaseDatabase.getInstance().getReference().child("Products");
        referenceMembers = FirebaseDatabase.getInstance().getReference().child("Members").child("Customers");
        referenceOrder = FirebaseDatabase.getInstance().getReference().child("Orders");
        referenceVendor = FirebaseDatabase.getInstance().getReference().child("Vendor");

        checkoutProductImageIv = findViewById(R.id.imageProceedId);
        checkoutProductNameTv = findViewById(R.id.nameProceedId);
        checkoutProductSalePriceTv = findViewById(R.id.salePriceProceedId);
        checkoutProductUnitTv = findViewById(R.id.unitProceedId);
        checkoutProductQuantityTv = findViewById(R.id.quantityProceedId);

        checkoutTotalTv = findViewById(R.id.checkoutTotalId);
        checkoutCitySpinner = findViewById(R.id.checkoutCityId);
        checkoutNameEt = findViewById(R.id.checkoutNameId);
        checkoutStreetAddressEt = findViewById(R.id.checkoutStreetAddressId);
        checkoutPhoneEt = findViewById(R.id.checkoutPhoneId);
        checkoutConfirmPhoneEt = findViewById(R.id.checkoutConfirmPhoneId);
        checkoutDescriptionEt = findViewById(R.id.checkoutDescriptionId);
        checkoutConfirmBtn = findViewById(R.id.checkoutConfirmBtnId);

        checkoutTotalTv.setText(totalKey);
        checkoutProductQuantityTv.setText(quantityKey);

        referenceVendor.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    tkn = snapshot.child("deviceToken").getValue().toString();
                } catch (Exception ignored) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        referenceProducts.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                imageF = snapshot.child("productImage").getValue().toString();
                nameF = snapshot.child("productTitle").getValue().toString();
                unitF = snapshot.child("productUnit").getValue().toString();
                salePriceF = snapshot.child("productSalePrice").getValue().toString();
                pidF = snapshot.child("productId").getValue().toString();

                Glide.with(getApplicationContext()).load(imageF).into(checkoutProductImageIv);
                checkoutProductNameTv.setText(nameF);
                checkoutProductUnitTv.setText(unitF);
                checkoutProductSalePriceTv.setText(salePriceF);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        referenceMembers.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                try {
                    String myFName = snapshot.child("CustomerFirstName").getValue().toString();
                    String myLName = snapshot.child("CustomerLastName").getValue().toString();
                    String myCity = snapshot.child("CustomerCity").getValue().toString();
                    String myAddress = snapshot.child("CustomerAddress").getValue().toString();
                    String myPhone = snapshot.child("CustomerContactNumber").getValue().toString();

                    checkoutNameEt.setText(myFName + " " + myLName);
                    checkoutStreetAddressEt.setText(myAddress);
                    checkoutPhoneEt.setText(myPhone);

                    ArrayAdapter<CharSequence> arrayAdapterCategory = ArrayAdapter.createFromResource(getApplicationContext(),
                            R.array.cities, android.R.layout.simple_list_item_activated_1);
                    arrayAdapterCategory.setDropDownViewResource(android.R.layout.simple_list_item_1);
                    checkoutCitySpinner.setAdapter(arrayAdapterCategory);
                    checkoutCitySpinner.setSelection(arrayAdapterCategory.getPosition(myCity));
                    checkoutCitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            checkoutCityLink = parent.getItemAtPosition(position).toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });

                } catch (Exception ignored) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        checkoutConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loader.setMessage("Wait until we confirm your order");
                loader.setCanceledOnTouchOutside(false);
                loader.show();

                final String personNameCin = checkoutNameEt.getText().toString();
                final String personAddressCin = checkoutStreetAddressEt.getText().toString();
                final String personMobileCin = checkoutPhoneEt.getText().toString();
                final String personConfirmMobileCin = checkoutConfirmPhoneEt.getText().toString();
                final String personDescriptionCin = checkoutDescriptionEt.getText().toString();

                if (personNameCin.isEmpty()) {
                    loader.dismiss();
                    checkoutNameEt.setError("Enter your name please");
                } else if (personAddressCin.isEmpty()) {
                    loader.dismiss();
                    checkoutStreetAddressEt.setError("Enter your address please");
                } else if (personMobileCin.isEmpty()) {
                    loader.dismiss();
                    checkoutPhoneEt.setError("Enter your contact please");
                } else if (personConfirmMobileCin.isEmpty()) {
                    loader.dismiss();
                    checkoutConfirmPhoneEt.setError("Enter your confirm contact please");
                } else {
                    final Map<String, Object> map = new HashMap<>();
                    map.put("personUID", mAuth.getCurrentUser().getUid());
                    map.put("personEmail", mAuth.getCurrentUser().getEmail());
                    map.put("personName", personNameCin);
                    map.put("personAddress", personAddressCin);
                    map.put("personMobile", personMobileCin);
                    map.put("personConfirmMobile", personConfirmMobileCin);
                    map.put("personDescription", personDescriptionCin);
                    map.put("productId", pidF);
                    map.put("productQuantity", quantityKey);
                    map.put("productImage", imageF);
                    map.put("productName", nameF);
                    map.put("productPrice", salePriceF);
                    map.put("productTotal", totalKey);

                    referenceOrder.push().setValue(map)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        try {

                                            referenceMembers.child(mAuth.getCurrentUser().getUid())
                                                    .child("MyCart").child(pidF).removeValue()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {

                                                                TOPIC = tkn; //topic must match with what the receiver subscribed to

                                                                try {
                                                                    Log.e("sjdbkajs", TOPIC);
                                                                } catch (Exception e) {
                                                                    Log.e("maslakiay", e.getMessage());
                                                                }

                                                                NOTIFICATION_TITLE = "Product Name : " + nameF;
                                                                NOTIFICATION_MESSAGE = "Person Contact : " + checkoutPhoneEt.getText().toString();

                                                                JSONObject notification = new JSONObject();
                                                                JSONObject notifcationBody = new JSONObject();
                                                                try {
                                                                    notifcationBody.put("title", NOTIFICATION_TITLE);
                                                                    notifcationBody.put("message", NOTIFICATION_MESSAGE);

                                                                    notification.put("to", TOPIC);
                                                                    notification.put("data", notifcationBody);
                                                                } catch (JSONException e) {
                                                                    Log.e(TAG, "onCreate: " + e.getMessage());
                                                                }
                                                                sendNotification(notification);


                                                                Toast.makeText(ProceedToCheckutActivity.this, "Order Confirmed Successfully", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                Toast.makeText(ProceedToCheckutActivity.this, task.toString(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });

                                        } catch (Exception ignored) {

                                        }

                                        loader.dismiss();
                                        startActivity(new Intent(getApplicationContext(), UserMenu.class));
                                        finish();
                                        Toast.makeText(ProceedToCheckutActivity.this, "Order Confirmed Successfully", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

            }
        });
    }

    private void sendNotification(JSONObject notification) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                response -> {
                    Log.i(TAG, "onResponse: " + response.toString());
//                        edtTitle.setText("");
//                        edtMessage.setText("");
                },
                error -> {
                    Toast.makeText(ProceedToCheckutActivity.this, "Request error", Toast.LENGTH_LONG).show();
                    Log.i(TAG, "onErrorResponse: Didn't work");
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }
}