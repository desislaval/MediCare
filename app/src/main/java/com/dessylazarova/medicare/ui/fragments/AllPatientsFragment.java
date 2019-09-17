package com.dessylazarova.medicare.ui.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.dessylazarova.medicare.AuthUtils;
import com.dessylazarova.medicare.R;
import com.dessylazarova.medicare.adapters.PatientsAdapter;
import com.dessylazarova.medicare.data.Patient;
import com.dessylazarova.medicare.data.User;
import com.dessylazarova.medicare.databinding.FragmentAllPatientsBinding;
import com.dessylazarova.medicare.ui.activities.DetailsActivity;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AllPatientsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class AllPatientsFragment extends Fragment {
    private static final String NAME_FIELD = "name";
    private static final String ID_FIELD = "id";
    private static final String USERS_PATH = "users";
    private static final String PATIENTS_PATH = "patients";
    private CollectionReference patientsRef;
    private PatientsAdapter adapter;
    private FirebaseFirestore db;
    private FragmentAllPatientsBinding binding;

    public AllPatientsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_all_patients, container, false);
        User user = AuthUtils.getInstance().getLoggedUser();
        db = FirebaseFirestore.getInstance();
        patientsRef = db.collection(USERS_PATH).document(user.getId()).collection(PATIENTS_PATH);
        setUpRecView();
        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    private void setUpRecView() {
        Query query = patientsRef.orderBy(NAME_FIELD, Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Patient> options = new FirestoreRecyclerOptions.Builder<Patient>()
                .setQuery(query, Patient.class)
                .build();
        adapter = new PatientsAdapter(options, documentSnapshot -> {
            Patient patient = documentSnapshot.toObject(Patient.class);
            String myId = documentSnapshot.getId();
            Intent intent = new Intent(getActivity(), DetailsActivity.class);
            intent.putExtra(NAME_FIELD, patient.getName());
            intent.putExtra(ID_FIELD, myId);
            startActivity(intent);
        });
        binding.recView.setHasFixedSize(true);
        binding.recView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recView.setAdapter(adapter);
        searchDatabase();
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.logout_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                AuthUtils.getInstance().logout();
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void searchDatabase() {
        binding.search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Query egnQuery = patientsRef.orderBy("egn").startAt(newText).endAt(newText + "\uf8ff");
                FirestoreRecyclerOptions<Patient> options = new FirestoreRecyclerOptions.Builder<Patient>()
                        .setQuery(egnQuery, Patient.class)
                        .build();
                adapter = new PatientsAdapter(options, documentSnapshot -> {
                    Patient patient = documentSnapshot.toObject(Patient.class);
                    String myId = documentSnapshot.getId();
                    Intent intent = new Intent(getActivity(), DetailsActivity.class);
                    intent.putExtra(NAME_FIELD, patient.getName());
                    intent.putExtra(ID_FIELD, myId);
                    startActivity(intent);
                });
                adapter.startListening();
                binding.recView.swapAdapter(adapter, true);
                return true;
            }
        });
        binding.search.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Query query = patientsRef.orderBy(NAME_FIELD, Query.Direction.ASCENDING);
                FirestoreRecyclerOptions<Patient> options = new FirestoreRecyclerOptions.Builder<Patient>()
                        .setQuery(query, Patient.class)
                        .build();
                adapter = new PatientsAdapter(options, documentSnapshot -> {
                    Patient patient = documentSnapshot.toObject(Patient.class);
                    String myId = documentSnapshot.getId();
                    Intent intent = new Intent(getActivity(), DetailsActivity.class);
                    intent.putExtra(NAME_FIELD, patient.getName());
                    intent.putExtra(ID_FIELD, myId);
                    startActivity(intent);
                });
                adapter.startListening();
                binding.recView.swapAdapter(adapter, true);
                return false;
            }
        });
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
