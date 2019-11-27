package com.moon.ailatrieuphu.adminpage.user;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.moon.ailatrieuphu.Program;
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
    private boolean isLoaded = false;
    private int mPosition = 0;

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
            getAllUser();
        } else {
            processSearch(keyWord);
        }
        rvUser.scrollToPosition(mPosition);
    }

    private void processSearch(String keyWord) {
    }

    private void getAllUser() {
        ProgressDialogF.showLoading(getContext());
        apiService.getAllUser().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                ProgressDialogF.hideLoading();
                if (response.code() == 200) {
                    userList = response.body();
                    //userList.sort(Comparator.comparing(CauHoi::getIdCauHoi).reversed());
                    tvEmpty.setVisibility(View.GONE);
                    rvUser.setVisibility(View.VISIBLE);
                } else {
                    tvEmpty.setVisibility(View.VISIBLE);
                    rvUser.setVisibility(View.GONE);
                }
                if (!isLoaded) {
                    userAdapter = new UserAdapter(userList, getContext());
                    rvUser.setLayoutManager(new LinearLayoutManager(getContext()));
                    rvUser.setAdapter(userAdapter);
                    isLoaded = true;
                    setEvent();
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
                mPosition = position;
                registerForContextMenu(v);
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo
            menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (userList.get(mPosition).isAdminRole()) {
            menu.add(0, v.getId(), 0, "Khôi phục mật khẩu");
        } else {
            menu.add(0, v.getId(), 0, "Cấp quyền Admin");
            menu.add(0, v.getId(), 0, "Khôi phục mật khẩu");
            menu.add(0, v.getId(), 0, "Xóa");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int idUser = userList.get(mPosition).getIdUser();
        switch (item.getTitle().toString()) {
            case "Cấp quyền Admin":
                updateAdminRole(idUser);
                break;

            case "Khôi phục mật khẩu":
                Toast.makeText(getContext(), "Khôi phục mật khẩu", Toast.LENGTH_SHORT).show();
                break;

            case "Xóa":
                deleteMember(idUser);
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void deleteMember(int idUser) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Xóa !!!");
        alertDialog.setIcon(R.mipmap.ic_launcher);
        alertDialog.setMessage("Người dùng có Nickname: " + userList.get(mPosition).getNickname()
                + " sẽ bị xóa. \nTất cả thông tin và điểm số sẽ bị mất."
                + "\nBạn có chắc chắn không?");
        alertDialog.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ProgressDialogF.showLoading(getContext());
                apiService.deleteUser(idUser).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        ProgressDialogF.hideLoading();
                        if (response.body().equals("success")) {
                            reloadData();
                            Toast.makeText(getContext(), "Xóa thành công!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), R.string.err_internal_server, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        ProgressDialogF.hideLoading();
                        Toast.makeText(getContext(), R.string.err_connect, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        alertDialog.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void updateAdminRole(int idUser) {
        ProgressDialogF.showLoading(getContext());
        apiService.updateAdminRole(idUser, Program.getDateTimeNow()).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                ProgressDialogF.hideLoading();
                if (response.body().equals("success")) {
                    Toast.makeText(getContext(), "Thành công!", Toast.LENGTH_SHORT).show();
                    reloadData();
                } else {
                    Toast.makeText(getContext(), R.string.err_internal_server, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                ProgressDialogF.hideLoading();
                Toast.makeText(getContext(), R.string.err_connect, Toast.LENGTH_SHORT).show();
            }
        });
    }
}