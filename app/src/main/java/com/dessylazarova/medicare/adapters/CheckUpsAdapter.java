package com.dessylazarova.medicare.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.dessylazarova.medicare.R;
import com.dessylazarova.medicare.data.CheckUp;
import com.dessylazarova.medicare.databinding.CheckUpItemBinding;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;

public class CheckUpsAdapter extends FirestoreRecyclerAdapter<CheckUp, CheckUpVH> {
    private CheckUpVH.CheckUpListener listener;

    public CheckUpsAdapter(@NonNull FirestoreRecyclerOptions<CheckUp> options, CheckUpVH.CheckUpListener listener) {
        super(options);
        this.listener = listener;
    }

    @Override
    protected void onBindViewHolder(@NonNull CheckUpVH checkUpVH, int i, @NonNull CheckUp checkUp) {
        checkUpVH.binding.txtDate.setText(new SimpleDateFormat("dd.MM.yyyy").format(checkUp.getDate().toDate()));
        checkUpVH.binding.txtSymptoms.setText(checkUp.getSymptoms());
        checkUpVH.binding.txtTreatment.setText(checkUp.getTreatment());
        DocumentSnapshot snapshot = getSnapshots().getSnapshot(i);
        checkUpVH.setDocumentSnapshot(snapshot);
    }

    @NonNull
    @Override
    public CheckUpVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CheckUpItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.check_up_item, parent, false);
        return new CheckUpVH(binding, listener);
    }
}
