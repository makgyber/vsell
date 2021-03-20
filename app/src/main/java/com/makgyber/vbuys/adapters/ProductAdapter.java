package com.makgyber.vbuys.adapters;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.makgyber.vbuys.activities.InventoryDetailActivity;
import com.makgyber.vbuys.models.Product;
import com.makgyber.vbuys.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProductAdapter extends FirestoreRecyclerAdapter<Product, ProductAdapter.ProductHolder> {

    public ProductAdapter(@NonNull FirestoreRecyclerOptions<Product> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ProductHolder holder, final int position, @NonNull final Product model) {
        Log.d("TAG", "onBindViewHolder: " + model.getId());

        holder.textViewProductName.setText(model.getProductName());
        holder.textViewProductDescription.setText(model.getDescription());
        holder.textViewProductPrice.setText(Double.toString(model.getPrice()));
        holder.textViewTindahan.setText(model.getTindahanName());
        holder.productId = model.getId();
//        if (model.getImageUri() != null && !model.getImageUri().toString().isEmpty()) {
//            Picasso.get().load(model.getImageUri().toString()).centerCrop().resize(200,200).into(holder.productImage);
//            Log.d("PRODUCT ADAPTER", "onBindViewHolder: " + model.getImageUri().toString());
//        }
        if (model.getImageList() != null && !model.getImageUri().isEmpty()) {
            Picasso.get().load(model.getImageList().get(0).toString()).centerCrop().resize(200,200).into(holder.productImage);
            Log.d("PRODUCT ADAPTER", "onBindViewHolder: " + model.getImageUri().toString());
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String product_id = model.getId();
                Log.d("ProductAdapter", "onClick: product_id " + product_id);
                Log.d("ProductAdapter", "onClick: TINDAHAN_ID " + model.getTindahanId());
                Log.d("ProductAdapter", "onClick: TINDAHAN_NAME " + model.getProductName());

                Intent intent = new Intent(v.getContext(), InventoryDetailActivity.class );
                intent.putExtra("PRODUCT_ID", product_id);
                intent.putExtra("TINDAHAN_ID", model.getTindahanId());
                intent.putExtra("TINDAHAN_NAME", model.getTindahanName());
                intent.putExtra("TINDAHAN_LATITUDE", Double.toString(model.getPosition().getLatitude()));
                intent.putExtra("TINDAHAN_LONGITUDE",  Double.toString(model.getPosition().getLongitude()));

                intent.putStringArrayListExtra("PRODUCT_IMAGE_LIST", model.getImageList());
                v.getContext().startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public ProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
        return new ProductHolder(v);
    }

    class ProductHolder extends RecyclerView.ViewHolder {
        TextView textViewProductName;
        TextView textViewProductDescription;
        TextView textViewProductPrice;
        TextView textViewTindahan;
        String productId;
        ImageView productImage;

        public ProductHolder(@NonNull final View itemView) {
            super(itemView);
            textViewProductName = itemView.findViewById(R.id.txt_product_name);
            textViewProductDescription = itemView.findViewById(R.id.txt_product_description);
            textViewProductPrice = itemView.findViewById(R.id.txt_product_price);
            textViewTindahan = itemView.findViewById(R.id.txt_tindahan);
            productImage = itemView.findViewById(R.id.iv_product_image);
        }
    }

}
