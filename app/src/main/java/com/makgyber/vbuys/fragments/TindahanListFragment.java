package com.makgyber.vbuys.fragments;

import android.content.Intent;
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
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.makgyber.vbuys.R;
import com.makgyber.vbuys.activities.InventoryActivity;
import com.makgyber.vbuys.activities.StoreSetupActivity;
import com.makgyber.vbuys.adapters.ProductAdapter;
import com.makgyber.vbuys.adapters.TindahanAdapter;
import com.makgyber.vbuys.models.Product;
import com.makgyber.vbuys.models.Tindahan;

import org.w3c.dom.Text;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TindahanListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TindahanListFragment extends Fragment {

    private final String TAG="TindahanListFragment";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference tindahanRef = db.collection("tindahan");
    private TindahanAdapter adapter;
    private TextView tvEmpty;

    public TindahanListFragment() {
        // Required empty public constructor
    }

    public static TindahanListFragment newInstance() {
        TindahanListFragment fragment = new TindahanListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
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
        return inflater.inflate(R.layout.fragment_tindahan_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getInventoryList(view);
    }

    private void getInventoryList(View vw) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("USER_PROFILE", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", "");
        Log.d(TAG, "getInventoryList: UserId - " + userId);
        Query query = tindahanRef.whereEqualTo("owner", userId);
        RecyclerView recyclerView = vw.findViewById(R.id.rv_tindahan);
        tvEmpty = vw.findViewById(R.id.tv_empty);
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                tvEmpty.setVisibility(queryDocumentSnapshots.getDocuments().isEmpty() ? View.VISIBLE : View.GONE);
            }
        });
        FirestoreRecyclerOptions<Tindahan> options = new FirestoreRecyclerOptions.Builder<Tindahan>()
                .setQuery(query, new SnapshotParser<Tindahan>() {
                    @NonNull
                    @Override
                    public Tindahan parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        Tindahan tindahan = snapshot.toObject(Tindahan.class);
                        tindahan.setId( snapshot.getId() );
                        return tindahan;
                    }
                })
                .build();

        adapter = new TindahanAdapter(options);


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
