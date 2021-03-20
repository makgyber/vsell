package com.makgyber.vsells.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.makgyber.vsells.R;
import com.makgyber.vsells.adapters.SellerChatAdapter;
import com.makgyber.vsells.models.Chat;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SellerChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SellerChatFragment extends Fragment {
    private final String TAG="SellerChatFragment";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference chatRef = db.collection("chat");
    private SellerChatAdapter adapter;
    private TextView tvEmpty;

    public SellerChatFragment() {
        // Required empty public constructor
    }

    /**
     * @return A new instance of fragment ChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SellerChatFragment newInstance() {
        SellerChatFragment fragment = new SellerChatFragment();
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

        getSellerChatList(view);
    }

    private void getSellerChatList(View vw) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("TINDAHAN", MODE_PRIVATE);
        String storeId = sharedPreferences.getString("tindahanId", "");

        Log.d(TAG, "getSellerChatList: StoreId - " + storeId);
        RecyclerView recyclerView = vw.findViewById(R.id.rv_chat);
        tvEmpty = vw.findViewById(R.id.tv_empty);
        Query query = chatRef.whereEqualTo("storeId", storeId);
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

        adapter = new SellerChatAdapter(options);


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
