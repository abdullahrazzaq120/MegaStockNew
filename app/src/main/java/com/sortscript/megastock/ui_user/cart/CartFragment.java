package com.sortscript.megastock.ui_user.cart;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sortscript.megastock.DisplayProductDetailsActivity;
import com.sortscript.megastock.Holder.HolderCart;
import com.sortscript.megastock.Model.ModelCart;
import com.sortscript.megastock.Model.ProductsModel;
import com.sortscript.megastock.ProceedToCheckutActivity;
import com.sortscript.megastock.R;
import com.sortscript.megastock.SignInWithEmailActivity;
import com.sortscript.megastock.Vendor.VendorMenu;
import com.sortscript.megastock.ui_user.products_user.ProductsUserHolder;

public class CartFragment extends Fragment {

    private CartViewModel cartViewModel;
    RecyclerView userCartRv;
    FirebaseAuth mAuth;
    DatabaseReference mRef;
    FirebaseRecyclerAdapter<ModelCart, HolderCart> adapter;
    FirebaseRecyclerOptions<ModelCart> options;
    LinearLayoutManager linearLayoutManager;
    int priceSingleInt;
    ProgressDialog loader2;
    TextView delete_tv_cart;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        View root = inflater.inflate(R.layout.fragment_cart_user, container, false);

        loader2 = new ProgressDialog(getActivity(), R.style.CustomDialogTheme);
        mAuth = FirebaseAuth.getInstance();
        userCartRv = root.findViewById(R.id.userCartRvId);
        delete_tv_cart = root.findViewById(R.id.delete_tv_cartId);
        mRef = FirebaseDatabase.getInstance().getReference().child("Members").child("Customers");
        linearLayoutManager = new LinearLayoutManager(getActivity());

        delete_tv_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog alertDialog;
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View v2 = inflater.inflate(R.layout.delete_dialog, null);
                dialog.setView(v2);
                alertDialog = dialog.create();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                alertDialog.show();

                TextView tv1 = v2.findViewById(R.id.yesDeleteBtnId);
                TextView tv2 = v2.findViewById(R.id.cancelDeleteBtnId);

                tv1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            mRef.child(mAuth.getCurrentUser().getUid()).child("MyCart").removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getActivity(), "Successfully Deleted!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getActivity(), task.toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
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
            }
        });

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        options = new FirebaseRecyclerOptions.Builder<ModelCart>()
                .setQuery(mRef.child(mAuth.getCurrentUser().getUid()).child("MyCart"), ModelCart.class).build();

        adapter = new FirebaseRecyclerAdapter<ModelCart, HolderCart>(options) {

            @Override
            protected void onBindViewHolder(@NonNull final HolderCart holderCart, final int i, @NonNull final ModelCart modelCart) {

                try {

                    holderCart.setCartDetails(getActivity(), modelCart.getCartName(), modelCart.getCartPrice(),
                            modelCart.getCartImage(), modelCart.getCartUnit(), modelCart.getCartPId());

                } catch (Exception e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                holderCart.setOnclickListener(new HolderCart.ClickListener() {
                    @Override
                    public void onSingleProceedClick(View view, int position) {

                        String total = holderCart.cartTotalTv.getText().toString();
                        String quan = holderCart.cartQuantityTv.getText().toString();

                        Intent intent = new Intent(getActivity(), ProceedToCheckutActivity.class);
                        intent.putExtra("proceedProductKey", modelCart.getCartNodeKey());
                        intent.putExtra("proceedProductTotal", total);
                        intent.putExtra("proceedProductQuantity", quan);
                        startActivity(intent);
//                        }

                    }

                    @Override
                    public void onIncrementClick(View view, int position) {

                        loader2.show();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                holderCart.cartIncrementBtn.setSoundEffectsEnabled(false);
                                holderCart.quantityCart = holderCart.quantityCart + 1;
                                holderCart.cartQuantityTv.setText(String.valueOf(holderCart.quantityCart));
                                holderCart.cartDecrementBtn.setVisibility(View.VISIBLE);

                                priceSingleInt = Integer.parseInt(holderCart.cartPriceTv.getText().toString());
                                int tp = priceSingleInt * holderCart.quantityCart;
                                String tpString = String.valueOf(tp);
                                holderCart.cartTotalTv.setText(tpString);

                                if (holderCart.quantityCart > 1) {
                                    holderCart.cartDecrementBtn.setEnabled(true);
                                }

                                if (holderCart.quantityCart == Integer.parseInt(modelCart.getCartStockQuantity())) {
                                    holderCart.cartIncrementBtn.setEnabled(false);
                                }

                                loader2.dismiss();
                            }
                        }, 600);
                    }

                    @Override
                    public void onDecrementClick(View view, int position) {

                        loader2.show();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (holderCart.quantityCart == 1) {
                                    holderCart.cartDecrementBtn.setEnabled(false);
                                }
                                if (holderCart.quantityCart > 1) {
                                    holderCart.cartDecrementBtn.setEnabled(true);
                                    holderCart.quantityCart = holderCart.quantityCart - 1;
                                    holderCart.cartQuantityTv.setText(String.valueOf(holderCart.quantityCart));

                                    priceSingleInt = Integer.parseInt(holderCart.cartPriceTv.getText().toString());
                                    int tp = priceSingleInt * holderCart.quantityCart;
                                    String tpString = String.valueOf(tp);
                                    holderCart.cartTotalTv.setText(tpString);
                                }

                                loader2.dismiss();
                            }
                        }, 600);
                    }

                    @Override
                    public void onDeleteClick(View view, int position) {
                        getSnapshots().getSnapshot(position).getRef().removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getActivity(), "Product remove from cart", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getActivity(), "Failed to remove", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                });
            }

            @NonNull
            @Override
            public HolderCart onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_design, parent, false);
                return new HolderCart(view, getActivity());
            }
        };

        userCartRv.setLayoutManager(linearLayoutManager);
        userCartRv.setAdapter(adapter);
        adapter.startListening();
        adapter.notifyDataSetChanged();
        userCartRv.setHasFixedSize(true);
    }
}