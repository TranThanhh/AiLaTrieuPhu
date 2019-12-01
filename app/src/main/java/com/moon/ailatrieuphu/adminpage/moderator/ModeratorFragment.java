package com.moon.ailatrieuphu.adminpage.moderator;


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
import com.moon.ailatrieuphu.adminpage.UserAdapter;
import com.moon.ailatrieuphu.api.APIConnect;
import com.moon.ailatrieuphu.api.APIService;
import com.moon.ailatrieuphu.model.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ModeratorFragment extends Fragment {

    private RecyclerView rvMod;
    private TextView tvEmpty;

    private String keyWord = "";
    private List<User> modList;
    private boolean isLoaded = false;
    private int mPosition = 0;

    private APIService apiService;
    private UserAdapter userAdapter;
    private FragmentManager fragmentManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_moderator, container, false);

        setControl(view);

        return view;
    }

    private void setControl(View view) {
        rvMod = view.findViewById(R.id.recyclerviewModerator);
        tvEmpty = view.findViewById(R.id.textviewEmpty);

        fragmentManager = getFragmentManager();
        apiService = APIConnect.getServer();
        modList = new ArrayList<>();

        reloadData();
    }

    private void reloadData() {
        if (keyWord.equals("")) {
            getAllModerator();
        } else {
            processSearch(keyWord);
        }
        rvMod.scrollToPosition(mPosition);
    }

    private void processSearch(String keyWord) {
    }

    private void getAllModerator() {
        ProgressDialogF.showLoading(getContext());
        apiService.getAllModerator().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                ProgressDialogF.hideLoading();
                if (response.code() == 200) {
                    modList = response.body();
                    //modList.sort(Comparator.comparing(CauHoi::getIdCauHoi).reversed());
                    tvEmpty.setVisibility(View.GONE);
                    rvMod.setVisibility(View.VISIBLE);
                } else {
                    tvEmpty.setVisibility(View.VISIBLE);
                    rvMod.setVisibility(View.GONE);
                }
                if (!isLoaded) {
                    userAdapter = new UserAdapter(modList, getContext());
                    rvMod.setLayoutManager(new LinearLayoutManager(getContext()));
                    rvMod.setAdapter(userAdapter);
                    isLoaded = true;
                    setEvent();
                } else {
                    userAdapter.refresh(modList);
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
        menu.add(0, v.getId(), 0, "Gỡ quyền điều hành");
        menu.add(0, v.getId(), 0, "Khôi phục mật khẩu");
        menu.add(0, v.getId(), 0, "Xóa");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int idUser = modList.get(mPosition).getIdUser();
        switch (item.getTitle().toString()) {
            case "Gỡ quyền điều hành":
                downgradeToUser(idUser);
                break;

            case "Khôi phục mật khẩu":
                Toast.makeText(getContext(), "Khôi phục mật khẩu", Toast.LENGTH_SHORT).show();
                break;

            case "Xóa":
                deleteUser(idUser);
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void deleteUser(int idUser) {
        ProgressDialogF.showLoading(getContext());
        apiService.countCauHoiOfUser(idUser).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                ProgressDialogF.hideLoading();
                if (response.body() == 0) {
                    showDeleteAlertDialog(idUser);
                } else {
                    showDialogAlert(response.body());
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                ProgressDialogF.hideLoading();
                Toast.makeText(getContext(), R.string.err_connect, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDialogAlert(int count) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setCancelable(false);
        dialog.setTitle("Không thể xóa!");
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setMessage("Người điều hành có Nickname: " + modList.get(mPosition).getNickname() + "\nlà tác giả của " + count + " câu hỏi.\n" +
                "Bạn không thể xóa người này!");
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialog.show();
    }

    private void showDeleteAlertDialog(int idUser) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Xóa " + modList.get(mPosition).getEmail());
        alertDialog.setIcon(R.mipmap.ic_launcher);
        alertDialog.setMessage("Người điều hành có Nickname: " + modList.get(mPosition).getNickname()
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

    private void downgradeToUser(int idUser) {
        ProgressDialogF.showLoading(getContext());
        apiService.updateRoleLevel(idUser, 0, Program.getDateTimeNow()).enqueue(new Callback<String>() {
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
