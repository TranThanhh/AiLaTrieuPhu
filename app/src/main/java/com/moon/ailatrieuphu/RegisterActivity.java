package com.moon.ailatrieuphu;

import androidx.appcompat.app.AppCompatActivity;

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

import com.moon.ailatrieuphu.api.APIConnect;
import com.moon.ailatrieuphu.api.APIService;
import com.moon.ailatrieuphu.email.EncryptPass;
import com.moon.ailatrieuphu.model.Diem;
import com.moon.ailatrieuphu.model.User;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtEmail, edtNickname, edtPassword, edtRePassword, diaEdtCode;
    private Button btnRegister, btnCancelRegis,  diaBtnConfirmCode,  diaBtnCancelCode;
    private String email, nickname, password, passwordEncrypt, rePassword, errEmail, errNickname, errPassword;
    private TextView diaTxtSendCode, diaTxtSendCode2;
    APIService apiService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setControl();
        setEvent();
    }

    private void setControl() {
        edtEmail=findViewById(R.id.edittextEmail);
        edtNickname=findViewById(R.id.edittextNickname);
        edtPassword=findViewById(R.id.edittextPasswordRegis);
        edtRePassword=findViewById(R.id.edittextRePassword);
        btnRegister=findViewById(R.id.buttonRegister);
        btnCancelRegis=findViewById(R.id.buttonCancelRegis);
        apiService= APIConnect.getServer();
    }

    private void setEvent() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email=edtEmail.getText().toString();
                nickname=edtNickname.getText().toString();
                password=edtPassword.getText().toString();
                rePassword=edtRePassword.getText().toString();
                //check valid infomation
                errEmail=checkEmail(email);
                errNickname=checkNickname(nickname);
                errPassword=checkPassword(password);
                if(!errEmail.equals("")){
                    Toast.makeText(RegisterActivity.this,errEmail,Toast.LENGTH_SHORT).show();
                    edtEmail.requestFocus();
                    edtEmail.setSelection(edtEmail.getText().length());
                    Log.d("test","abcd");
                    return;
                }
                if(!errNickname.equals("")){
                    Toast.makeText(RegisterActivity.this,errNickname,Toast.LENGTH_SHORT).show();
                    edtNickname.requestFocus();
                    edtNickname.setSelection(edtNickname.getText().length());
                    return;
                }
                if(!errPassword.equals("")){
                    Toast.makeText(RegisterActivity.this,errPassword,Toast.LENGTH_SHORT).show();
                    edtPassword.requestFocus();
                    edtPassword.setSelection(edtPassword.getText().length());
                    return;
                }
                if(rePassword.equals("")){
                    Toast.makeText(RegisterActivity.this,"Nhập lại mật khẩu!",Toast.LENGTH_SHORT).show();
                    edtRePassword.requestFocus();
                    edtRePassword.setSelection(edtRePassword.getText().length());
                    return;
                }
                if(checkSameValue(rePassword.trim(),password.trim())==false){
                    Toast.makeText(RegisterActivity.this,"Mật khẩu nhập lại không trùng!",Toast.LENGTH_SHORT).show();
                    edtRePassword.requestFocus();
                    edtRePassword.setSelection(edtRePassword.getText().length());
                    return;
                }
                openDialogConfirmRegis();
                passwordEncrypt= EncryptPass.md5(password.trim());
                final User user=new User();
                user.setEmail(email.trim());
                user.setNickname(nickname.trim());
                user.setPassword(passwordEncrypt);

                apiService.checkUserExists(user).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        String result=response.body();
                        if(result.equals("email")){
                            Toast.makeText(RegisterActivity.this,"Email này đã tồn tại. Hãy thử lại!",Toast.LENGTH_SHORT).show();
                            edtEmail.findFocus();
                            return;
                        }
                        if(result.equals("nickname")){
                            Toast.makeText(RegisterActivity.this,"Nickname này đã tồn tại. Hãy thử lại!",Toast.LENGTH_SHORT).show();
                            edtEmail.findFocus();
                            return;
                        }
                        if(result.equals("no")){
                            ProgressDialogF.showLoading(RegisterActivity.this);
                            apiService.addUser(user).enqueue(new Callback<Integer>() {
                                @Override
                                public void onResponse(Call<Integer> call, Response<Integer> response) {
                                    ProgressDialogF.hideLoading();
                                    if((response.body())!=0){
                                        Toast.makeText(RegisterActivity.this,"Đăng ký thành công!",Toast.LENGTH_SHORT).show();
                                        Diem diem=new Diem();
                                        diem.setIdUser(response.body());
                                        diem.setDiemCao(0);
                                        apiService.setScore(diem).enqueue(new Callback<String>() {
                                            @Override
                                            public void onResponse(Call<String> call, Response<String> response) {
                                                if(response.body().equals("success")){
                                                    Log.d("diem","success");
                                                }
                                                else{
                                                    Toast.makeText(RegisterActivity.this,"Default Diem that bai!",Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<String> call, Throwable t) {

                                            }
                                        });
                                        //Program.user=user;
                                        Intent intent=new Intent();
                                        intent.putExtra("#email",user.getEmail());
                                        setResult(RESULT_OK,intent);
                                        finish();
                                    }
                                    else{
                                        Toast.makeText(RegisterActivity.this,"Đăng ký thất bại",Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Integer> call, Throwable t) {

                                }
                            });
                        }
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Toast.makeText(RegisterActivity.this,"Có lỗi xảy ra!",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        btnCancelRegis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void openDialogConfirmRegis() {
        final Dialog dialog = new Dialog(RegisterActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_register_enter_code);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();

    }

    public String checkEmail(String s) {
        String result = "";
        //verify if Email has all Space
        if (checkSpace(s) && s.trim().equals("")) {
            result = "Email phải có dạng example@gmail.com";
        }
        //verify if Email has not Enter
        else if (s.trim().equals("")) {
            result = "Bạn chưa nhập Email!";
        }
        //
        if(emailPatern(s)=="false"){
            result="Email sai định dạng example@gmail.com";
        }
        //verify if Email has not enough lenght or overlengt
        else if (s.trim().length() < 5 || s.trim().length() > 50) {
            result = "Email có độ dài từ 5 -> 50 ký tự!";
        }
        return result;
    }

    public String checkNickname(String s) {
        String result = "";
        //verify if Nickname has all Space
        if (checkSpace(s) && (s.trim()).equals("")) {
            result = "Nickname không dược phép chứa toàn khoảng trắng";
        }
        //verify if Nickname has not Enter
        else if (s.trim().equals("")) {
            result = "Bạn chưa nhập Nickname!";
        }
        //verify if Nickname has not enough lenght or overlengt
        else if ((s.trim()).length() < 3 || (s.trim()).length() > 30) {
            result = "Nickname có độ dài từ 3 -> 30 ký tự!";
        }
        return result;
    }

    public static String checkPassword(String s) {
        String result = "";
        //verify if PASS has all Space
        if (checkSpace(s) && (s.trim()).equals("")) {
            //edtPassword.setText("");
            result = "Mật khẩu không được phép chứa toàn khoảng trắng!";
        }
        //verify if Pass has not Enter
        else if (s.trim().equals("")) {
            //edtPassword.setText("");
            result = "Bạn chưa nhập mật khẩu!";
        }
        //verify if Pass has not enough lenght or overlengt
        else if ((s.trim()).length() < 8 || (s.trim()).length() > 30) {
            result = "Mật khẩu có độ dài từ 8 -> 30 ký tự! Không tính ký tự khoảng trắng ở đầu và cuối";
        }
        return result;
    }

    public static Boolean checkSameValue(String s1,String s2){
        if(s1.equals(s2)){
            return true;
        }else{
            return false;
        }
    }

    public static Boolean checkSpace(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (Character.isSpaceChar(s.charAt(i))) return true;
        }
        return false;
    }
    public String emailPatern(String s){
        String emailPattern="[\\w.]+@[a-z.]+\\.+[a-z]+";
        if(Pattern.matches(emailPattern,s)){
            return "true";
        }
        return "false";
    }
}
