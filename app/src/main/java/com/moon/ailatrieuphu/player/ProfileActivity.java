package com.moon.ailatrieuphu.player;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.moon.ailatrieuphu.MainActivity;
import com.moon.ailatrieuphu.Program;
import com.moon.ailatrieuphu.R;
import com.moon.ailatrieuphu.RegisterActivity;
import com.moon.ailatrieuphu.api.APIConnect;
import com.moon.ailatrieuphu.api.APIService;
import com.moon.ailatrieuphu.email.EncryptPass;
import com.moon.ailatrieuphu.model.User;
import com.moon.ailatrieuphu.utility.ProgressDialogF;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
    private TextView txtHello, txtEmail, txtNickname, txtPassword, txtHighScore;
    private Button btnChangePassword, btnExit, btnSubmitChangePass, btnCancelChangPass;
    private EditText edtOldPass, edtNewPass, edtReNewPass;
    private String errCurrentPass, errNewPass, errReNewPass, currentPass, newPass, reNewPass;
    public static int diemCao;
    APIService apiService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setContol();
        setEvent();
    }

    private void setContol() {

        txtEmail=findViewById(R.id.textViewEmail);
        txtNickname=findViewById(R.id.textViewNickname);
        txtPassword=findViewById(R.id.textViewPassword);
        txtHighScore=findViewById(R.id.textviewHighscore1);
        btnChangePassword=findViewById(R.id.buttonChangePass);
        btnExit =findViewById(R.id.buttonExit);

        Log.d("test1", Program.user.getNickname());

        txtEmail.setText(Program.user.getEmail());
        txtNickname.setText(Program.user.getNickname());
        txtPassword.setText(Program.user.getPassword());

        apiService= APIConnect.getServer();
        txtHighScore.setText(String.valueOf(Program.user.getDiemCao()));
    }

    private void setEvent() {
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog=new Dialog(ProfileActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_change_pass);
                dialog.setCanceledOnTouchOutside(false);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();
                edtOldPass=dialog.findViewById(R.id.editTextCurrentPassword);
                edtNewPass=dialog.findViewById(R.id.editTextNewPassword);
                edtReNewPass=dialog.findViewById(R.id.editTextReNewPassword);
                btnSubmitChangePass=dialog.findViewById(R.id.buttonSubmitChangePass);
                btnCancelChangPass=dialog.findViewById(R.id.buttonCancelChangePass);
                btnSubmitChangePass.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentPass=edtOldPass.getText().toString();
                        newPass=edtNewPass.getText().toString();
                        reNewPass=edtReNewPass.getText().toString();
                        errNewPass= RegisterActivity.checkPassword(newPass);
                        if(currentPass.equals("")&&newPass.equals("")&&reNewPass.equals("")){
                            Toast.makeText(ProfileActivity.this,"Bạn cần nhập tất cả các trường!",Toast.LENGTH_SHORT).show();
                            edtOldPass.requestFocus();
                            return;
                        }
                        if(currentPass.equals("")){
                            Toast.makeText(ProfileActivity.this,"Bạn chưa nhập mật khẩu hiện tại!",Toast.LENGTH_SHORT).show();
                            edtOldPass.requestFocus();
                            return;
                        }
                        if(!EncryptPass.md5(currentPass.trim()).equals(Program.user.getPassword())){
                            Toast.makeText(ProfileActivity.this,"Mật khẩu sai. Vui lòng nhập lại!",Toast.LENGTH_SHORT).show();
                            edtOldPass.requestFocus();
                            edtOldPass.setSelection(edtOldPass.getText().length());
                            return;
                        }
                        if(!errNewPass.equals("")){
                            Toast.makeText(ProfileActivity.this,errNewPass,Toast.LENGTH_SHORT).show();
                            edtNewPass.requestFocus();
                            edtNewPass.setSelection(edtNewPass.getText().length());
                            return;
                        }
                        if(RegisterActivity.checkSameValue(newPass.trim(), currentPass.trim())){
                            Toast.makeText(ProfileActivity.this,"Mật khẩu mới nên khác mật khẩu gần đây của bạn!",Toast.LENGTH_SHORT).show();
                            edtNewPass.requestFocus();
                            edtNewPass.setSelection(edtNewPass.getText().length());
                            return;
                        }
                        if(reNewPass.equals("")){
                            Toast.makeText(ProfileActivity.this,"Bạn chưa nhập lại mật khẩu mới!",Toast.LENGTH_SHORT).show();
                            edtReNewPass.requestFocus();
                            return;
                        }
                        if(!RegisterActivity.checkSameValue(reNewPass.trim(), newPass.trim())){
                            Toast.makeText(ProfileActivity.this,"Mật khẩu nhập lại không trùng!",Toast.LENGTH_SHORT).show();
                            edtReNewPass.requestFocus();
                            edtReNewPass.setSelection(edtReNewPass.getText().length());
                            return;
                        }
                        final User user=new User();
                        user.setIdUser(Program.user.getIdUser());
                        user.setPassword(EncryptPass.md5(newPass));
                        user.setUpdateTime(Program.getDateTimeNow());
                        ProgressDialogF.showLoading(ProfileActivity.this);
                        apiService.updatePassword(user).enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                ProgressDialogF.hideLoading();
                                if(response.body().equals("success")){
                                    Toast.makeText(ProfileActivity.this,"Thay đổi mật khẩu thành công!",Toast.LENGTH_SHORT).show();
                                    Program.user.setPassword(user.getPassword());
                                    Intent intent=new Intent(ProfileActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }else{
                                    Toast.makeText(ProfileActivity.this,"Thay đổi mật khẩu thất bại!",Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                Toast.makeText(ProfileActivity.this,"Thay đổi mật khẩu thất bại. Có lỗi xảy ra!",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                btnCancelChangPass.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                    }
                });
            }
        });
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
