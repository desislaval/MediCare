package com.dessylazarova.medicare.ui.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.dessylazarova.medicare.AuthUtils;
import com.dessylazarova.medicare.R;
import com.dessylazarova.medicare.data.Patient;
import com.dessylazarova.medicare.data.User;
import com.dessylazarova.medicare.databinding.FragmentInformationBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnInformationFragmentListener} interface
 * to handle interaction events.
 */
public class InformationFragment extends Fragment {
    private FragmentInformationBinding binding;
    private static final String NAME_ARG = "name";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String docRef;
    private CollectionReference patientsRef;
    private NewPatientFragment newPatientFragment;
    private OnInformationFragmentListener listener;
    private Patient patientData;

    public InformationFragment() {
        // Required empty public constructor
    }

    public static InformationFragment newInstance(String name) {
        InformationFragment fragment = new InformationFragment();
        Bundle args = new Bundle();
        args.putString(NAME_ARG, name);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_information, container, false);
        setHasOptionsMenu(true);

        queryDatabase();
        setClickListenersForTextViews();
        return binding.getRoot();
    }

    private void queryDatabase() {
        User user = AuthUtils.getInstance().getLoggedUser();
        String userId = user.getId();
        Bundle args = getArguments();
        String name = args.getString(NAME_ARG);

        patientsRef = db.collection("users/" + userId + "/patients/");

        patientsRef.whereEqualTo(NAME_ARG, name).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot querySnapshot : queryDocumentSnapshots) {
                docRef = querySnapshot.getId();
                Patient patient = querySnapshot.toObject(Patient.class);
                binding.txtName.setText(patient.getName());
                binding.txtEgn.setText(patient.getEGN());
                binding.txtAddress.setText(patient.getAddress());
                binding.txtPhoneNumber.setText(patient.getPhoneNumber());
                binding.txtEmail.setText(patient.getEmail());
                binding.txtChronicleDiseases.setText(patient.getChronicDiseases());
                binding.txtAllergies.setText(patient.getAllergies());
                patientData = patient;
            }
        });

    }

    private void setClickListenersForTextViews() {
        binding.txtPhoneNumber.setOnClickListener(v -> {
            String phone = binding.txtPhoneNumber.getText().toString();
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
            startActivity(intent);
        });

        binding.txtAddress.setOnClickListener(v -> {
            String address = binding.txtAddress.getText().toString();
            String map = "http://maps.google.co.in/maps?q=" + address;
            Uri gmmIntentUri = Uri.parse(map);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        });

        binding.txtEmail.setOnClickListener(v -> {
            String email = binding.txtEmail.getText().toString();
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:" + email));
            startActivity(emailIntent);
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.patient_actions_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                deletePatient();
                return true;
            case R.id.action_edit:
                editData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void editData() {
        listener.onInfoFragmentListener(patientData, docRef);
    }

    private void deletePatient() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Are you sure you want to delete this patient?")
                .setCancelable(false)
                .setPositiveButton(getString(android.R.string.yes), (dialog, id) -> {
                    patientsRef.document(docRef).delete();
                    getActivity().finish();
                })
                .setNegativeButton(getString(android.R.string.no), (dialog, id) -> dialog.cancel());
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnInformationFragmentListener) {
            listener = (OnInformationFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnInformationFrListener");
        }
    }


    public interface OnInformationFragmentListener {
        void onInfoFragmentListener(Patient patient, String docRef);
    }
}
