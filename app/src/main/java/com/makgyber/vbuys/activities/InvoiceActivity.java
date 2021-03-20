package com.makgyber.vbuys.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.makgyber.vbuys.R;
import com.makgyber.vbuys.fragments.InvoiceListFragment;

public class InvoiceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);
        getSupportActionBar().setTitle("Invoices");
        getSupportActionBar().setHomeButtonEnabled(true);

        loadFragment(InvoiceListFragment.newInstance("seller"));

    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
