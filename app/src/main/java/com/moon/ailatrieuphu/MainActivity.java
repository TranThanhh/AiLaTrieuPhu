package com.moon.ailatrieuphu;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.moon.ailatrieuphu.email.TimerSingleton;
import com.moon.ailatrieuphu.model.User;
import com.moon.ailatrieuphu.player.ProfileActivity;
import com.moon.ailatrieuphu.player.PlayerHighScoreDialogFragment;
import com.moon.ailatrieuphu.utility.ProgressDialogF;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private Button btnLogin, btnRegis_Login, btnExitApp, btnStartGame, btnCheckInfo, btnLogout, btnViewHighScore, btnSubmitExit,
            btnCancelExit, diaBtnConfirmForgotPass, diaBtnCancelForgotPass;
    private EditText edtEmailLogin, edtPassdLogin;
    private String email, password, errEmail, errPassword;
    private TextView txtHello, txtForgotPass;
    private static final int SIGNUP_REQUEST_CODE = 1;
    private SharedPreferences sharedPreferences;
    private CheckBox cbRemember;
    private String code = "";
    private CountDownTimer timer;
    private APIService apiService;

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

        loadSharedPreferences();
    }

    private void loadSharedPreferences() {
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
                            if (Program.user.getRoleLevel() == 0) {
                                openDialogStart();
                            } else {
                                startActivity(new Intent(MainActivity.this, AdminMainActivity.class));
                                finish();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Sai Email hoặc Password. Vui lòng kiểm tra lại!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Toast.makeText(MainActivity.this, R.string.err_connect, Toast.LENGTH_SHORT).show();
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
                openAlertExit();
            }
        });
    }

    private void openDialogForgotPass() {
        final String titleMail = "Ai là triệu phú: Mã code xác thực";
        final String messageMail = "Xin chào! Đây là mã code bạn yêu cầu từ admin game Ai là triệu phú\n";
        final Dialog dialogSendCode = new Dialog(MainActivity.this);
        dialogSendCode.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSendCode.setContentView(R.layout.dialog_sendcode);
        dialogSendCode.setCanceledOnTouchOutside(false);

        final Button diaBtnConfirmCode = dialogSendCode.findViewById(R.id.buttonSubmitCode);
        final Button diaBtnCancelCode = dialogSendCode.findViewById(R.id.buttonCancelCode);
        final EditText diaEdtCodeForgotPass = dialogSendCode.findViewById(R.id.editText_EnterCode);
        final EditText diaEdtEmailForgotPass = dialogSendCode.findViewById(R.id.editText_EnterEmail);
        final TextView diaTxtSendCode = dialogSendCode.findViewById(R.id.textViewSendCode);
        final User userForgot = new User();
        diaEdtEmailForgotPass.requestFocus();
        diaEdtCodeForgotPass.setEnabled(false);
        diaBtnConfirmCode.setEnabled(false);

        diaTxtSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String sEmail = diaEdtEmailForgotPass.getText().toString();
                String errorEmail = "";
                errorEmail = RegisterActivity.checkEmail(sEmail);
                if (!errorEmail.equals("")) {
                    Toast.makeText(MainActivity.this, errorEmail, Toast.LENGTH_SHORT).show();
                    diaEdtEmailForgotPass.requestFocus();
                    diaEdtEmailForgotPass.setSelection(sEmail.length());
                    return;
                }

                code = Program.getRandom8NumberString();
                userForgot.setEmail(sEmail.trim());
                apiService.checkUserExists(userForgot).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.body().equals("email")) {
                            Program.sendMail(titleMail, messageMail, code, userForgot.getEmail());
                            TimerSingleton.timer.start();
                            diaEdtCodeForgotPass.setEnabled(true);
                            diaEdtCodeForgotPass.requestFocus();
                            diaBtnConfirmCode.setEnabled(true);
                            timer = new CountDownTimer(Program.timeFuture, Program.timeInterval) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                    diaTxtSendCode.setText("   " + millisUntilFinished / 1000 + "");
                                    diaTxtSendCode.setEnabled(false);
                                }

                                @Override
                                public void onFinish() {
                                    diaTxtSendCode.setText("Gửi code");
                                    diaTxtSendCode.setEnabled(true);
                                }
                            }.start();
                        } else {
                            Toast.makeText(MainActivity.this, "Email chưa đăng ký!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });
            }
        });

        dialogSendCode.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogSendCode.show();

        diaBtnCancelCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogSendCode.cancel();
            }
        });
        diaBtnConfirmCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputCode = diaEdtCodeForgotPass.getText().toString().trim();
                if (inputCode.equals("")) {
                    Toast.makeText(MainActivity.this, "Bạn chưa nhập vào mã code!", Toast.LENGTH_SHORT).show();
                } else if (!inputCode.equals(code)) {
                    Toast.makeText(MainActivity.this, "Mã code không đúng! Nhập lại!", Toast.LENGTH_SHORT).show();
                } else {
                    dialogSendCode.cancel();
                    dialogChangePass(userForgot.getEmail());
                }
            }
        });
    }

    private void dialogChangePass(String email) {
        final Dialog dialogChangePass = new Dialog(MainActivity.this);
        dialogChangePass.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogChangePass.setContentView(R.layout.dialog_forgot_password);
        dialogChangePass.setCanceledOnTouchOutside(false);

        final Button diaBtnConfirmPass = dialogChangePass.findViewById(R.id.buttonSubmitPass);
        final Button diaBtnCancelPass = dialogChangePass.findViewById(R.id.buttonCancelPass);
        final EditText diaEdtNewPass = dialogChangePass.findViewById(R.id.editText_NewPassword);
        final EditText diaEdtRePass = dialogChangePass.findViewById(R.id.editText_ReNewPassword);

        dialogChangePass.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogChangePass.show();
        diaBtnConfirmPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPassword = diaEdtNewPass.getText().toString();
                String reNewPassword = diaEdtRePass.getText().toString().trim();
                String errPass = "";
                errPass = RegisterActivity.checkPassword(newPassword);
                if (!errPass.equals("")) {
                    Toast.makeText(MainActivity.this, errPass, Toast.LENGTH_SHORT).show();
                    diaEdtNewPass.requestFocus();
                    diaEdtNewPass.setSelection(newPassword.length());
                    return;
                } else if (reNewPassword.equals("")) {
                    Toast.makeText(MainActivity.this, "Nhập lại mật khẩu", Toast.LENGTH_SHORT).show();
                    diaEdtRePass.requestFocus();
                    diaEdtRePass.setSelection(reNewPassword.length());
                    return;
                } else if (!reNewPassword.equals(newPassword.trim())) {
                    Toast.makeText(MainActivity.this, "Mật khẩu nhập lại không chính xác!", Toast.LENGTH_SHORT).show();
                    diaEdtRePass.requestFocus();
                    diaEdtRePass.setSelection(reNewPassword.length());
                    return;
                } else {
                    final User userChangePass = new User();
                    userChangePass.setEmail(email);
                    userChangePass.setPassword(EncryptPass.md5(newPassword.trim()));
                    userChangePass.setUpdateTime(Program.getDateTimeNow());
                    ProgressDialogF.showLoading(MainActivity.this);
                    apiService.forgotPassword(userChangePass).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            ProgressDialogF.hideLoading();
                            if (response.code() == 200) {
                                Toast.makeText(MainActivity.this, "Thay đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                                dialogChangePass.cancel();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(MainActivity.this, R.string.err_connect, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        diaBtnCancelPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogChangePass.cancel();
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
        btnViewHighScore = dialog.findViewById(R.id.buttonViewHighScore);
        btnLogout = dialog.findViewById(R.id.buttonLogout);
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
        btnViewHighScore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new PlayerHighScoreDialogFragment().show(getSupportFragmentManager(), null);
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Program.user = null;
                dialog.dismiss();
            }
        });
        dialog.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                loadSharedPreferences();
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
