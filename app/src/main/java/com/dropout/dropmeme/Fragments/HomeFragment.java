package com.dropout.dropmeme.Fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dropout.dropmeme.Adapters.HomeContentAdapter;
import com.dropout.dropmeme.CreateMemeActivity;
import com.dropout.dropmeme.Models.HomeMeme;
import com.dropout.dropmeme.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private RecyclerView allDealersList;
    private LinearLayoutManager layoutManager;

    private FirebaseFirestore db;
    private HomeContentAdapter adapter;
    private ProgressDialog dialog;
    private CardView createCard;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        allDealersList = view.findViewById(R.id.home_content_list);
        layoutManager = new LinearLayoutManager(getActivity());
        allDealersList.setLayoutManager(layoutManager);
        allDealersList.setItemAnimator(new DefaultItemAnimator());

        createCard = view.findViewById(R.id.create_meme_card);

        db = FirebaseFirestore.getInstance();

        Query query = db.collection("HomeMeme");
        final FirestoreRecyclerOptions<HomeMeme> options = new FirestoreRecyclerOptions.Builder<HomeMeme>()
                .setQuery(query, HomeMeme.class)
                .build();

        adapter = new HomeContentAdapter(getContext(), options);
        allDealersList.setAdapter(adapter);

        createCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createIntent = new Intent(getActivity(), CreateMemeActivity.class);
                createIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                createIntent.putExtra("path", "default");
                startActivity(createIntent);
            }
        });

        return view;
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
}
