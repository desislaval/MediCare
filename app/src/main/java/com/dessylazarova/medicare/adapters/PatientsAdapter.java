package com.dessylazarova.medicare.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.dessylazarova.medicare.R;
import com.dessylazarova.medicare.data.Patient;
import com.dessylazarova.medicare.databinding.PatientItemBinding;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class PatientsAdapter extends FirestoreRecyclerAdapter<Patient, PatientVH> {
    private PatientVH.OnItemClickListener listener;

    public PatientsAdapter(@NonNull FirestoreRecyclerOptions<Patient> options, PatientVH.OnItemClickListener listener) {
        super(options);
        this.listener = listener;
    }

    @Override
    protected void onBindViewHolder(@NonNull PatientVH patientVH, int i, @NonNull Patient patient) {
        patientVH.binding.txtName.setText(patient.getName());
        patientVH.binding.txtEgn.setText(patient.getEGN());
        DocumentSnapshot snapshot = getSnapshots().getSnapshot(i);
        patientVH.setDocumentSnapshot(snapshot);
    }

    @NonNull
    @Override
    public PatientVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PatientItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.patient_item, parent, false);
        PatientVH viewHolder = new PatientVH(binding, listener);
        return viewHolder;
    }

}
