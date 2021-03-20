package com.makgyber.vsells.adapters;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;
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
import com.google.firebase.Timestamp;
import com.makgyber.vsells.R;
import com.makgyber.vsells.activities.MessageActivity;
import com.makgyber.vsells.models.Chat;
import com.makgyber.vsells.models.Invoice;

public class InvoiceAdapter extends FirestoreRecyclerAdapter<Invoice, InvoiceAdapter.InvoiceHolder> {

    public InvoiceAdapter(@NonNull FirestoreRecyclerOptions<Invoice> options) { super(options); }

    @Override
    protected void onBindViewHolder(@NonNull InvoiceAdapter.InvoiceHolder holder, final int position, @NonNull final Invoice model) {
        Log.d("TAG", "onBindViewHolder: " + model.getId());
        holder.tvTopic.setText(model.getBuyerName());

//
//        holder.tvStoreName.setText(model.getStoreName());
//        if (model.getStoreImage() != null && !model.getStoreImage().toString().isEmpty()) {
//            Picasso.get().load(model.getStoreImage().toString()).centerCrop().resize(200, 200).into(holder.ivProfileImage);
//        }
//
//        if (model.isBuyerSeen()) {
//            holder.ivNewMessage.setVisibility(View.INVISIBLE);
//        }
//
//        holder.tvTimestamp.setText(formatDate(model.getDateCreated()));
//        holder.chatId = model.getId();
//
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String chat_id = model.getId();
//                Intent intent = new Intent(v.getContext(), MessageActivity.class );
//                intent.putExtra("chatId", chat_id);
//                intent.putExtra("topic", model.getTopic());
//                intent.putExtra("persona", "buyer");
//                intent.putExtra("talkerId", model.getStoreId());
//                intent.putExtra("talkerName", model.getStoreName());
//                v.getContext().startActivity(intent);
//            }
//        });
    }


    @NonNull
    @Override
    public InvoiceAdapter.InvoiceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.invoice_item, parent, false);
        return new InvoiceAdapter.InvoiceHolder(v);
    }

    class InvoiceHolder extends RecyclerView.ViewHolder {
        TextView tvTopic, tvStoreName, tvTimestamp;
        String chatId;

        public InvoiceHolder(@NonNull final View itemView) {
            super(itemView);
            tvTopic = itemView.findViewById(R.id.tv_topic);
            tvStoreName = itemView.findViewById(R.id.tv_store_name);
            tvTimestamp = itemView.findViewById(R.id.tv_timestamp);
        }

    }

    private String formatDate(Timestamp dateCreated) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy' 'HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("HKT"));
        TimeZone tz = TimeZone.getDefault();
        sdf.setTimeZone(tz);
        dateCreated.toDate().toString();
        return sdf.format(dateCreated.toDate());
    }

}