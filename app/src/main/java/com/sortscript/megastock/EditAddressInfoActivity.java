package com.sortscript.megastock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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

public class EditAddressInfoActivity extends AppCompatActivity {

    ProgressDialog loader;
    DatabaseReference reference;
    FirebaseAuth mAuth;
    TextInputEditText countryEditAddressEt, addressEditAddressEt, postalCodeEditAddressEt;
    Spinner cityEditAddressSpinner;
    Button updateEditAddressBtn;
    String cityLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_address_info);

        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("Members").child("Customers").child(mAuth.getCurrentUser().getUid());
        loader = new ProgressDialog(this);

        countryEditAddressEt = findViewById(R.id.countryEditAddressId);
        addressEditAddressEt = findViewById(R.id.addressEditAddressId);
        postalCodeEditAddressEt = findViewById(R.id.postalCodeEditAddressId);
        cityEditAddressSpinner = findViewById(R.id.cityEditAddressId);
        updateEditAddressBtn = findViewById(R.id.updateEditAddressBtnId);

        reference.child("UserDetails").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String pAddress = snapshot.child("CustomerAddress").getValue().toString();
                String pCity = snapshot.child("CustomerCity").getValue().toString();
                String pCountry = snapshot.child("CustomerCountry").getValue().toString();
                String pPostal = snapshot.child("CustomerPostalCode").getValue().toString();

                countryEditAddressEt.setText(pCountry);
                addressEditAddressEt.setText(pAddress);
                postalCodeEditAddressEt.setText(pPostal);

                ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(EditAddressInfoActivity.this,
                        R.array.cities, android.R.layout.simple_list_item_activated_1);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                cityEditAddressSpinner.setAdapter(arrayAdapter);
                cityEditAddressSpinner.setSelection(arrayAdapter.getPosition(pCity));
                cityEditAddressSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cityLink = parent.getItemAtPosition(position).toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        updateEditAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loader.setTitle("Updating Address Info");
                loader.setCanceledOnTouchOutside(false);
                loader.show();

                String addressU = addressEditAddressEt.getText().toString();
                String postalU = postalCodeEditAddressEt.getText().toString();
                String countryU = countryEditAddressEt.getText().toString();

                if (addressU.isEmpty()) {
                    addressEditAddressEt.setError("Address can't be Empty");
                } else {
                    Map<String, Object> map = new HashMap<>();
                    map.put("CustomerAddress", addressU);
                    map.put("CustomerPostalCode", postalU);
                    map.put("CustomerCountry", countryU);
                    map.put("CustomerCity", cityLink);
                    reference.child("UserDetails").updateChildren(map)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        loader.dismiss();
                                        Toast.makeText(EditAddressInfoActivity.this, "Saved!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        loader.dismiss();
                                        Toast.makeText(EditAddressInfoActivity.this, task.toString(), Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                }
            }
        });
    }
}