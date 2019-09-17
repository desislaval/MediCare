package com.dessylazarova.medicare.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.dessylazarova.medicare.R;
import com.dessylazarova.medicare.data.Patient;
import com.dessylazarova.medicare.databinding.ActivityDetailsBinding;
import com.dessylazarova.medicare.ui.fragments.CheckUpFragment;
import com.dessylazarova.medicare.ui.fragments.InformationFragment;
import com.dessylazarova.medicare.ui.fragments.NewCheckUpFragment;
import com.dessylazarova.medicare.ui.fragments.NewPatientFragment;
import com.google.firebase.firestore.DocumentReference;

public class DetailsActivity extends AppCompatActivity implements InformationFragment.OnInformationFragmentListener, CheckUpFragment.OnFragmentInteractionListener, NewCheckUpFragment.OnFragmentInteractionListener, NewPatientFragment.OnFragmentInteractionListener {
    private ActivityDetailsBinding binding;
    private static final String NAME_FIELD = "name";
    private static final String ID_FIELD = "id";
    private InformationFragment informationFragment;
    private NewPatientFragment newPatientFragment;
    private NewCheckUpFragment newCheckUpFragment;
    private Fragment currentFragment;
    private String name;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_details);
        newPatientFragment = new NewPatientFragment();
        setUpToolbar();
        openNewFragment(1);
        binding.btnShowCheckups.setOnClickListener(v -> openNewFragment(2));
        binding.btnAddCheckup.setOnClickListener(v -> openNewFragment(3));
    }

    private void openNewFragment(int number) {
        switch (number) {
            case 1:
                informationFragment = InformationFragment.newInstance(name);
                getSupportFragmentManager().beginTransaction().replace(R.id.container, informationFragment).commit();
                break;
            case 2:
                CheckUpFragment checkUpFragment = CheckUpFragment.newInstance(id);
                getSupportFragmentManager().beginTransaction().replace(R.id.container, checkUpFragment).
                        addToBackStack(null).commit();
                break;
            case 3:
                newCheckUpFragment = NewCheckUpFragment.newInstance(id);
                currentFragment = newCheckUpFragment;
                getSupportFragmentManager().beginTransaction().replace(R.id.container, newCheckUpFragment)
                        .addToBackStack(null).commit();
                binding.btnAddCheckup.setClickable(false);
                break;
        }
    }

    private void setUpToolbar() {
        setSupportActionBar(binding.toolbar);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            name = bundle.getString(NAME_FIELD);
            id = bundle.getString(ID_FIELD);
            getSupportActionBar().setTitle(name);
        }
    }

    @Override
    public void onCheckUpFragmentInteraction(Uri uri) {

    }

    @Override
    public void onInfoFragmentListener(Patient patient, String docRef) {
        newPatientFragment.setPatientData(patient, docRef);
        getSupportFragmentManager().
                beginTransaction().
                replace(R.id.container, newPatientFragment)
                .addToBackStack(null)
                .commit();

    }

    @Override
    public void onBackPressed() {
        if (currentFragment instanceof NewCheckUpFragment) {
            ((NewCheckUpFragment) currentFragment).respondToBackPress();
            binding.btnAddCheckup.setClickable(true);
        } else
            super.onBackPressed();

    }

    @Override
    public void onFragmentClosed() {
        binding.btnAddCheckup.setClickable(true);
    }
}
