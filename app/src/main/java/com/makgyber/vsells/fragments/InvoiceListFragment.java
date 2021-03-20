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
import com.makgyber.vsells.adapters.InvoiceAdapter;
import com.makgyber.vsells.adapters.InvoiceAdapter;
import com.makgyber.vsells.models.Invoice;

import static android.content.Context.MODE_PRIVATE;

public class InvoiceListFragment extends Fragment {
    private final String TAG="InvoiceFragment";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference invoiceRef = db.collection("invoice");
    private InvoiceAdapter adapter;
    private String context;
    private TextView tvEmpty;

    public InvoiceListFragment(String context) {
        // Required empty public constructor
    }

    /**
     * @return A new instance of fragment InvoiceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InvoiceListFragment newInstance(String context) {
        InvoiceListFragment fragment = new InvoiceListFragment(context);
        Bundle args = new Bundle();
        args.putString("context", context);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        context = args.getString("context");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_invoice, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getInvoiceList(view);
    }

    private void getInvoiceList(View vw) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("USER_PROFILE", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", "");
        Log.d(TAG, "getInvoiceList: UserId - " + userId);
        Query query = invoiceRef.whereEqualTo("buyerId", userId);
        RecyclerView recyclerView = vw.findViewById(R.id.rv_invoice);
        tvEmpty = vw.findViewById(R.id.tv_empty);
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                tvEmpty.setVisibility(queryDocumentSnapshots.getDocuments().isEmpty() ? View.VISIBLE : View.GONE);
            }
        });

        FirestoreRecyclerOptions<Invoice> options = new FirestoreRecyclerOptions.Builder<Invoice>()
                .setQuery(query, new SnapshotParser<Invoice>() {
                    @NonNull
                    @Override
                    public Invoice parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        Invoice invoice = snapshot.toObject(Invoice.class);
                        invoice.setId( snapshot.getId() );
                        Log.d(TAG, "parseSnapshot: ID  "  + snapshot.getId());
                        return invoice;
                    }
                })
                .build();

        adapter = new InvoiceAdapter(options);

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
