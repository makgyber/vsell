package com.makgyber.vsells.adapters;

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
import com.makgyber.vsells.models.Product;
import com.makgyber.vsells.activities.ProductDetailActivity;
import com.makgyber.vsells.R;
import com.squareup.picasso.Picasso;

public class SearchProductResultsAdapter extends FirestoreRecyclerAdapter<Product, SearchProductResultsAdapter.ProductHolder> {

    public SearchProductResultsAdapter(@NonNull FirestoreRecyclerOptions<Product> options) {
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

        if (model.getImageList() != null && !model.getImageList().isEmpty()) {
            Picasso.get().load(model.getImageList().get(0).toString()).centerCrop().resize(300,300).into(holder.productImage);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String productId = model.getId();
                String productName = model.getProductName();
                String productDescription = model.getDescription();
                String productImageUrl = model.getImageUri();
                String productPrice = Double.toString(model.getPrice());

                Intent intent = new Intent(v.getContext(), ProductDetailActivity.class );
                intent.putExtra("PRODUCT_ID", productId);
                intent.putExtra("PRODUCT_NAME", productName);
                intent.putExtra("PRODUCT_DESCRIPTION", productDescription);
                intent.putExtra("PRODUCT_IMAGE", productImageUrl);
                intent.putExtra("PRODUCT_PRICE", productPrice);
                intent.putExtra("PRODUCT_TINDAHAN_ID", model.getTindahanId());
                intent.putExtra("PRODUCT_TINDAHAN_NAME", model.getTindahanName());
                intent.putStringArrayListExtra("PRODUCT_IMAGE_LIST", model.getImageList());
                v.getContext().startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public ProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_product_result_item, parent, false);
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

