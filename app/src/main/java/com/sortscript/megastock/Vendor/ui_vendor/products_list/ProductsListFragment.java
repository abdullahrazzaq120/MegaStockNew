package com.sortscript.megastock.Vendor.ui_vendor.products_list;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sortscript.megastock.Holder.ProductsHolder;
import com.sortscript.megastock.Model.ProductsModel;
import com.sortscript.megastock.R;
import com.sortscript.megastock.Vendor.EditVendorProductsActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ProductsListFragment extends Fragment {

    private ProductsListViewModel productsListViewModel;
    RecyclerView productsListVendorRv;
    DatabaseReference mRef;
    FirebaseStorage mStorageRef;
    FirebaseRecyclerAdapter<ProductsModel, ProductsHolder> adapter;
    FirebaseRecyclerOptions<ProductsModel> options;
    LinearLayoutManager linearLayoutManager;
    private final String KEY_RECYCLER_STATE = "recycler_state";
    private static Bundle mBundleRecyclerViewState;
    private Parcelable mListState = null;
    ProgressDialog loader;
    private StorageReference ref1;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        productsListViewModel = new ViewModelProvider(this).get(ProductsListViewModel.class);
        View root = inflater.inflate(R.layout.fragment_products_list, container, false);
        productsListVendorRv = root.findViewById(R.id.productsListVendorRvId);

        mRef = FirebaseDatabase.getInstance().getReference().child("Products");
        mStorageRef = FirebaseStorage.getInstance();
        loader = new ProgressDialog(getActivity(), R.style.CustomDialogTheme);

        options = new FirebaseRecyclerOptions.Builder<ProductsModel>()
                .setQuery(mRef, ProductsModel.class).build();

        adapter = new FirebaseRecyclerAdapter<ProductsModel, ProductsHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull final ProductsHolder holderAdminHome, final int i, @NonNull final ProductsModel modelHome) {

                try {

                    holderAdminHome.setAdminProductDetails(getActivity(), modelHome.getProductTitle(), modelHome.getProductCostPrice(),
                            modelHome.getProductImage(), modelHome.getProductDescription(), modelHome.getProductSalePrice(), modelHome.getProductStock());

                } catch (Exception e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                holderAdminHome.setOnclickListener(new ProductsHolder.ClickListener() {

                    @Override
                    public void onDeleteClick(View view, final int position) {

                        try {

                            ref1 = mStorageRef.getReferenceFromUrl(modelHome.getProductImage());
                            ref1.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    getSnapshots().getSnapshot(position).getRef().removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(getActivity(), "Failed to delete", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                }
                            });

                        } catch (Exception e) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onItemClick(View view, int position) {

                        try {
                            Intent intent = new Intent(getActivity(), EditVendorProductsActivity.class);
                            intent.putExtra("position", getRef(i).getKey());
                            startActivity(intent);

                        } catch (Exception ignored) {
                        }
                    }
                });
            }

            @NonNull
            @Override
            public ProductsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_admin_home_design, parent, false);
                return new ProductsHolder(view);
            }
        };

        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        productsListVendorRv.setLayoutManager(linearLayoutManager);
        productsListVendorRv.setAdapter(adapter);
        adapter.startListening();
        adapter.notifyDataSetChanged();
        productsListVendorRv.setHasFixedSize(true);
        productsListVendorRv.setNestedScrollingEnabled(false);

        return root;
    }

    @Override
    public void onPause() {
        super.onPause();

        mBundleRecyclerViewState = new Bundle();
        mListState = productsListVendorRv.getLayoutManager().onSaveInstanceState();
        mBundleRecyclerViewState.putParcelable(KEY_RECYCLER_STATE, mListState);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mBundleRecyclerViewState != null) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    mListState = mBundleRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
                    productsListVendorRv.getLayoutManager().onRestoreInstanceState(mListState);

                }
            }, 50);
        }

        productsListVendorRv.setLayoutManager(linearLayoutManager);
    }
}