package com.moon.ailatrieuphu;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.moon.ailatrieuphu.api.APIConnect;
import com.moon.ailatrieuphu.api.APIService;
import com.moon.ailatrieuphu.model.CauHoi;
import com.moon.ailatrieuphu.model.User;
import com.moon.ailatrieuphu.utility.ProgressDialogF;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlayActivity extends AppCompatActivity {

    private Button btnA, btnB, btnC, btnD, btnStopPlay, btnBackLost, btnBackWin, btnExitLost, btnExitWin, btnOKHelpCall,btnOKHelpMem;
    private TextView txtQuestion, txtTime, txtHighScore, txtScoreLost, txtScoreWin, txtHelpCall, txtDapAnA,txtDapAnB,txtDapAnC,txtDapAnD;
    private List<CauHoi>  list5ChEasy, list5ChMedium, list5ChHard;
    private int idEasy = 1,idMedium = 2,idHard = 3, numberDoneQuestion= 0, idCauHoi, diem = 0,diemCao, percent;
    private CountDownTimer countDownTimer;
    private int[] mucdiem={200,400,600,1000,2000,3000,6000,10000,14000,22000,30000,40000,60000,85000,150000};
    private ArrayList<Integer> listRandomEasy, listRandomMedium, listRandomeHard;
    private String dapAn = "";
    private ImageView img5050, imgCall, imgHelp;

    APIService apiService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        setControl();
        setEvent();
    }

    private void setControl() {
        txtQuestion =findViewById(R.id.textviewQuestion);
        txtTime=findViewById(R.id.textviewTime);
        txtHighScore=findViewById(R.id.textviewScore);
        btnA=findViewById(R.id.buttonA);
        btnB=findViewById(R.id.buttonB);
        btnC=findViewById(R.id.buttonC);
        btnD=findViewById(R.id.buttonD);
        btnStopPlay=findViewById(R.id.buttonLogout);
        img5050=findViewById(R.id.img5050);
        imgCall=findViewById(R.id.imgCall);
        imgHelp=findViewById(R.id.imgHelp);
        Animation animation= AnimationUtils.loadAnimation(this,R.anim.anim_alpha);

        list5ChEasy=new ArrayList<>();
        list5ChMedium=new ArrayList<>();
        list5ChHard=new ArrayList<>();

        apiService= APIConnect.getServer();
        ProgressDialogF.showLoading(PlayActivity.this);
        apiService.getByIdLoaiCH(idEasy).enqueue(new Callback<List<CauHoi>>() {
            @Override
            public void onResponse(Call<List<CauHoi>> call, Response<List<CauHoi>> response) {
                ProgressDialogF.hideLoading();
                listRandomEasy =getRandomNonRepeatingIntegers(5, 0,response.body().size()-1);
                Log.d("dapan list",listRandomEasy+"");

                for (int i = 0; i < listRandomEasy.size(); i++) {
                    list5ChEasy.add(response.body().get(listRandomEasy.get(i)));
                }
                generateQuestion(numberDoneQuestion);
                //numberDoneQuestion++;

            }

            @Override
            public void onFailure(Call<List<CauHoi>> call, Throwable t) {

            }
        });
        apiService.getByIdLoaiCH(idMedium).enqueue(new Callback<List<CauHoi>>() {
            @Override
            public void onResponse(Call<List<CauHoi>> call, Response<List<CauHoi>> response) {
                listRandomMedium =getRandomNonRepeatingIntegers(5, 0,response.body().size()-1);
                for (int i = 0; i < listRandomMedium.size(); i++) {
                    list5ChMedium.add(response.body().get(listRandomMedium.get(i)));
                }
            }

            @Override
            public void onFailure(Call<List<CauHoi>> call, Throwable t) {

            }
        });
        apiService.getByIdLoaiCH(idHard).enqueue(new Callback<List<CauHoi>>() {
            @Override
            public void onResponse(Call<List<CauHoi>> call, Response<List<CauHoi>> response) {
                listRandomeHard =getRandomNonRepeatingIntegers(5, 0,response.body().size()-1);
                for (int i = 0; i < listRandomeHard.size(); i++) {
                    list5ChHard.add(response.body().get(listRandomeHard.get(i)));
                }
            }

            @Override
            public void onFailure(Call<List<CauHoi>> call, Throwable t) {

            }
        });

    }

    private void generateQuestionEasy(int m) {
        Log.d("dapan cauhoi",m+"");
        setDefaultColorButton();
        CauHoi c=list5ChEasy.get(m);
        dapAn = c.getDapAnDung();

        setCauHoi(c);
        countDownTime();
    }

    private void generateQuestionMedium(int m) {
        Log.d("dapan cauhoi",m+"");
        setDefaultColorButton();
        CauHoi c=list5ChMedium.get(m-5);

        dapAn = c.getDapAnDung();

        setCauHoi(c);
        countDownTime();
    }

    private void generateQuestionHard(int m) {
        Log.d("dapan cauhoi",m+"");
        setDefaultColorButton();
        CauHoi c=list5ChHard.get(m-10);
        dapAn = c.getDapAnDung();

        setCauHoi(c);
        countDownTime();
    }

    private void setCauHoi(CauHoi c){
        txtQuestion.setText("Câu hỏi "+(numberDoneQuestion+1)+": "+c.getNoiDung());
        String A=c.getCauA(),B=c.getCauB(),C=c.getCauC(),D=c.getCauD();
        if(dapAn.equals("A")){
            btnA.setText("          "+c.getCauA());
            setDapAnRandom(btnB,btnC,btnD,B,C,D);

        }else if(dapAn.equals("B")){
            btnB.setText("          "+c.getCauB());
            setDapAnRandom(btnA,btnC,btnD,A,C,D);

        }else if(dapAn.equals("C")){
            btnC.setText("          "+c.getCauC());
            setDapAnRandom(btnB,btnA,btnD,B,A,D);

        }else if(dapAn.equals("D")){
            btnD.setText("          "+c.getCauD());
            setDapAnRandom(btnB,btnC,btnA,B,C,A);

        }
    }

    private void setDapAnRandom(Button btn1, Button btn2, Button btn3, String s1, String s2, String s3) {
        int m=getRandomInt(1,3);
        Log.d("dapan",String.valueOf(m));
        if(m==1){
            btn1.setText("          "+s1);
            btn2.setText("          "+s2);
            btn3.setText("          "+s3);
            Log.d("dapan 1",s1);
            Log.d("dapan 2",s2);
            Log.d("dapan 3",s3);

        }
        if(m==2){
            btn1.setText("          "+s2);
            btn2.setText("          "+s3);
            btn3.setText("          "+s1);
            Log.d("dapan 1",s2);
            Log.d("dapan 2",s3);
            Log.d("dapan 3",s1);
        }
        if(m==3){
            btn1.setText("          "+s3);
            btn2.setText("          "+s1);
            btn3.setText("          "+s2);
            Log.d("dapan 1",s3);
            Log.d("dapan 2",s2);
            Log.d("dapan 3",s1);
        }
    }

    private void generateQuestion(int m){
        if(m<5){
            generateQuestionEasy(m);
        }else if(m>=5&& m<10){
            generateQuestionMedium(m);
        }else if(m>=10){
            generateQuestionHard(m);
        }
        Log.d("cau",String.valueOf(m));
    }

    private void setDefaultColorButton(){
        btnA.setBackgroundResource(R.drawable.background_answer_normal);
        btnB.setBackgroundResource(R.drawable.background_answer_normal);
        btnC.setBackgroundResource(R.drawable.background_answer_normal);
        btnD.setBackgroundResource(R.drawable.background_answer_normal);
        btnA.setEnabled(true);
        btnB.setEnabled(true);
        btnC.setEnabled(true);
        btnD.setEnabled(true);
    }

    private void countDownTime(){
        if(countDownTimer!=null){
            countDownTimer.cancel();
        }
        countDownTimer=new CountDownTimer(16000,1000) {
            int load=1;
            @Override
            public void onTick(long l) {
                txtTime.setText("Thời gian\n"+(16-load));
                load++;
            }
            @Override
            public void onFinish() {
                load=1;
                txtTime.setText("Time out!");
                displayRightAnswer(dapAn);

                if(diem<2000){
                    openDialogLost();
                }else if(diem>=2000){
                    openDialogWin();
                }
            }
        };
        countDownTimer.start();
    }


    public static int getRandomInt(int min, int max) {
        Random random = new Random();
        return random.nextInt((max - min) + 1) + min;
    }

    public static ArrayList<Integer> getRandomNonRepeatingIntegers(int size, int min, int max) {
        ArrayList<Integer> numbers = new ArrayList<Integer>();
        while (numbers.size() < size) {
            int random = getRandomInt(min, max);
            if (!numbers.contains(random)) {
                numbers.add(random);
            }
        }
        return numbers;
    }

    private void setEvent() {
        btnA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnA.setBackgroundResource(R.drawable.background_answer_press);
                countDownTimer.cancel();
                returnResult("A");
            }
        });
        btnB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnB.setBackgroundResource(R.drawable.background_answer_press);
                countDownTimer.cancel();
                returnResult("B");
            }
        });
        btnC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnC.setBackgroundResource(R.drawable.background_answer_press);
                countDownTimer.cancel();
                returnResult("C");
            }
        });
        btnD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnD.setBackgroundResource(R.drawable.background_answer_press);
                countDownTimer.cancel();
                returnResult("D");
            }
        });
        btnStopPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent=new Intent(PlayActivity.this, MainActivity.class);
