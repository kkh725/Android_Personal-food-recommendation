package com.example.toss_test.etc;

import android.os.AsyncTask;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkhttpClient_Post extends AsyncTask<String, Void, String> {

    OkHttpClient client = new OkHttpClient();
    @Override
    protected String doInBackground(String... params) {
        String result = null;
        String strUrl = params[0];

        RequestBody formBody = new FormBody.Builder()
                .add("member_key", params[1])
                .build();

        /**
         * new OkhttpClient_Post().execute(url,my message).get();
         * 첫번째 파라미터로 api 통신할 url 입력하고, 두번째 파라미터로 url 에 보낼 메세지값을 입력한다.
         * api 받는 서버에서 어떤형식으로 받아올지 는 내가 add 에 입력한 값으로.
         * 내가 "message" / 메세지 ! ////라고 보내면 서버에서 message 값을 받으면 메세지 ! 값이 받아져온다.
         */

        Request request = new Request.Builder().url(strUrl)
                .post(formBody)
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
