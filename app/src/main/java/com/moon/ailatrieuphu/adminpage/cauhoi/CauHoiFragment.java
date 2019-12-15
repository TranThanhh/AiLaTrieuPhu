package com.moon.ailatrieuphu.adminpage.cauhoi;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.moon.ailatrieuphu.Program;
import com.moon.ailatrieuphu.R;
import com.moon.ailatrieuphu.adminpage.AdminMainActivity;
import com.moon.ailatrieuphu.api.APIConnect;
import com.moon.ailatrieuphu.api.APIService;
import com.moon.ailatrieuphu.model.CauHoi;
import com.moon.ailatrieuphu.utility.ProgressDialogF;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CauHoiFragment extends Fragment {

    private RecyclerView rvCauHoi;
    private TextView tvEmpty;
    private EditText edtSearch;
    private ImageView imgvSearch;

    private List<CauHoi> cauHoiList;
    private boolean isLoaded = false;
    private FloatingActionButton fabtnOnTop;

    private APIService apiService;
    private CauHoiAdapter cauHoiAdapter;
    public FragmentManager fragmentManager;
    private AdminMainActivity adminMainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cau_hoi, container, false);

        setControl(view);

        //set event after loadData;

        return view;
    }

    private void setControl(View view) {
        rvCauHoi = view.findViewById(R.id.recyclerviewCauHoi);
        tvEmpty = view.findViewById(R.id.textviewEmpty);
        fabtnOnTop = view.findViewById(R.id.floatingactionbutton);
        edtSearch = view.findViewById(R.id.edittextSearch);
        imgvSearch = view.findViewById(R.id.imageviewSearch);

        fragmentManager = getFragmentManager();
        apiService = APIConnect.getServer();
        cauHoiList = new ArrayList<>();
        adminMainActivity=(AdminMainActivity) getActivity();

        reloadData();
    }

    public void reloadData() {
        edtSearch.setText(Program.keyWordCauHoi);
        if (Program.keyWordCauHoi.equals("")) {
            getAllCauHoiActive();
        } else {
            searchCauHoiActive(Program.keyWordCauHoi);
        }
    }

    private void searchCauHoiActive(String keyWord) {
        cauHoiList.clear();
        ProgressDialogF.showLoading(getContext());
        apiService.searchCauHoiActive(keyWord).enqueue(new Callback<List<CauHoi>>() {
            @Override
            public void onResponse(Call<List<CauHoi>> call, Response<List<CauHoi>> response) {
                ProgressDialogF.hideLoading();
                if (response.code() == 204) {
                    tvEmpty.setVisibility(View.VISIBLE);
                    rvCauHoi.setVisibility(View.GONE);
                }
                if (response.code() == 200) {
                    cauHoiList = response.body();
                    tvEmpty.setVisibility(View.GONE);
                    rvCauHoi.setVisibility(View.VISIBLE);
                }
                if (!isLoaded) {
                    cauHoiAdapter = new CauHoiAdapter(cauHoiList, getContext());
                    rvCauHoi.setLayoutManager(new LinearLayoutManager(getContext()));
                    rvCauHoi.setAdapter(cauHoiAdapter);
                    setEvent();
                    isLoaded = true;
                } else {
                    cauHoiAdapter.refresh(cauHoiList);
                }
                adminMainActivity.bottomNav.getMenu().findItem(R.id.navigationQuestion).setTitle("Câu hỏi ("+cauHoiAdapter.getItemCount()+")");
            }

            @Override
            public void onFailure(Call<List<CauHoi>> call, Throwable t) {
                Toast.makeText(getContext(), R.string.err_connect, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getAllCauHoiActive() {
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
                if (!isLoaded) {
                    cauHoiAdapter = new CauHoiAdapter(cauHoiList, getContext());
                    rvCauHoi.setLayoutManager(new LinearLayoutManager(getContext()));
                    rvCauHoi.setAdapter(cauHoiAdapter);
                    setEvent();
                    isLoaded = true;
                } else {
                    cauHoiAdapter.refresh(cauHoiList);
                }
                rvCauHoi.scrollToPosition(Program.positionCauHoi);
                adminMainActivity.bottomNav.getMenu().findItem(R.id.navigationQuestion).setTitle("Câu hỏi ("+cauHoiAdapter.getItemCount()+")");
            }

            @Override
            public void onFailure(Call<List<CauHoi>> call, Throwable t) {
                ProgressDialogF.hideLoading();
                Toast.makeText(getContext(), R.string.err_connect, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setEvent() {
        cauHoiAdapter.setOnItemClick(new CauHoiAdapter.ClickListener() {
            @Override
            public void onItemCLick(int position, View v) {
                //Toast.makeText(getContext(), cauHoiList.get(position).getIdCauHoi()+"", Toast.LENGTH_SHORT).show();
                Program.positionCauHoi = position;
                ViewCauHoiFragment viewCauHoiFragment = new ViewCauHoiFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("#cauhoi", cauHoiList.get(position));
                viewCauHoiFragment.setArguments(bundle);
                fragmentManager.beginTransaction().add(R.id.fullscreenFragmentContainerAdmin, viewCauHoiFragment).addToBackStack(null).commit();
            }
        });
        fabtnOnTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rvCauHoi.smoothScrollToPosition(0);
            }
        });
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //do nothing.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //do nothing.
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().trim().equals("")) {
                    getAllCauHoiActive();
                    Program.keyWordCauHoi="";
                }
            }
        });
        imgvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Program.keyWordCauHoi=edtSearch.getText().toString().trim();
                if(!Program.keyWordCauHoi.equals("")) searchCauHoiActive(Program.keyWordCauHoi);
            }
        });
    }
}
