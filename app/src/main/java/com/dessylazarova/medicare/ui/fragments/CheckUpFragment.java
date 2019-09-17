package com.dessylazarova.medicare.ui.fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import com.dessylazarova.medicare.AuthUtils;
import com.dessylazarova.medicare.R;
import com.dessylazarova.medicare.adapters.CheckUpVH;
import com.dessylazarova.medicare.adapters.CheckUpsAdapter;
import com.dessylazarova.medicare.data.CheckUp;
import com.dessylazarova.medicare.data.User;
import com.dessylazarova.medicare.databinding.FragmentCheckUpBinding;
import com.dessylazarova.medicare.ui.activities.DetailsActivity;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InformationFragment.OnInformationFragmentListener} interface
 * to handle interaction events.
 */
public class CheckUpFragment extends Fragment {
    private static final String NAME_FIELD = "name";
    private static final String SYMPTOMS_FIELD = "date";
    private static final String ID_FIELD = "id";
    private static final String USERS_PATH = "users";
    private static final String PATIENTS_PATH = "patients";
    private static final String CHECK_UPS_PATH = "check-ups";
    private OnFragmentInteractionListener mListener;
    private FragmentCheckUpBinding binding;
    private CollectionReference checkUpRef;
    private CheckUpsAdapter adapter;
    private FirebaseFirestore db;

    public CheckUpFragment() {
        // Required empty public constructor
    }

    public static CheckUpFragment newInstance(String id) {
        CheckUpFragment fragment = new CheckUpFragment();
        Bundle args = new Bundle();
        args.putString(ID_FIELD, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_check_up, container, false);
        
        User user = AuthUtils.getInstance().getLoggedUser();
        Bundle args = getArguments();
        String id = args.getString(ID_FIELD);
        db = FirebaseFirestore.getInstance();
        checkUpRef = db.collection(USERS_PATH).document(user.getId()).
                collection(PATIENTS_PATH).document(id).collection(CHECK_UPS_PATH);
        setUpRecView();

        return binding.getRoot();
    }

    private void setUpRecView() {
        Query query = checkUpRef.orderBy("date", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<CheckUp> options = new FirestoreRecyclerOptions.Builder<CheckUp>()
                .setQuery(query, CheckUp.class)
                .build();
        adapter = new CheckUpsAdapter(options, (DocumentSnapshot snapshot) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("Are you sure you want to delete this patient?")
                    .setCancelable(false)
                    .setPositiveButton(getString(android.R.string.yes), (dialog, id) -> {
                        String docRef = snapshot.getId();
                        checkUpRef.document(docRef).delete();
                        getActivity().finish();
                    })
                    .setNegativeButton(getString(android.R.string.no), (dialog, id) -> dialog.cancel());
            AlertDialog alert = builder.create();
            alert.show();
        });
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);
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

    public interface OnFragmentInteractionListener {
        void onCheckUpFragmentInteraction(Uri uri);
    }
}
