package com.dessylazarova.medicare.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.dessylazarova.medicare.AuthUtils;
import com.dessylazarova.medicare.R;
import com.dessylazarova.medicare.databinding.ActivityMainBinding;
import com.dessylazarova.medicare.ui.fragments.AllPatientsFragment;
import com.dessylazarova.medicare.ui.fragments.NewPatientFragment;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity implements
        AllPatientsFragment.OnFragmentInteractionListener, NewPatientFragment.OnFragmentInteractionListener {
    private ActivityMainBinding binding;
    private FirebaseFirestore db;
    private AllPatientsFragment patientsFragment;
    private NewPatientFragment newPatientFragment;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setSupportActionBar(binding.bottomAppBar);
        attachAllPatientsFragment();

        binding.btnAddPatient.setOnClickListener(v -> {
            newPatientFragment = new NewPatientFragment();
            currentFragment = newPatientFragment;
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, newPatientFragment)
                    .addToBackStack(null).commit();
            binding.btnAddPatient.hide();
        });
    }

    private void attachAllPatientsFragment() {
        patientsFragment = new AllPatientsFragment();
        currentFragment = patientsFragment;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, patientsFragment).commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    @Override
    public void onFragmentClosed() {
        binding.btnAddPatient.show();
    }

    @Override
    public void onBackPressed() {
        if (currentFragment instanceof NewPatientFragment) {
            ((NewPatientFragment) currentFragment).respondToBackPress();
        }
    }

}
