package com.sortscript.megastock.Vendor.ui_vendor.change_password;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sortscript.megastock.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class ChangePasswordFragment extends Fragment {

    private ChangePasswordViewModel ordersViewModel;
    EditText changePasswordEt;
    Button changepasswordBtn;
    FirebaseAuth mAuth;
    FirebaseUser user;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ordersViewModel =
                new ViewModelProvider(this).get(ChangePasswordViewModel.class);
        View root = inflater.inflate(R.layout.fragment_change_password, container, false);

        changePasswordEt = root.findViewById(R.id.changePasswordId);
        changepasswordBtn = root.findViewById(R.id.changePasswordBtnId);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        changepasswordBtn.setOnClickListener(v -> {
            final String pass = changePasswordEt.getText().toString().trim();
            if (pass.isEmpty()) {
                changePasswordEt.setError("Enter new password please");
            } else {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Are you sure you want to change password?");
                builder.setPositiveButton("Change", (dialog, which) -> user.updatePassword(pass).addOnSuccessListener(aVoid -> {
                    Toast.makeText(getActivity(), "Password Reset Successfully", Toast.LENGTH_LONG).show();
                    changePasswordEt.setText("");

                }).addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Password Reset Failed", Toast.LENGTH_LONG).show();
                })).setNegativeButton("Close", (dialog, which) -> dialog.cancel());
                AlertDialog d = builder.create();
                d.show();
            }
        });

        return root;
    }
}