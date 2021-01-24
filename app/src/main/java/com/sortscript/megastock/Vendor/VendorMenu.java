package com.sortscript.megastock.Vendor;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.sortscript.megastock.R;
import com.sortscript.megastock.SignInWithEmailActivity;

import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class VendorMenu extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    FirebaseAuth mAuth;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_menu);
        Toolbar toolbar = findViewById(R.id.toolbarVendor);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("Vendor");
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view_vendor);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        navigationView.getMenu().findItem(R.id.nav_logout_vendor).setOnMenuItemClickListener(menuItem -> {

            AlertDialog alertDialog;
            AlertDialog.Builder dialog = new AlertDialog.Builder(VendorMenu.this);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v2 = inflater.inflate(R.layout.logout_dialog, null);
            dialog.setView(v2);
            alertDialog = dialog.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            alertDialog.show();

            TextView tv1 = v2.findViewById(R.id.yesLogoutBtnId);
            TextView tv2 = v2.findViewById(R.id.cancelLogoutBtnId);

            tv1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        mAuth.signOut();
                        Intent i = new Intent(VendorMenu.this, SignInVendorActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        alertDialog.dismiss();
                    } catch (Exception ignored) {

                    }
                }
            });

            tv2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }
            });

            return true;
        });

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_orders_vendor, R.id.nav_add_products_vendor, R.id.nav_view_vendor, R.id.nav_change_password_vendor)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_vendor);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        String tkn = FirebaseInstanceId.getInstance().getToken();
//        reference.child("deviceToken").setValue(tkn);
//    }
}