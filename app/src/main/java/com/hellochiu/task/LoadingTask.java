package com.hellochiu.task;

import android.os.AsyncTask;

public class LoadingTask extends AsyncTask<Void, Void, String> {

    private LoadingTaskListener listener;

    public LoadingTask(LoadingTaskListener listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(Void... voids) {
        return listener.onBackground();
    }

    @Override
    protected void onPostExecute(String result) {
        listener.onFinish(result);
    }
}
