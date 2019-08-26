package com.hellochiu.task;

import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;

public class LoadingDialogTask extends AsyncTask<Void, Void, String> {

    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private LoadingDialogTaskListener listener;

    public LoadingDialogTask(AlertDialog.Builder builder, LoadingDialogTaskListener listener) {
        this.builder = builder;
        this.listener = listener;
    }

    @Override
    protected String doInBackground(Void... voids) {
        return listener.onBackground();
    }

    @Override
    protected void onPreExecute() {
        builder.setOnCancelListener(dialog -> this.cancel(true));
        dialog = builder.show();
    }

    @Override
    protected void onPostExecute(String result) {
        dialog.dismiss();
        listener.onFinish(result);
    }

}
