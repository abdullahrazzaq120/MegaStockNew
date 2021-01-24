package com.sortscript.megastock;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.sortscript.megastock.Vendor.SignInVendorActivity;
import com.sortscript.megastock.Vendor.VendorProfileActivity;

public class WhoYouAre extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_who_you_are);

        getSupportActionBar().hide();


        CardView cardView = findViewById(R.id.shopkeeperId);
        CardView cardView1 = findViewById(R.id.customerId);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WhoYouAre.this, SignInVendorActivity.class);
                startActivity(intent);
            }
        });

        cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WhoYouAre.this, SignInWithEmailActivity.class);
                startActivity(intent);
            }
        });

    }
}