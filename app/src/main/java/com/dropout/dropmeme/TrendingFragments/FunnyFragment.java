package com.dropout.dropmeme.TrendingFragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dropout.dropmeme.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FunnyFragment extends Fragment {


    public FunnyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_funny, container, false);
    }

}
