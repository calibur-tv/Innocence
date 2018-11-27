package com.riuir.calibur.ui.home.Drama;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.riuir.calibur.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainScoreFragment extends Fragment {


    public MainScoreFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_score, container, false);
    }

}
