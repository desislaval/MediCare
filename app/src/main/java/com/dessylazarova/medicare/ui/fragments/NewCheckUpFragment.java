package com.dessylazarova.medicare.ui.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.dessylazarova.medicare.AuthUtils;
import com.dessylazarova.medicare.R;
import com.dessylazarova.medicare.data.CheckUp;
import com.dessylazarova.medicare.data.Patient;
import com.dessylazarova.medicare.data.User;
import com.dessylazarova.medicare.databinding.FragmentNewCheckUpBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewCheckUpFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class NewCheckUpFragment extends Fragment {
    private OnFragmentInteractionListener listener;
    private static final String USERS_PATH = "users";
    private static final String PATIENTS_PATH = "patients";
    private static final String ID_FIELD = "id";
    private FragmentNewCheckUpBinding binding;

    public static NewCheckUpFragment newInstance(String id) {
        NewCheckUpFragment fragment = new NewCheckUpFragment();
        Bundle args = new Bundle();
        args.putString(ID_FIELD, id);
        fragment.setArguments(args);
        return fragment;
    }

    public NewCheckUpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_check_up, container, false);
        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    public void saveCheckUp() {
        String symptoms = binding.edtSymptoms.getText().toString();
        String treatment = binding.edtTreatment.getText().toString();

        if (symptoms.trim().isEmpty() || treatment.trim().isEmpty()) {
            binding.edtTreatment.setError(getResources().getString(R.string.empty_field_error));
            binding.edtSymptoms.setError(getResources().getString(R.string.empty_field_error));
            return;
        }

        Bundle args = getArguments();
        String id = args.getString("id");
        User user = AuthUtils.getInstance().getLoggedUser();
        CollectionReference reference = FirebaseFirestore.getInstance()
                .collection(USERS_PATH).document(user.getId()).collection(PATIENTS_PATH).document(id).collection("check-ups");
        reference.add(new CheckUp(treatment, symptoms));
        getActivity().getSupportFragmentManager().popBackStack();
        if (listener != null)
            listener.onFragmentClosed();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.save_patient_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveCheckUp();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void respondToBackPress() {
        if (!binding.edtSymptoms.getText().toString().isEmpty() || !binding.edtTreatment.getText().toString().isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(getActivity().getResources().getString(R.string.exit_unsaved_changes))
                    .setCancelable(false)
                    .setPositiveButton(getString(android.R.string.yes), (dialog, id) -> {
                        getFragmentManager().popBackStack();
                    })
                    .setNegativeButton(getString(android.R.string.no), (dialog, id) -> dialog.cancel());
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentClosed();
    }
}
