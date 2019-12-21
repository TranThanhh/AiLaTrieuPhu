package com.moon.ailatrieuphu.utility;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import com.moon.ailatrieuphu.R;

import static com.moon.ailatrieuphu.R.layout.dialog_loading;

public class LoadingDialog {
    private static AlertDialog mLoadingDialog;

    private static AlertDialog create(Context context) {
        AlertDialog.Builder alBuilder = new AlertDialog.Builder(context);
        alBuilder.setCancelable(false);
        alBuilder.setView(R.layout.dialog_loading);
        AlertDialog loadingDialog = alBuilder.create();
        if (loadingDialog.getWindow() != null) {
            loadingDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        loadingDialog.show();
        return loadingDialog;
    }

    public static void show(Context context) {
        hide();
        mLoadingDialog = create(context);
    }

    public static void hide() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }
}
