package com.dessylazarova.medicare.ui.fragments;

import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ScrollView;

import com.dessylazarova.medicare.AuthUtils;
import com.dessylazarova.medicare.R;
import com.dessylazarova.medicare.data.Patient;
import com.dessylazarova.medicare.data.User;
import com.dessylazarova.medicare.databinding.FragmentNewPatientBinding;
import com.dessylazarova.medicare.ui.activities.DetailsActivity;
import com.dessylazarova.medicare.ui.activities.MainActivity;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewPatientFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class NewPatientFragment extends Fragment {
    private OnFragmentInteractionListener listener;
    private static final String NAME_FIELD = "name";
    private static final String EGN_FIELD = "egn";
    private static final String USERS_PATH = "users";
    private static final String PATIENTS_PATH = "patients";
    private FragmentNewPatientBinding binding;
    private boolean patientHasChanged = false;
    private String fullName;
    private String egn;
    private String address;
    private String chronicDiseases;
    private String phoneNumber;
    private String email;
    private Patient patient;
    private String docRef;
    private String allergies;
    private boolean hasPersonChanged = false;

    public NewPatientFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_patient, container, false);
        setHasOptionsMenu(true);

        if (getActivity() instanceof DetailsActivity) {
            ScrollView.LayoutParams params = (ScrollView.LayoutParams) binding.scrollView.getLayoutParams();
            params.setMargins(0, 0, 0, 128);
            binding.scrollView.setLayoutParams(params);
        }


        Bundle bundle = getArguments();
        if (bundle != null) {
            binding.edtFullName.setText(bundle.getString(NAME_FIELD));
            binding.edtEgn.setText(bundle.getString(EGN_FIELD));
        }
        if (patient != null) {
            hasPersonChanged = true;
            binding.edtFullName.setText(patient.getName());
            binding.edtEgn.setText(patient.getEGN());
            binding.edtAddress.setText(patient.getAddress());
            binding.edtPhoneNumber.setText(patient.getPhoneNumber());
            binding.edtEmail.setText(patient.getEmail());
            binding.edtChronicDiseases.setText(patient.getChronicDiseases());
            binding.edtAllergies.setText(patient.getAllergies());
        }
        return binding.getRoot();
    }

    public void savePatient() {
        fullName = binding.edtFullName.getText().toString();
        egn = binding.edtEgn.getText().toString();
        address = binding.edtAddress.getText().toString();
        chronicDiseases = binding.edtChronicDiseases.getText().toString();
        phoneNumber = binding.edtPhoneNumber.getText().toString();
        email = binding.edtEmail.getText().toString();
        allergies = binding.edtAllergies.getText().toString();
        User user = AuthUtils.getInstance().getLoggedUser();
        CollectionReference reference = FirebaseFirestore.getInstance()
                .collection(USERS_PATH).document(user.getId()).collection(PATIENTS_PATH);
        if (hasPersonChanged && isEdtTxtCorrect()) {
            DocumentReference documentReference = reference.document(docRef);
            documentReference.update("name", fullName,
                    "egn", egn,
                    "address", address,
                    "phoneNumber", phoneNumber,
                    "email", email,
                    "chronicDiseases", chronicDiseases,
                    "allergies", allergies);
            getActivity().getSupportFragmentManager().popBackStack();
        } else if (isEdtTxtCorrect()) {
            reference.add(new Patient(fullName, egn, address, phoneNumber, email, chronicDiseases, allergies));
            if (listener != null) {
                listener.onFragmentClosed();
            }
            getActivity().getSupportFragmentManager().popBackStack();
        }

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
                savePatient();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void respondToBackPress() {
        if (isEdtTxtFilled()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(getActivity().getResources().getString(R.string.exit_unsaved_changes))
                    .setCancelable(false)
                    .setPositiveButton(getString(android.R.string.yes), (dialog, id) -> {
                        if (listener != null) {
                            listener.onFragmentClosed();
                        }
                        getFragmentManager().popBackStack();
                    })
                    .setNegativeButton(getString(android.R.string.no), (dialog, id) -> dialog.cancel());
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            if (listener != null) {
                listener.onFragmentClosed();
            }
            getFragmentManager().popBackStack();
        }
    }

    public boolean isEdtTxtFilled() {
        int matches = 0;
        if (!binding.edtFullName.getText().toString().trim().isEmpty())
            matches++;
        if (!binding.edtEgn.getText().toString().trim().isEmpty())
            matches++;
        if (!binding.edtAddress.getText().toString().trim().isEmpty())
            matches++;
        if (!binding.edtEmail.getText().toString().trim().isEmpty())
            matches++;
        if (!binding.edtPhoneNumber.getText().toString().trim().isEmpty())
            matches++;
        if (!binding.edtAllergies.getText().toString().trim().isEmpty())
            matches++;
        if (!binding.edtChronicDiseases.getText().toString().trim().isEmpty())
            matches++;
        return matches > 0;
    }

    public boolean isEdtTxtCorrect() {
        int matches = 0;
        if (fullName.isEmpty()) {
            binding.edtFullName.setError(getResources().getString(R.string.empty_field_error));
            matches++;
        }
        if (egn.trim().isEmpty()) {
            binding.edtEgn.setError(getResources().getString(R.string.empty_field_error));
            matches++;
        }
        if (address.trim().isEmpty()) {
            binding.edtAddress.setError(getResources().getString(R.string.empty_field_error));
            matches++;
        }
        if (email.isEmpty()) {
            binding.edtEmail.setError(getResources().getString(R.string.empty_field_error));
            matches++;
        }
        if (phoneNumber.isEmpty()) {
            binding.edtPhoneNumber.setError(getResources().getString(R.string.empty_field_error));
            matches++;
        }

        String numbersReges = "^[0-9]*$";
        if (!(phoneNumber.matches(numbersReges))) {
            binding.edtPhoneNumber.setError(null);
            binding.edtPhoneNumber.setError(getResources().getString(R.string.phone_number_check));
            matches++;
        }
        if (!(android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())) {
            binding.edtEmail.setError(null);
            binding.edtEmail.setError(getResources().getString(R.string.email_check));
            matches++;
        }
        String egnRegex = "^[0-9]{10}$";
        if (!(egn.matches(egnRegex))) {
            binding.edtEgn.setError(null);
            binding.edtEgn.setError(getResources().getString(R.string.egn_check));
            matches++;
        }
        return matches <= 0;
    }

    public void setPatientData(Patient patientData, String docRef) {
        patient = patientData;
        this.docRef = docRef;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NewPatientFragment.OnFragmentInteractionListener) {
            listener = (NewPatientFragment.OnFragmentInteractionListener) context;
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
