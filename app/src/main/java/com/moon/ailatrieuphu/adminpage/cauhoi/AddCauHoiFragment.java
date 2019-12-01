package com.moon.ailatrieuphu.adminpage.cauhoi;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.moon.ailatrieuphu.Program;
import com.moon.ailatrieuphu.ProgressDialogF;
import com.moon.ailatrieuphu.R;
import com.moon.ailatrieuphu.api.APIConnect;
import com.moon.ailatrieuphu.api.APIService;
import com.moon.ailatrieuphu.model.CauHoi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddCauHoiFragment extends Fragment {

    private TextView tvTitle;
    private EditText edtCauHoi, edtCauA, edtCauB, edtCauC, edtCauD;
    private RadioButton rbtnDe, rbtnVua, rbtnKho, rbtnA, rbtnB, rbtnC, rbtnD;
    private RadioGroup rgrLoai, rgrDapAn;
    private Button btnReload, btnEdit, btnDelete;
    private ImageView imgvBack, imgvSave;

    private APIService apiService;
    private FragmentManager fragmentManager;
    private CauHoi cauHoi = null;

    private boolean isEditMode = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_cau_hoi, container, false);

        setControl(view);

        setEvent(view);

        return view;
    }

    private void setEvent(final View v) {
        imgvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentManager.popBackStack();
            }
        });

        btnReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cauHoi != null) recieveData(cauHoi);
                else clearFields();
            }
        });

        imgvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String noiDung = edtCauHoi.getText().toString().trim();
                String cauA = edtCauA.getText().toString().trim();
                String cauB = edtCauB.getText().toString().trim();
                String cauC = edtCauC.getText().toString().trim();
                String cauD = edtCauD.getText().toString().trim();

                if (noiDung.equals("")) {
                    Toast.makeText(getContext(), "Nội dung câu hỏi không được để trống!", Toast.LENGTH_SHORT).show();
                    edtCauHoi.requestFocus();
                } else if (cauA.equals("")) {
                    Toast.makeText(getContext(), "Câu trả lời không được để trống!", Toast.LENGTH_SHORT).show();
                    edtCauA.requestFocus();
                } else if (cauB.equals("")) {
                    Toast.makeText(getContext(), "Câu trả lời không được để trống!", Toast.LENGTH_SHORT).show();
                    edtCauB.requestFocus();
                } else if (cauC.equals("")) {
                    Toast.makeText(getContext(), "Câu trả lời không được để trống!", Toast.LENGTH_SHORT).show();
                    edtCauC.requestFocus();
                } else if (cauD.equals("")) {
                    Toast.makeText(getContext(), "Câu trả lời không được để trống!", Toast.LENGTH_SHORT).show();
                    edtCauD.requestFocus();
                } else if(v.findViewById(rgrLoai.getCheckedRadioButtonId())==null){
                    Toast.makeText(getContext(), "Bạn chưa chọn loại câu hỏi!", Toast.LENGTH_SHORT).show();
                } else if(v.findViewById(rgrDapAn.getCheckedRadioButtonId())==null){
                    Toast.makeText(getContext(), "Bạn chưa chọn đáp án đúng!", Toast.LENGTH_SHORT).show();
                } else {
                    int idLoaiCH = Integer.parseInt((v.findViewById(rgrLoai.getCheckedRadioButtonId())).getTag().toString());
                    String dapAnDung = (v.findViewById(rgrDapAn.getCheckedRadioButtonId())).getTag().toString();

                    CauHoi cauHoiNew=new CauHoi();
                    cauHoiNew.setNoiDung(noiDung);
                    cauHoiNew.setCauA(cauA);
                    cauHoiNew.setCauB(cauB);
                    cauHoiNew.setCauC(cauC);
                    cauHoiNew.setCauD(cauD);
                    cauHoiNew.setIdLoaiCH(idLoaiCH);
                    cauHoiNew.setDapAnDung(dapAnDung);
                    cauHoiNew.setIdUser(Program.user.getIdUser());

                    if (getArguments() != null) {
                        cauHoiNew.setIdCauHoi(cauHoi.getIdCauHoi());
                        cauHoiNew.setCreatedTime(cauHoi.getCreatedTime());
                        cauHoiNew.setUpdateTime(Program.getDateTimeNow());
                        editCauHoi(cauHoiNew);
                    } else {
                        cauHoiNew.setCreatedTime(Program.getDateTimeNow());
                        addCauHoi(cauHoiNew);
                    }
                }
            }
        });
    }

    private void addCauHoi(CauHoi cauHoiNew) {
        ProgressDialogF.showLoading(getContext());
        apiService.addCauHoi(cauHoiNew).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                ProgressDialogF.hideLoading();
                if(response.body().equals("success")){
                    Toast.makeText(getContext(), "Thêm câu hỏi thành công!", Toast.LENGTH_SHORT).show();
                    fragmentManager.popBackStack();
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

    private void editCauHoi(CauHoi cauHoiEdit) {

    }

    private void clearFields() {
        edtCauHoi.setText("");
        edtCauA.setText("");
        edtCauB.setText("");
        edtCauC.setText("");
        edtCauD.setText("");
        rgrDapAn.clearCheck();
        rgrLoai.clearCheck();
    }

    private void setControl(View view) {
        tvTitle = view.findViewById(R.id.textviewTitleCauHoi);

        edtCauHoi = view.findViewById(R.id.edittextNoiDungCauHoi);
        edtCauA = view.findViewById(R.id.edittextTraLoiA);
        edtCauB = view.findViewById(R.id.edittextTraLoiB);
        edtCauC = view.findViewById(R.id.edittextTraLoiC);
        edtCauD = view.findViewById(R.id.edittextTraLoiD);

        rbtnDe = view.findViewById(R.id.radioDe);
        rbtnVua = view.findViewById(R.id.radioVua);
        rbtnKho = view.findViewById(R.id.radioKho);

        imgvSave = view.findViewById(R.id.imageviewSave);
        imgvBack = view.findViewById(R.id.imageviewBack);

        rbtnA = view.findViewById(R.id.radioA);
        rbtnB = view.findViewById(R.id.radioB);
        rbtnC = view.findViewById(R.id.radioC);
        rbtnD = view.findViewById(R.id.radioD);

        rgrLoai = view.findViewById(R.id.radiogroupLoai);
        rgrDapAn = view.findViewById(R.id.radiogroupDapAn);

        btnReload = view.findViewById(R.id.buttonReload);
        btnDelete = view.findViewById(R.id.buttonDelete);

        fragmentManager = getFragmentManager();
        apiService = APIConnect.getServer();

        rbtnDe.setTag(1);
        rbtnVua.setTag(2);
        rbtnKho.setTag(3);
        rbtnA.setTag("A");
        rbtnB.setTag("B");
        rbtnC.setTag("C");
        rbtnD.setTag("D");

        if (getArguments() != null) {
            btnEdit.setVisibility(View.VISIBLE);
            btnDelete.setVisibility(View.VISIBLE);

            cauHoi = (CauHoi) getArguments().getSerializable("#cauhoi");
            if (cauHoi != null) recieveData(cauHoi);
        } else{
            btnEdit.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);
        }
    }

    private void recieveData(final CauHoi cauHoi) {
        tvTitle.setText("Chỉnh sửa câu hỏi");
        edtCauHoi.setText(cauHoi.getNoiDung() + "");
        edtCauA.setText(cauHoi.getCauA());
        edtCauB.setText(cauHoi.getCauB());
        edtCauC.setText(cauHoi.getCauC());
        edtCauD.setText(cauHoi.getCauD());
        switch (cauHoi.getIdLoaiCH()) {
            case 1:
                rbtnDe.setChecked(true);
                break;
            case 2:
                rbtnVua.setChecked(true);
                break;
            case 3:
                rbtnKho.setChecked(true);
                break;
        }
        switch (cauHoi.getDapAnDung()) {
            case "A":
                rbtnA.setChecked(true);
                break;
            case "B":
                rbtnB.setChecked(true);
                break;
            case "C":
                rbtnC.setChecked(true);
                break;
            case "D":
                rbtnD.setChecked(true);
                break;
        }
    }
}
