package com.makgyber.vsells.adapters;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.makgyber.vsells.R;
import com.makgyber.vsells.models.Message;
import com.squareup.picasso.Picasso;


public class MessageAdapter extends FirestoreRecyclerAdapter<Message, MessageAdapter.MessageHolder> {

    public MessageAdapter(@NonNull FirestoreRecyclerOptions<Message> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MessageHolder holder, final int position, @NonNull final Message model) {
        holder.tvContent.setText(model.getContent());
        holder.tvTimestamp.setText(formatDate(model.getDateCreated()));

        if (model.getImageUri() != null && !model.getImageUri().isEmpty()) {
            Picasso.get().load(model.getImageUri()).centerCrop().resize(200, 200).into(holder.ivProfileImage);
        }

    }

    @Override
    public int getItemViewType(int position) {
        Message model = getItem(position);
        if (model.getSenderType().equalsIgnoreCase("buyer")) {
            return 1;
        } else {
            return 2;
        }
    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v;
        if (viewType == 1) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sent_message_item, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.received_message_item, parent, false);
        }

        return new MessageHolder(v);
    }

    class MessageHolder extends RecyclerView.ViewHolder {
        TextView tvContent, tvTimestamp;
        ImageView ivProfileImage;

        public MessageHolder(@NonNull final View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.tv_content);
            tvTimestamp = itemView.findViewById(R.id.tv_timestamp);
            ivProfileImage = itemView.findViewById(R.id.image_message_profile);
        }



    }

    private String formatDate(Timestamp dateCreated)  {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        sdf.setTimeZone(TimeZone.getTimeZone("HKT"));
        TimeZone tz = TimeZone.getDefault();
        sdf.setTimeZone(tz);
        dateCreated.toDate().toString();
        return sdf.format( dateCreated.toDate());
    }

}



