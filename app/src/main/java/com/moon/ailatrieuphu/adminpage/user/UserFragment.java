package com.moon.ailatrieuphu.adminpage.user;


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

import com.moon.ailatrieuphu.ProgressDialogF;
import com.moon.ailatrieuphu.R;
import com.moon.ailatrieuphu.api.APIConnect;
import com.moon.ailatrieuphu.api.APIService;
import com.moon.ailatrieuphu.model.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UserFragment extends Fragment {

    private RecyclerView rvUser;
    private TextView tvEmpty;

    private String keyWord = "";
    private List<User> userList;
    private boolean isLoaded = true;

    private APIService apiService;
    private UserAdapter userAdapter;
    private FragmentManager fragmentManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        setControl(view);

        return view;
    }

    private void setControl(View view) {
        rvUser = view.findViewById(R.id.recyclerviewUser);
        tvEmpty = view.findViewById(R.id.textviewEmpty);

        fragmentManager = getFragmentManager();
        apiService = APIConnect.getServer();
        userList = new ArrayList<>();

        reloadData();
    }

    private void reloadData() {
        if (keyWord.equals("")) {
            getListUser();
        } else {
            processSearch(keyWord);
        }
    }

    private void processSearch(String keyWord) {
    }

    private void getListUser() {
        ProgressDialogF.showLoading(getContext());
        apiService.getAllUser().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                ProgressDialogF.hideLoading();
                if (response.code() == 204) {
                    tvEmpty.setVisibility(View.VISIBLE);
                    rvUser.setVisibility(View.GONE);
                }
                if (response.code() == 200) {
                    userList = response.body();
                    //userList.sort(Comparator.comparing(CauHoi::getIdCauHoi).reversed());
                    tvEmpty.setVisibility(View.GONE);
                    rvUser.setVisibility(View.VISIBLE);
                }
                if (isLoaded) {
                    userAdapter = new UserAdapter(userList, getContext());
                    rvUser.setLayoutManager(new LinearLayoutManager(getContext()));
                    rvUser.setAdapter(userAdapter);
                    setEvent();
                    isLoaded = false;
                } else {
                    userAdapter.refresh(userList);
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                ProgressDialogF.hideLoading();
                Toast.makeText(getContext(), R.string.err_connect, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setEvent() {
        userAdapter.setOnItemClick(new UserAdapter.ClickListener() {
            @Override
            public void onItemCLick(int position, View v) {
                Toast.makeText(getContext(), userList.get(position).getIdUser()+"", Toast.LENGTH_SHORT).show();
            }
        });
    }
}