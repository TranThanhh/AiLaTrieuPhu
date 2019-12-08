package com.moon.ailatrieuphu.adminpage.cauhoi;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.moon.ailatrieuphu.Program;
import com.moon.ailatrieuphu.utility.ProgressDialogF;
import com.moon.ailatrieuphu.R;
import com.moon.ailatrieuphu.api.APIConnect;
import com.moon.ailatrieuphu.api.APIService;
import com.moon.ailatrieuphu.model.CauHoi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CauHoiFragment extends Fragment {

    private RecyclerView rvCauHoi;
    private TextView tvEmpty;

    private String keyWord="";
    private List<CauHoi> cauHoiList;
    private boolean isLoaded=true;

    private APIService apiService;
    private CauHoiAdapter cauHoiAdapter;
    public FragmentManager fragmentManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_cau_hoi, container, false);

        setControl(view);

        return view;
    }

    private void setControl(View view) {
        rvCauHoi=view.findViewById(R.id.recyclerviewCauHoi);
        tvEmpty=view.findViewById(R.id.textviewEmpty);

        fragmentManager=getFragmentManager();
        apiService= APIConnect.getServer();
        cauHoiList=new ArrayList<>();

        reloadData();
    }

    public void reloadData() {
        if (keyWord.equals("")) {
            getListCauHoi();
        } else {
            processSearch(keyWord);
        }
    }

    private void processSearch(String keyWord) {
    }

    private void getListCauHoi() {
        ProgressDialogF.showLoading(getContext());
        apiService.getAllCauHoiActive().enqueue(new Callback<List<CauHoi>>() {
            @Override
            public void onResponse(Call<List<CauHoi>> call, Response<List<CauHoi>> response) {
                ProgressDialogF.hideLoading();
                if (response.code() == 204) {
                    tvEmpty.setVisibility(View.VISIBLE);
                    rvCauHoi.setVisibility(View.GONE);
                }
                if (response.code() == 200) {
                    cauHoiList = response.body();
                    //cauHoiList.sort(Comparator.comparing(CauHoi::getIdCauHoi).reversed());
                    tvEmpty.setVisibility(View.GONE);
                    rvCauHoi.setVisibility(View.VISIBLE);
                }
                if (isLoaded) {
                    cauHoiAdapter = new CauHoiAdapter(cauHoiList, getContext());
                    rvCauHoi.setLayoutManager(new LinearLayoutManager(getContext()));
                    rvCauHoi.setAdapter(cauHoiAdapter);
                    setEvent();
                    isLoaded = false;
                } else {
                    cauHoiAdapter.refresh(cauHoiList);
                }
                rvCauHoi.scrollToPosition(Program.positionCauHoi);
            }

            @Override
            public void onFailure(Call<List<CauHoi>> call, Throwable t) {
                ProgressDialogF.hideLoading();
                Toast.makeText(getContext(), "Có lỗi xảy ra", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setEvent() {
        cauHoiAdapter.setOnItemClick(new CauHoiAdapter.ClickListener() {
            @Override
            public void onItemCLick(int position, View v) {
                //Toast.makeText(getContext(), cauHoiList.get(position).getIdCauHoi()+"", Toast.LENGTH_SHORT).show();
                Program.positionCauHoi=position;
                ViewCauHoiFragment viewCauHoiFragment = new ViewCauHoiFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("#cauhoi", cauHoiList.get(position));
                viewCauHoiFragment.setArguments(bundle);
                fragmentManager.beginTransaction().add(R.id.fullscreenFragmentContainerAdmin, viewCauHoiFragment).addToBackStack(null).commit();
            }
        });
    }
}
