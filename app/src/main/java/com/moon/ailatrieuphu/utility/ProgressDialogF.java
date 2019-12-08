package com.moon.ailatrieuphu.utility;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import androidx.appcompat.app.AppCompatActivity;

import com.moon.ailatrieuphu.R;

public class ProgressDialogF extends AppCompatActivity {
    private static AlertDialog mAlertDialog;

    public static AlertDialog showLoadingDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_dialog);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        return alertDialog;
    }

    public static void showLoading(Context context) {
        hideLoading();
        mAlertDialog = showLoadingDialog(context);
    }

    public static void hideLoading() {
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
    }

}
