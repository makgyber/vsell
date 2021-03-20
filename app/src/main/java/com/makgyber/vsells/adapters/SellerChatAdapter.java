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
import com.squareup.picasso.Picasso;

public class SellerChatAdapter extends FirestoreRecyclerAdapter<Chat, SellerChatAdapter.ChatHolder> {

    public SellerChatAdapter(@NonNull FirestoreRecyclerOptions<Chat> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatHolder holder, final int position, @NonNull final Chat model) {
        Log.d("TAG", "onBindViewHolder: " + model.getId());

        holder.tvTopic.setText(model.getTopic());
        Log.d("ChatAdapter", "onBindViewHolder: SELLER");
        holder.tvStoreName.setText(model.getBuyerName());
        if (model.getBuyerImage() != null && !model.getBuyerImage().toString().isEmpty()) {
            Picasso.get().load(model.getBuyerImage().toString()).centerCrop().resize(200, 200).into(holder.ivProfileImage);
        }

        if (model.isSellerSeen()) {
            holder.ivNewMessage.setVisibility(View.INVISIBLE);
        }

        holder.tvTimestamp.setText(formatDate(model.getDateCreated()));
        holder.chatId = model.getId();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String chat_id = model.getId();
                Intent intent = new Intent(v.getContext(), MessageActivity.class );
                intent.putExtra("chatId", chat_id);
                intent.putExtra("topic", model.getTopic());
                intent.putExtra("persona", "seller");
                intent.putExtra("talkerId", model.getBuyerId());
                intent.putExtra("talkerName", model.getBuyerName());
                v.getContext().startActivity(intent);
            }
        });
    }


    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);
        return new ChatHolder(v);
    }

    class ChatHolder extends RecyclerView.ViewHolder {
        TextView tvTopic, tvStoreName, tvTimestamp;
        String chatId;
        ImageView ivProfileImage, ivNewMessage;

        public ChatHolder(@NonNull final View itemView) {
            super(itemView);
            tvTopic = itemView.findViewById(R.id.tv_topic);
            tvStoreName = itemView.findViewById(R.id.tv_store_name);
            tvTimestamp = itemView.findViewById(R.id.tv_timestamp);
            ivProfileImage = itemView.findViewById(R.id.iv_profile_photo2);
            ivNewMessage = itemView.findViewById(R.id.iv_new_message);
        }

    }

    private String formatDate(Timestamp dateCreated)  {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy' 'HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("HKT"));
        TimeZone tz = TimeZone.getDefault();
        sdf.setTimeZone(tz);
        dateCreated.toDate().toString();
        return sdf.format( dateCreated.toDate());
    }

}


