package com.sortscript.megastock.ui_user.dashboard;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.sortscript.megastock.EditProfileActivity;
import com.sortscript.megastock.R;
import com.sortscript.megastock.SignInWithEmailActivity;
import com.sortscript.megastock.Vendor.VendorMenu;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    Button userLogoutBtn, editProfileBtn;
    FirebaseAuth firebaseAuth;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard_user, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        userLogoutBtn = root.findViewById(R.id.userLogoutBtnId);
        editProfileBtn = root.findViewById(R.id.editProfileBtnId);
        userLogoutBtn.setOnClickListener(view -> {

            AlertDialog alertDialog;
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater2 = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v2 = inflater2.inflate(R.layout.logout_dialog, null);
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
                        firebaseAuth.signOut();
                        Intent i = new Intent(getActivity(), SignInWithEmailActivity.class);
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
        });

        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), EditProfileActivity.class));
            }
        });

        return root;
    }
}