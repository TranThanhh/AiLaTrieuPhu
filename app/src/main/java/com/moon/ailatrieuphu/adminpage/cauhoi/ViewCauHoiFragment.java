package com.moon.ailatrieuphu.adminpage.cauhoi;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.moon.ailatrieuphu.Program;
import com.moon.ailatrieuphu.utility.LoadingDialog;
import com.moon.ailatrieuphu.R;
import com.moon.ailatrieuphu.api.APIConnect;
import com.moon.ailatrieuphu.api.APIService;
import com.moon.ailatrieuphu.model.CauHoi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewCauHoiFragment extends Fragment {

    private TextView tvTitleCauHoi, tvNoiDung, tvCauA, tvCauB, tvCauC, tvCauD, tvAuthor;
    private ImageView imgvCauA, imgvCauB, imgvCauC, imgvCauD, imgvEdit, imgvBack;
    private RadioButton rbtnDe, rbtnVua, rbtnKho;
    private Button btnDelete;

    private CauHoi cauHoi;
    private FragmentManager fragmentManager;
    private CauHoiFragment cauHoiFragment;
    private APIService apiService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_view_cau_hoi, container, false);
        
        setControl(view);

        setEvent();

        return view;
    }

    private void setEvent() {
        imgvEdit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AddCauHoiFragment addCauHoiFragment = new AddCauHoiFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("#cauhoi", cauHoi);
                addCauHoiFragment.setArguments(bundle);
                fragmentManager.popBackStack();
                fragmentManager.beginTransaction().add(R.id.fullscreenFragmentContainerAdmin, addCauHoiFragment).addToBackStack(null).commit();
            }
        });
        imgvBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.popBackStack();
            }
        });
        btnDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteAlertDialog(cauHoi.getIdCauHoi());
            }
        });
    }

    private void showDeleteAlertDialog(int idCauHoi) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Xóa câu hỏi có ID: "+idCauHoi);
        alertDialog.setIcon(R.mipmap.ic_launcher);
        alertDialog.setMessage("Câu hỏi này sẽ bị xóa, bạn có chắc chắn không?");
        alertDialog.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LoadingDialog.show(getContext());
                apiService.deleteCauHoi(idCauHoi).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        LoadingDialog.hide();
                        if (response.body().equals("success")) {
                            Toast.makeText(getContext(), "Xóa thành công!", Toast.LENGTH_SHORT).show();
                            fragmentManager.popBackStack();
                            cauHoiFragment.reloadData();
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

    private void setControl(View view) {
        tvTitleCauHoi=view.findViewById(R.id.textviewTitleCauHoi);
        tvNoiDung=view.findViewById(R.id.textviewNoiDung);
        tvCauA=view.findViewById(R.id.textviewCauA);
        tvCauB=view.findViewById(R.id.textviewCauB);
        tvCauC=view.findViewById(R.id.textviewCauC);
        tvCauD=view.findViewById(R.id.textviewCauD);
        tvAuthor=view.findViewById(R.id.textviewAuthor);
        imgvCauA=view.findViewById(R.id.imageviewCauA);
        imgvCauB=view.findViewById(R.id.imageviewCauB);
        imgvCauC=view.findViewById(R.id.imageviewCauC);
        imgvCauD=view.findViewById(R.id.imageviewCauD);
        imgvEdit=view.findViewById(R.id.imageviewEdit);
        imgvBack=view.findViewById(R.id.imageviewBack);
        rbtnDe=view.findViewById(R.id.radiobuttonDe);
        rbtnVua=view.findViewById(R.id.radiobuttonVua);
        rbtnKho=view.findViewById(R.id.radiobuttonKho);

        btnDelete=view.findViewById(R.id.buttonDelete);

        fragmentManager=getFragmentManager();
        cauHoiFragment=(CauHoiFragment) fragmentManager.findFragmentByTag("CauHoiFrag");
        apiService= APIConnect.getServer();


        if (getArguments() != null) {
            cauHoi = (CauHoi) getArguments().getSerializable("#cauhoi");
            if (cauHoi != null) recieveData(cauHoi);
        } else{
            Toast.makeText(getContext(), "Có lỗi xảy ra!", Toast.LENGTH_SHORT).show();
            fragmentManager.popBackStack();
        }

        if(Program.user.getRoleLevel()==2){
            if(Program.user.getIdUser()==cauHoi.getIdUser()){
                imgvEdit.setVisibility(View.VISIBLE);
                btnDelete.setVisibility(View.VISIBLE);
                tvAuthor.setVisibility(View.GONE);
            } else {
                imgvEdit.setVisibility(View.INVISIBLE);
                btnDelete.setVisibility(View.INVISIBLE);
                tvAuthor.setVisibility(View.VISIBLE);
            }
        }

        if(tvAuthor.getVisibility()==View.VISIBLE){
            LoadingDialog.show(getContext());
            apiService.getNickname(cauHoi.getIdUser()).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    LoadingDialog.hide();
                    tvAuthor.setText("Tác giả: "+response.body());
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Toast.makeText(getContext(), R.string.err_connect, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void recieveData(CauHoi cauHoi) {
        tvTitleCauHoi.setText("ID: "+cauHoi.getIdCauHoi());
        tvNoiDung.setText(cauHoi.getNoiDung());
        tvCauA.setText(cauHoi.getCauA());
        tvCauB.setText(cauHoi.getCauB());
        tvCauC.setText(cauHoi.getCauC());
        tvCauD.setText(cauHoi.getCauD());

        switch (cauHoi.getDapAnDung()){
            case "A": imgvCauA.setVisibility(View.VISIBLE);
                imgvCauB.setVisibility(View.INVISIBLE);
                imgvCauC.setVisibility(View.INVISIBLE);
                imgvCauD.setVisibility(View.INVISIBLE);
                break;
            case "B": imgvCauA.setVisibility(View.INVISIBLE);
                imgvCauB.setVisibility(View.VISIBLE);
                imgvCauC.setVisibility(View.INVISIBLE);
                imgvCauD.setVisibility(View.INVISIBLE);
                break;
            case "C": imgvCauA.setVisibility(View.INVISIBLE);
                imgvCauB.setVisibility(View.INVISIBLE);
                imgvCauC.setVisibility(View.VISIBLE);
                imgvCauD.setVisibility(View.INVISIBLE);
                break;
            case "D": imgvCauA.setVisibility(View.INVISIBLE);
                imgvCauB.setVisibility(View.INVISIBLE);
                imgvCauC.setVisibility(View.INVISIBLE);
                imgvCauD.setVisibility(View.VISIBLE);
                break;
        }

        switch (cauHoi.getIdLoaiCH()){
            case 1: rbtnDe.setChecked(true);
                rbtnVua.setChecked(false);
                rbtnKho.setChecked(false);
                break;
            case 2: rbtnDe.setChecked(false);
                rbtnVua.setChecked(true);
                rbtnKho.setChecked(false);
                break;
            case 3: rbtnDe.setChecked(false);
                rbtnVua.setChecked(false);
                rbtnKho.setChecked(true);
                break;
        }
    }
}
