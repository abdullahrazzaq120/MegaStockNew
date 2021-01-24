package com.sortscript.megastock.ui_user.products_user;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sortscript.megastock.DisplayProductDetailsActivity;
import com.sortscript.megastock.Holder.ProductsHolder;
import com.sortscript.megastock.Model.ProductsModel;
import com.sortscript.megastock.R;
import com.sortscript.megastock.Vendor.EditVendorProductsActivity;

public class ProductsUsersFragment extends Fragment {

    private ProductsViewModel productsViewModel;
    SearchView userSearchProductsSv;
    RecyclerView userProductsRv;
    DatabaseReference mRef;
    FirebaseRecyclerAdapter<ProductsModel, ProductsUserHolder> adapter;
    FirebaseRecyclerOptions<ProductsModel> options;
    GridLayoutManager _sGridLayoutManager;
    private final String KEY_RECYCLER_STATE = "recycler_state";
    private static Bundle mBundleRecyclerViewState;
    private Parcelable mListState = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        productsViewModel = new ViewModelProvider(this).get(ProductsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_products_user, container, false);

        userProductsRv = root.findViewById(R.id.userProductsRvId);
        userSearchProductsSv = root.findViewById(R.id.userSearchProductsSvId);

        mRef = FirebaseDatabase.getInstance().getReference().child("Products");
        _sGridLayoutManager = new GridLayoutManager(getActivity(), 2);

        return root;
    }

    private void filter(Query query) {

        options = new FirebaseRecyclerOptions.Builder<ProductsModel>()
                .setQuery(query, ProductsModel.class).build();

        adapter = new FirebaseRecyclerAdapter<ProductsModel, ProductsUserHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull final ProductsUserHolder productsUserHolder, final int i, @NonNull final ProductsModel modelHome) {

                try {

                    productsUserHolder.setUserProducts(getActivity(), modelHome.getProductImage(), modelHome.getProductTitle(), modelHome.getProductCompanyBrand(),
                            modelHome.getProductSalePrice());

                } catch (Exception ignored) {

                }

                productsUserHolder.setOnclickListener(new ProductsUserHolder.ClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {

                        try {
                            Intent intent = new Intent(getActivity(), DisplayProductDetailsActivity.class);
                            intent.putExtra("ProductKeyUser", getRef(i).getKey());
                            startActivity(intent);

                        } catch (Exception ignored) {
                        }
                    }
                });
            }

            @NonNull
            @Override
            public ProductsUserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_home_design, parent, false);
                return new ProductsUserHolder(view);
            }
        };

        userProductsRv.setLayoutManager(_sGridLayoutManager);
        userProductsRv.setAdapter(adapter);
        adapter.startListening();
        adapter.notifyDataSetChanged();
        userProductsRv.setHasFixedSize(true);

    }

    @Override
    public void onStart() {
        super.onStart();

        userSearchProductsSv.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.isEmpty()) {
                    Query query = (mRef.orderByChild("productTitle").startAt(newText).endAt(newText + "\uf0ff"));
                    filter(query);

                } else {
                    Query query = (mRef);
                    filter(query);

                }
                return true;
            }
        });

        options = new FirebaseRecyclerOptions.Builder<ProductsModel>()
                .setQuery(mRef, ProductsModel.class).build();

        adapter = new FirebaseRecyclerAdapter<ProductsModel, ProductsUserHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull final ProductsUserHolder productsUserHolder, final int i, @NonNull final ProductsModel modelHome) {

                try {

                    productsUserHolder.setUserProducts(getActivity(), modelHome.getProductImage(), modelHome.getProductTitle(), modelHome.getProductCompanyBrand(),
                            modelHome.getProductSalePrice());

                } catch (Exception ignored) {

                }

                productsUserHolder.setOnclickListener(new ProductsUserHolder.ClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {

                        try {
                            Intent intent = new Intent(getActivity(), DisplayProductDetailsActivity.class);
                            intent.putExtra("ProductKeyUser", getRef(i).getKey());
                            startActivity(intent);

                        } catch (Exception ignored) {
                        }
                    }
                });
            }

            @NonNull
            @Override
            public ProductsUserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_home_design, parent, false);
                return new ProductsUserHolder(view);
            }
        };

        userProductsRv.setLayoutManager(_sGridLayoutManager);
        userProductsRv.setAdapter(adapter);
        adapter.startListening();
        adapter.notifyDataSetChanged();
        userProductsRv.setHasFixedSize(true);

    }

    @Override
    public void onPause() {
        super.onPause();

        mBundleRecyclerViewState = new Bundle();
        mListState = userProductsRv.getLayoutManager().onSaveInstanceState();
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
                    userProductsRv.getLayoutManager().onRestoreInstanceState(mListState);

                }
            }, 50);
        }

        userProductsRv.setLayoutManager(_sGridLayoutManager);
    }
}