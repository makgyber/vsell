package com.makgyber.vsells.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.makgyber.vsells.R;
import com.makgyber.vsells.adapters.MessageAdapter;
import com.makgyber.vsells.adapters.SellerMessageAdapter;
import com.makgyber.vsells.models.Message;

public class MessageActivity extends AppCompatActivity {

    String chatId, topic, persona, talker, profileImage, talkerId;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference messageRef = db.collection("message");
    private MessageAdapter adapter;
    private SellerMessageAdapter sellerAdapter;
    private ImageButton btnSend, btnImage;
    private EditText edtContent;
    RecyclerView recyclerView;
    private final static String defaultProfileImage = "https://firebasestorage.googleapis.com/v0/b/villagebuys-13fa9.appspot.com/o/images%2Fusers%2FiOWv9iplGZC29sd1EJSb.jpg?alt=media&token=6487b466-5377-4589-b621-76809228188e";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        if (getIntent().hasExtra("chatId")) {
            chatId = getIntent().getExtras().get("chatId").toString();
        }

        if (getIntent().hasExtra("topic")) {
            topic = getIntent().getExtras().get("topic").toString();
        }

        if (getIntent().hasExtra("talkerId")) {
            talkerId = getIntent().getExtras().get("talkerId").toString();
        }

        if (getIntent().hasExtra("talkerName")) {
            talker = getIntent().getExtras().get("talkerName").toString();
        }

        if (getIntent().hasExtra("persona")) {
            persona = getIntent().getExtras().get("persona").toString();
        }

        if (persona.equals("seller")) {
            setTheme(R.style.SellerTheme);
        }

        setContentView(R.layout.activity_message);

        getSupportActionBar().setTitle(topic);
        getSupportActionBar().setSubtitle(talker);
        getSupportActionBar().setHomeButtonEnabled(true);


        edtContent = findViewById(R.id.edt_content);

        btnSend = findViewById(R.id.button_chatbox_send);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createMessage(v);
            }
        });

        getMessageList();
        prepareProfileImage();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.message_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            this.onBackPressed();
            return true;
        }

        return false;
    }

    private void prepareProfileImage() {
        if (persona.equalsIgnoreCase("buyer")) {
            db.collection("chat").document(chatId).update("buyerSeen", true);
            SharedPreferences sharedUserPreferences = getApplicationContext().getSharedPreferences("USER_PROFILE", MODE_PRIVATE);
            profileImage = sharedUserPreferences.getString("photoUrl",  defaultProfileImage);
        } else {
            db.collection("chat").document(chatId).update("sellerSeen", true);
            SharedPreferences sharedStorePreferences = getApplicationContext().getSharedPreferences("TINDAHAN", MODE_PRIVATE);
            profileImage = sharedStorePreferences.getString("tindahanLogo",  defaultProfileImage);
        }
    }

    private void createMessage(View v) {
        if (!edtContent.getText().toString().isEmpty()) {
            String content = edtContent.getText().toString();
            edtContent.setText("");
            Timestamp now = Timestamp.now();
            Message newMesg = new Message(chatId, now, persona, content, profileImage);

            messageRef.document().set(newMesg).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    if(persona.equalsIgnoreCase("seller")) {
                        recyclerView.scrollToPosition(sellerAdapter.getItemCount() - 1);
                        //update lastMessageCreated in chat document
                        db.collection("chat").document(chatId).update(
                                "lastMessageCreated", now,
                                "buyerSeen", false,
                                "sellerSeen", true
                        );
                    } else {
                        recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                        //update lastMessageCreated in chat document
                        db.collection("chat").document(chatId).update(
                                "lastMessageCreated", now,
                                "buyerSeen", true,
                                "sellerSeen", false
                        );
                    }

                }
            });

            //save the new time in sharedpreferences
            SharedPreferences chatPref = getSharedPreferences("CHAT", MODE_PRIVATE);
            SharedPreferences.Editor editor = chatPref.edit();
            editor.putString(chatId, now.toString());
            editor.commit();
        }

    }

    private void getMessageList() {
        Query query = messageRef.whereEqualTo("chatId", chatId).orderBy("dateCreated", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Message> options = new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(query, new SnapshotParser<Message>() {
                    @NonNull
                    @Override
                    public Message parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        Message message = snapshot.toObject(Message.class);
                        return message;
                    }
                })
                .build();



        recyclerView = findViewById(R.id.rv_messages);
        recyclerView.setHasFixedSize(false);
        LinearLayoutManager llman = new LinearLayoutManager(MessageActivity.this);
        llman.setStackFromEnd(true);
        recyclerView.setLayoutManager(llman);

        if (persona.equalsIgnoreCase("seller")) {
            sellerAdapter = new SellerMessageAdapter(options);
            recyclerView.setAdapter(sellerAdapter);
        } else {
            adapter = new MessageAdapter(options);
            recyclerView.setAdapter(adapter);
        }


    }


    @Override
    public void onStop() {
        super.onStop();
        if (persona.equalsIgnoreCase("buyer")) {
            adapter.stopListening();
        } else {
            sellerAdapter.stopListening();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (persona.equalsIgnoreCase("buyer")) {
            adapter.startListening();
            recyclerView.scrollToPosition(adapter.getItemCount() - 1);
        } else {
            sellerAdapter.startListening();
            recyclerView.scrollToPosition(sellerAdapter.getItemCount() - 1);
        }

    }
}
