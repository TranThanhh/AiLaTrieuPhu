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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.moon.ailatrieuphu.Program;
import com.moon.ailatrieuphu.adminpage.AdminMainActivity;
import com.moon.ailatrieuphu.email.EncryptPass;
import com.moon.ailatrieuphu.utility.LoadingDialog;
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

    private List<User> modList;
    private boolean isLoaded = false;
    private FloatingActionButton fabtnOnTop;

    private APIService apiService;
    private UserAdapter userAdapter;
    private FragmentManager fragmentManager;
    private AdminMainActivity adminMainActivity;

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
        fabtnOnTop = view.findViewById(R.id.floatingactionbutton);

        fragmentManager = getFragmentManager();
        apiService = APIConnect.getServer();
        modList = new ArrayList<>();
        adminMainActivity = (AdminMainActivity) getActivity();

        reloadData();
    }

    public void reloadData() {
        getAllModerator();
    }

    private void getAllModerator() {
        LoadingDialog.show(getContext());
        apiService.getAllModeratorActive().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                LoadingDialog.hide();
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
                rvMod.scrollToPosition(Program.positionModerator);
                adminMainActivity.bottomNav.getMenu().findItem(R.id.navigationModerator).setTitle("Điều hành viên (" + userAdapter.getItemCount() + ")");
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                LoadingDialog.hide();
                Toast.makeText(getContext(), R.string.err_connect, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setEvent() {
        userAdapter.setOnItemClick(new UserAdapter.ClickListener() {
            @Override
            public void onItemCLick(int position, View v) {
                Program.positionModerator = position;
                registerForContextMenu(v);
            }
        });
        fabtnOnTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rvMod.smoothScrollToPosition(0);
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
        int idUser = modList.get(Program.positionModerator).getIdUser();
        switch (item.getTitle().toString()) {
            case "Gỡ quyền điều hành":
                downgradeToUser(idUser);
                break;

            case "Khôi phục mật khẩu":
                showRestorePasswordAlertDialog(modList.get(Program.positionModerator));
                break;

            case "Xóa":
                deleteUser(idUser);
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void showRestorePasswordAlertDialog(User userRestorePassword) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setCancelable(false);
        dialog.setTitle("Thay đổi mật khẩu!");
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setMessage("Một mật khẩu mới cho tài khoản này sẽ được gửi tới email của họ: " + userRestorePassword.getEmail() +
                "\nBạn có chắc chắn không?");
        dialog.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                resetUserPassword(userRestorePassword);
            }
        });
        dialog.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void resetUserPassword(User userRestorePassword) {
        //String newPass = Program.getRandom8NumberString();
        String newPass="12345678";
        String messageMail = "Mật khẩu của bạn đã được thay đổi." + "" +
                "\nMật khẩu mới của bạn là: " + newPass +
                "\nHãy đăng nhập và thay đổi mật khẩu của bạn!";
        String titleMail = "[Thay đổi mật khẩu] Game Ai Là Triệu Phú";

        userRestorePassword.setPassword(EncryptPass.bcrypt(newPass));
        userRestorePassword.setUpdateTime(Program.getDateTimeNow());
        LoadingDialog.show(getContext());
        apiService.updatePassword(userRestorePassword).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                LoadingDialog.hide();
                if (response.body().equals("success")) {
                    //Program.sendMail(titleMail, messageMail, "", userRestorePassword.getEmail());
                    Toast.makeText(getContext(), "Thay đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                    reloadData();
                } else {
                    Toast.makeText(getContext(), R.string.err_internal_server, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                LoadingDialog.hide();
                Toast.makeText(getContext(), R.string.err_connect, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteUser(int idUser) {
        LoadingDialog.show(getContext());
        apiService.countCauHoiOfUserActive(idUser).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                LoadingDialog.hide();
                if (response.body() == 0) {
                    showDeleteAlertDialog(idUser);
                } else {
                    showDialogAlert(response.body());
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                LoadingDialog.hide();
                Toast.makeText(getContext(), R.string.err_connect, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDialogAlert(int count) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setCancelable(false);
        dialog.setTitle("Không thể xóa!");
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setMessage("Người điều hành có Nickname: " + modList.get(Program.positionModerator).getNickname() + "\nlà tác giả của " + count + " câu hỏi.\n" +
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
        alertDialog.setTitle("Xóa " + modList.get(Program.positionModerator).getEmail());
        alertDialog.setIcon(R.mipmap.ic_launcher);
        alertDialog.setMessage("Người điều hành có Nickname: " + modList.get(Program.positionModerator).getNickname()
                + " sẽ bị xóa. \nTất cả thông tin và điểm số sẽ bị mất."
                + "\nBạn có chắc chắn không?");
        alertDialog.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LoadingDialog.show(getContext());
                apiService.deleteUser(idUser).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        LoadingDialog.hide();
                        if (response.body().equals("success")) {
                            reloadData();
                            Toast.makeText(getContext(), "Xóa thành công!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), R.string.err_internal_server, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        LoadingDialog.hide();
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
        LoadingDialog.show(getContext());
        apiService.updateRoleLevel(idUser, 0, Program.getDateTimeNow()).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                LoadingDialog.hide();
                if (response.body().equals("success")) {
                    Toast.makeText(getContext(), "Thành công!", Toast.LENGTH_SHORT).show();
                    reloadData();
                } else {
                    Toast.makeText(getContext(), R.string.err_internal_server, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                LoadingDialog.hide();
                Toast.makeText(getContext(), R.string.err_connect, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