//                startActivity(intent);
                countDownTimer.cancel();
                finish();
            }
        });
        img5050.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                img5050.setEnabled(false);
                img5050.setImageResource(R.drawable.help5050used);
                countDownTimer.cancel();
                if(dapAn.equalsIgnoreCase("A")){
                    remove2AnswerNotRight(btnB,btnC,btnD);
                    countDownTimer.start();
                }else if(dapAn.equalsIgnoreCase("B")){
                    remove2AnswerNotRight(btnA,btnC,btnD);
                    countDownTimer.start();
                }else if(dapAn.equalsIgnoreCase("C")){
                    remove2AnswerNotRight(btnA,btnB,btnD);
                    countDownTimer.start();
                }else if(dapAn.equalsIgnoreCase("D")){
                    remove2AnswerNotRight(btnA,btnB,btnC);
                    countDownTimer.start();
                }
            }
        });
        imgCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgCall.setEnabled(false);
                imgCall.setImageResource(R.drawable.helpcallused);
                openDialogHelpCall();
            }
        });
        imgHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgHelp.setEnabled(false);
                imgHelp.setImageResource(R.drawable.helpmemused);
                openDialogHelpMem();
            }
        });
    }

    private void returnResult(String dapAnUser) {
        if(dapAn.equals(dapAnUser)){
            numberDoneQuestion++;
            generateQuestion(numberDoneQuestion);
            diem=mucdiem[numberDoneQuestion];
            txtHighScore.setText("Điểm hiện tại\n"+mucdiem[numberDoneQuestion]);

            if(numberDoneQuestion==16){
                Toast.makeText(PlayActivity.this,"Thắng!",Toast.LENGTH_SHORT).show();
                openDialogWin();
            }
        }else{
            displayRightAnswer(dapAn);
            diemCao = Program.user.getDiemCao();
            if(diem<2000) {
                openDialogLost();
            }
            if(diem>=2000){
                openDialogWin();
            }
        }
    }

    private void openDialogHelpMem() {
        final Dialog dialog=new Dialog(PlayActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_helpmem);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();

        countDownTimer.cancel();

        txtDapAnA=dialog.findViewById(R.id.dapanA);
        txtDapAnB=dialog.findViewById(R.id.dapanB);
        txtDapAnC=dialog.findViewById(R.id.dapanC);
        txtDapAnD=dialog.findViewById(R.id.dapanD);
        btnOKHelpMem=dialog.findViewById(R.id.btnOKHelpMem);
        int i1,i2,i3,i4;
        i1=getRandomInt(55,90);
        percent=100-i1;
        i2=getRandomInt(0,percent);
        percent=100-i1-i2;
        i3=getRandomInt(0,percent);
        percent=100-i1-i2-i3;
        i4=percent;
        if(dapAn.equals("A")){
            txtDapAnA.setText("A.   "+i1+ "%");
            txtDapAnB.setText("B.   "+i2+ "%");
            txtDapAnC.setText("C.   "+i3+ "%");
            txtDapAnD.setText("D.   "+i4+ "%");
        }else if(dapAn.equals("B")){
            txtDapAnA.setText("A.   "+i2+ "%");
            txtDapAnB.setText("B.   "+i1+ "%");
            txtDapAnC.setText("C.   "+i3+ "%");
            txtDapAnD.setText("D.   "+i4+ "%");
        }else if(dapAn.equals("C")){
            txtDapAnA.setText("A.   "+i2+ "%");
            txtDapAnB.setText("B.   "+i3+ "%");
            txtDapAnC.setText("C.   "+i1+ "%");
            txtDapAnD.setText("D.   "+i4+ "%");

        }else if(dapAn.equals("D")){
            txtDapAnA.setText("A.   "+i2+ "%");
            txtDapAnB.setText("B.   "+i3+ "%");
            txtDapAnC.setText("C.   "+i4+ "%");
            txtDapAnD.setText("D.   "+i1+ "%");

        }

        btnOKHelpMem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                countDownTimer.start();
            }
        });

    }

    private void openDialogHelpCall() {
        final Dialog dialog=new Dialog(PlayActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_helpcall);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
        countDownTimer.cancel();
        txtHelpCall=dialog.findViewById(R.id.textViewHelpCall);
        btnOKHelpCall =dialog.findViewById(R.id.btnOKHelpCall);
        txtHelpCall.setText("Hãy chọn đáp án "+dapAn+" !");
        btnOKHelpCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                countDownTimer.start();
            }
        });

    }

    private void remove2AnswerNotRight(Button btn1, Button btn2, Button btn3) {
        int m=getRandomInt(1,3);
            if(m==1){
                btn1.setText("");
                btn2.setText("");
                btn1.setEnabled(false);
                btn2.setEnabled(false);
            }
            if(m==2){
                btn2.setText("");
                btn3.setText("");
                btn2.setEnabled(false);
                btn3.setEnabled(false);
            }
            if(m==3){
                btn1.setText("");
                btn3.setText("");
                btn1.setEnabled(false);
                btn3.setEnabled(false);
            }
    }


    private void openDialogWin() {
        final Dialog dialog=new Dialog(PlayActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_win);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
        setHighScore();

        btnBackWin=dialog.findViewById(R.id.buttonBackWin);
        btnExitWin=dialog.findViewById(R.id.buttonExitWin);
        txtScoreWin=dialog.findViewById(R.id.textviewScoreWin);
        txtScoreWin.setText(String.valueOf(diem));
        btnBackWin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numberDoneQuestion=0;

                imgHelp.setEnabled(true);
                imgCall.setEnabled(true);
                img5050.setEnabled(true);
                img5050.setImageResource(R.drawable.help5050);
                imgHelp.setImageResource(R.drawable.helpmem);
                imgCall.setImageResource(R.drawable.helpcall);
                finish();
            }
        });
        btnExitWin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alertDialog=new AlertDialog.Builder(PlayActivity.this);
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
        });

    }

    private void setHighScore() {
        final User user=new User();
        user.setIdUser(Program.user.getIdUser());
        if(diem>diemCao){
            user.setDiemCao(diem);
            user.setUpdateTime(Program.getDateTimeNow());
            ProgressDialogF.showLoading(this);
            //call api to modify score
            apiService.modifyScore(user).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    ProgressDialogF.hideLoading();
                    Log.d("diem", diem + "");
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Toast.makeText(PlayActivity.this,"Thay đổi điểm thất bại",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void openDialogLost() {
        final Dialog dialog=new Dialog(PlayActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_lost);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
        setHighScore();

        btnBackLost=dialog.findViewById(R.id.buttonBackLost);
        btnExitLost=dialog.findViewById(R.id.buttonExitLost);
        txtScoreLost=dialog.findViewById(R.id.textviewScoreLost);
        txtScoreLost.setText(String.valueOf(diem));
        btnBackLost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numberDoneQuestion=0;
                imgHelp.setEnabled(true);
                imgCall.setEnabled(true);
                img5050.setEnabled(true);
                img5050.setImageResource(R.drawable.help5050);
                imgHelp.setImageResource(R.drawable.helpmem);
                imgCall.setImageResource(R.drawable.helpcall);
                finish();
            }
        });
        btnExitLost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alertDialog=new AlertDialog.Builder(PlayActivity.this);
                alertDialog.setTitle("Xác nhận!");
                alertDialog.setIcon(R.mipmap.ic_launcher);
                alertDialog.setMessage("Bạn có thực sự muốn thoát?");
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
        });

    }

    private void displayRightAnswer(String s){
        Animation animation= AnimationUtils.loadAnimation(PlayActivity.this,R.anim.anim_alpha);
        if(s.equalsIgnoreCase("A")){
            btnA.setBackgroundResource(R.drawable.background_answer_correct);
            btnA.startAnimation(animation);
        }else if(s.equalsIgnoreCase("B")){
            btnB.setBackgroundResource(R.drawable.background_answer_correct);
            btnB.startAnimation(animation);
        }else if(s.equalsIgnoreCase("C")){
            btnC.setBackgroundResource(R.drawable.background_answer_correct);
            btnC.startAnimation(animation);
        }else if(s.equalsIgnoreCase("D")){
            btnD.setBackgroundResource(R.drawable.background_answer_correct);
            btnD.startAnimation(animation);
        }
    }

}
