package com.example.toss_test.etc;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.toss_test.Login.Login;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkhttpClient_Get extends AsyncTask<String, Void, String> {

    OkHttpClient client = new OkHttpClient();

    @Override
    protected String doInBackground(String... params) {
        String result = null;
        String strUrl = params[0];

        Request request = new Request.Builder().url(strUrl)
                .build();
        try {
            Response response = client.newCall(request).execute();
            result = response.body().string();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}




