package com.moon.ailatrieuphu.adminpage.cauhoi;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moon.ailatrieuphu.R;

public class ViewCauHoiFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_view_cau_hoi, container, false);
        
        setControl(view);
        
        return view;
    }

    private void setControl(View view) {
    }

}
