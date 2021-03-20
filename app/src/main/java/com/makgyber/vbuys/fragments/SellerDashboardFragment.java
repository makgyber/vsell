package com.makgyber.vbuys.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makgyber.vbuys.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SellerDashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SellerDashboardFragment extends Fragment {

    public SellerDashboardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SellerDashboardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SellerDashboardFragment newInstance(String param1, String param2) {
        SellerDashboardFragment fragment = new SellerDashboardFragment();
        Bundle args = new Bundle();
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
        return inflater.inflate(R.layout.fragment_seller_dashboard, container, false);
    }
}
