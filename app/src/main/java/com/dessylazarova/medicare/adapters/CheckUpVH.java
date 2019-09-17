package com.dessylazarova.medicare.adapters;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.dessylazarova.medicare.databinding.CheckUpItemBinding;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

public class CheckUpVH extends RecyclerView.ViewHolder {
    CheckUpItemBinding binding;
DocumentSnapshot snapshot;
    public CheckUpVH(CheckUpItemBinding binding, final CheckUpListener listener) {
        super(binding.getRoot());
        this.binding = binding;

        binding.imgDelete.setOnClickListener(v -> {
            if (listener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener!=null) {
                    listener.onDeleteClick(snapshot);
                }
            }
        });
    }

    public void setDocumentSnapshot(DocumentSnapshot snapshot) {
        this.snapshot = snapshot;
    }

    public interface CheckUpListener {
        void onDeleteClick(DocumentSnapshot documentSnapshot);
    }
}
