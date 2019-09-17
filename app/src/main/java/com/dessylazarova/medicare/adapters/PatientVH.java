package com.dessylazarova.medicare.adapters;


import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dessylazarova.medicare.databinding.PatientItemBinding;
import com.google.firebase.firestore.DocumentSnapshot;

public class PatientVH extends RecyclerView.ViewHolder {
    protected PatientItemBinding binding;
    private DocumentSnapshot snapshot;
    public PatientVH(PatientItemBinding binding, OnItemClickListener listener) {
        super(binding.getRoot());
        this.binding = binding;

        itemView.setOnClickListener(v -> {
            listener.onItemClicked(snapshot);
        });
    }

    public void setDocumentSnapshot(DocumentSnapshot snapshot) {
        this.snapshot = snapshot;
    }

    public interface OnItemClickListener {
        void onItemClicked(DocumentSnapshot documentSnapshot);
    }
}
