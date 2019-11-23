package com.moon.ailatrieuphu;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.moon.ailatrieuphu.adminpage.AdminMainActivity;
import com.moon.ailatrieuphu.api.APIConnect;
import com.moon.ailatrieuphu.api.APIService;
import com.moon.ailatrieuphu.email.EncryptPass;
import com.moon.ailatrieuphu.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private EditText edtEmailLogin, edtPassdLogin, diaEdtEmailForgotPass, diaEdtCodeForgotPass;
    private Button btnLogin, btnRegis_Login, btnExitApp, btnStartGame, btnCheckInfo, btnExit2, btnSubmitExit,
            btnCancelExit, diaBtnConfirmForgotPass, diaBtnCancelForgotPass;
    private String email, password, errEmail, errPassword;
    private TextView txtHello, txtForgotPass, diaTxtSendCode;
    private static final int SIGNUP_REQUEST_CODE = 1;
    private SharedPreferences sharedPreferences;
    private CheckBox cbRemember;
    APIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setControl();

        setEvent();
    }

    private void setControl() {
        edtEmailLogin = findViewById(R.id.edittextEmail);
        edtPassdLogin = findViewById(R.id.edittextPassword);
        btnLogin = findViewById(R.id.buttonLogin);
        btnExitApp = findViewById(R.id.buttonExitApp);
        btnRegis_Login = findViewById(R.id.buttonRegis);
        txtForgotPass = findViewById(R.id.txt_forgot_password);
        cbRemember = findViewById(R.id.chbRememberSignIn);
        apiService = APIConnect.getServer();

        sharedPreferences = getSharedPreferences("userLogin", MODE_PRIVATE);
        edtEmailLogin.setText(sharedPreferences.getString("email", ""));
        edtPassdLogin.setText(sharedPreferences.getString("password", ""));
        cbRemember.setChecked(sharedPreferences.getBoolean("remember", false));
    }

    private void setEvent() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = edtEmailLogin.getText().toString().trim();
                password = edtPassdLogin.getText().toString().trim();
//                email="mem@gmail.com";
//                password="12345678";
                //check valid infomation
                errEmail = checkNull(email);
                errPassword = checkNull(password);
                if (!errEmail.equals("")) {
                    Toast.makeText(MainActivity.this, "Email không được chứa toàn khoảng trắng và không được rỗng!", Toast.LENGTH_SHORT).show();
                    edtEmailLogin.requestFocus();
                    return;
                }
                if (!errPassword.equals("")) {
                    Toast.makeText(MainActivity.this, "Mật khẩu không được chứa toàn khoảng trắng và không được rỗng!", Toast.LENGTH_SHORT).show();
                    edtPassdLogin.requestFocus();
                    return;
                }

                final User user = new User();
                user.setEmail(email);
                user.setPassword(EncryptPass.md5(password));

                apiService.getUserLogin(user).enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        Log.d("AAA", response.body().isAdminRole() + "");
                        Log.d("AAA", response.body().getEmail() + "");
                        if (response.code() == 200) {
                            //login success
                            User user1 = response.body();
                            Program.user = new User();
                            Program.user = user1;
                            if (cbRemember.isChecked()) {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("email", email);
                                editor.putString("password", password);
                                editor.putBoolean("remember", true);
                                editor.commit();
                            } else {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.remove("email");
                                editor.remove("password");
                                editor.remove("remember");
                                editor.commit();
                            }
                            if (Program.user.isAdminRole()) {
                                startActivity(new Intent(MainActivity.this, AdminMainActivity.class));
                                finish();
                            } else {
                                openDialogStart();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Sai Email hoặc Password. Vui lòng kiểm tra lại!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "Có lỗi xảy ra. Vui lòng kiểm tra lại!", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
        txtForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogForgotPass();
            }
        });
        btnRegis_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivityForResult(intent, SIGNUP_REQUEST_CODE);
            }
        });
        btnExitApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //openDialogExit();
                openAlertExit();
            }
        });
    }

    private void openDialogForgotPass() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_sendcode);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
        diaBtnConfirmForgotPass = dialog.findViewById(R.id.buttonSubmitCode);
        diaBtnCancelForgotPass = dialog.findViewById(R.id.buttonCancelCode);
        diaEdtCodeForgotPass = dialog.findViewById(R.id.editText_EnterCode);
        diaEdtEmailForgotPass = dialog.findViewById(R.id.editText_EnterEmail);
        diaTxtSendCode = dialog.findViewById(R.id.textViewSendCode);

        diaBtnCancelForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

    }

    private void openAlertExit() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Xác nhận!");
        alertDialog.setIcon(R.mipmap.ic_launcher);
        alertDialog.setMessage("Bạn có thực sự muốn thoát Ai là triệu phú?");
        alertDialog.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAffinity();
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

    private void openDialogExit() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_exit_app);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
        btnSubmitExit = dialog.findViewById(R.id.buttonExitGame);
        btnCancelExit = dialog.findViewById(R.id.buttonCancelExit);
        btnSubmitExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAffinity();
            }
        });
        btnCancelExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGNUP_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                edtEmailLogin.setText(data.getStringExtra("#email"));
                edtPassdLogin.setText("");
                edtPassdLogin.requestFocus();
            }
        }
    }

    private void openDialogStart() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_startgame);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
        btnStartGame = dialog.findViewById(R.id.buttonStartGame);
        btnCheckInfo = dialog.findViewById(R.id.buttonCheckInfo);
        btnExit2 = dialog.findViewById(R.id.buttonExit);
        txtHello = dialog.findViewById(R.id.textViewHello);
        txtHello.setText("Xin chào " + Program.user.getNickname() + " !");
        btnStartGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PlayActivity.class);
                startActivity(intent);
            }
        });
        btnCheckInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
        btnExit2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
    }

    private String checkNull(String s) {
        String result = ""; // save notification if has any field not right
        //Verify if Pass has all space or null
        if (s.equals("")) {
            edtEmailLogin.setText("");
            result = "Hãy nhập vào";
        }

        return result;
    }

    public Boolean checkSpace(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (Character.isSpaceChar(s.charAt(i))) return true;
        }
        return false;
    }

}
