package com.makgyber.vbuys.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.makgyber.vbuys.R;
import com.makgyber.vbuys.adapters.ChatAdapter;
import com.makgyber.vbuys.models.Chat;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {
    private final String TAG="ChatFragment";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference chatRef = db.collection("chat");
    private ChatAdapter adapter;
    private TextView tvEmpty;

    public ChatFragment() {
        // Required empty public constructor
    }

    /**
     * @return A new instance of fragment ChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatFragment newInstance() {
        ChatFragment fragment = new ChatFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getChatList(view);
    }

    private void getChatList(View vw) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("USER_PROFILE", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", "");
        Log.d(TAG, "getChatList: UserId - " + userId);
        RecyclerView recyclerView = vw.findViewById(R.id.rv_chat);
        tvEmpty = vw.findViewById(R.id.tv_empty);
        Query query = chatRef.whereEqualTo("buyerId", userId);
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                tvEmpty.setVisibility(queryDocumentSnapshots.getDocuments().isEmpty() ? View.VISIBLE : View.GONE);
            }
        });

        FirestoreRecyclerOptions<Chat> options = new FirestoreRecyclerOptions.Builder<Chat>()
                .setQuery(query, new SnapshotParser<Chat>() {
                    @NonNull
                    @Override
                    public Chat parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        Chat chat = snapshot.toObject(Chat.class);
                        chat.setId( snapshot.getId() );
                        return chat;
                    }
                })
                .build();

        adapter = new ChatAdapter(options);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

    }


    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }
}
