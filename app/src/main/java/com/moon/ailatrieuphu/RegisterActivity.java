package com.moon.ailatrieuphu;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.moon.ailatrieuphu.email.GMailSender;
import com.moon.ailatrieuphu.email.TimerSingleton;
import com.moon.ailatrieuphu.model.User;
import com.moon.ailatrieuphu.utility.ProgressDialogF;

import java.util.Random;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtEmail, edtNickname, edtPassword, edtRePassword, diaEdtCode;
    private Button btnRegister, btnCancelRegis,  diaBtnConfirmCode,  diaBtnCancelCode;
    private String email, nickname, password, passwordEncrypt, rePassword, errEmail, errNickname, errPassword;
    private TextView diaTxtSendCode, diaTxtSendCode2;
    private String code = "";
    private CountDownTimer timer;
    public static int timeFuture = 60000;
    public static int timeInterval = 1000;
    public static String countTestTime = "0";

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
                passwordEncrypt= EncryptPass.md5(password.trim());
                final User user=new User();
                user.setEmail(email.trim());
                user.setNickname(nickname.trim());
                user.setPassword(passwordEncrypt);
                user.setCreateTime(Program.getDateTimeNow());
                Log.d("email", user.getEmail());

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
                            showDialogCode(user);
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

    private void showDialogCode(User user) {
        final String titleMail = "Ai là triệu phú: Mã code xác thực";
        final String messageMail = "Xin chào! Đây là mã code bạn yêu cầu từ admin game Ai là triệu phú\n";
        code = getRandomNumberString();
        Log.d("code", code);
        sendMail(titleMail, messageMail, code, user.getEmail());

        TimerSingleton.timer.start();
        final Dialog dialogSendCode = new Dialog(RegisterActivity.this);
        dialogSendCode.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSendCode.setContentView(R.layout.dialog_register_enter_code);
        dialogSendCode.setCanceledOnTouchOutside(false);
        dialogSendCode.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogSendCode.show();
        diaEdtCode = dialogSendCode.findViewById(R.id.editText_Code);
        diaTxtSendCode = dialogSendCode.findViewById(R.id.textViewSendCode1);
        diaTxtSendCode2 = dialogSendCode.findViewById(R.id.textViewSendCode2);
        diaBtnConfirmCode = dialogSendCode.findViewById(R.id.buttonSubmitCode);
        diaBtnCancelCode = dialogSendCode.findViewById(R.id.buttonCancelCode);

        diaEdtCode.requestFocus();
        dialogSendCode.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                timer = new CountDownTimer(timeFuture, timeInterval) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        diaTxtSendCode2.setText("  " + millisUntilFinished/1000 + "");
                        diaTxtSendCode2.setEnabled(false);
                        diaTxtSendCode.setEnabled(false);
                        if (millisUntilFinished/1000 == 0){
                            timer.onFinish();
                        }
                    }

                    @Override
                    public void onFinish() {
                        diaTxtSendCode2.setText("Bấm vào đây!");
                        diaTxtSendCode2.setEnabled(true);
                        diaTxtSendCode.setEnabled(true);
                    }
                }.start();
            }
        });
        diaTxtSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                code = getRandomNumberString();
                Log.d("code", code);
                TimerSingleton.timer.start();
                sendMail(titleMail, messageMail, code, user.getEmail());
                timer.start();
            }
        });
        diaTxtSendCode2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                code = getRandomNumberString();
                TimerSingleton.timer.start();
                sendMail(titleMail, messageMail, code, user.getEmail());
                timer.start();
            }
        });
        diaBtnCancelCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSendCode.cancel();
                TimerSingleton.timer.cancel();
            }
        });
        diaBtnConfirmCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputCode = diaEdtCode.getText().toString().trim();
                if(inputCode.equals("")){
                    Toast.makeText(getApplicationContext(), "Bạn chưa nhập mã code!", Toast.LENGTH_SHORT).show();
                }
                if(countTestTime.equals("0")){
                    Toast.makeText(getApplicationContext(), "Hết giờ!", Toast.LENGTH_SHORT).show();
                }else if(inputCode.equals(code)){
                    apiService.addUser(user).enqueue(new Callback<Integer>() {
                        @Override
                        public void onResponse(Call<Integer> call, Response<Integer> response) {
                            ProgressDialogF.hideLoading();
                            if((response.body())!=0){
                                Toast.makeText(RegisterActivity.this,"Đăng ký thành công!",Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(RegisterActivity.this,"Có lỗi xảy ra! Đăng Ký thất bại",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }

    public static String getRandomNumberString() {
        Random rnd = new Random();
        int number = rnd.nextInt(999999);

        // this will convert any number sequence into 6 character.
        return String.format("%06d", number);
    }
    public static void sendMail(final String title, final String message, final String code, final String email){
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    GMailSender sender = new GMailSender("ailatrieuphu.tt123@gmail.com",
                            "#1ailatrieuphu");
                    sender.sendMail(title, message + " " + code,
                            "ailatrieuphu.tt123@gmail.com", email);

                } catch (Exception e) {
                    Log.e("SendMail", e.getMessage(), e);

                }
            }

        }).start();
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
