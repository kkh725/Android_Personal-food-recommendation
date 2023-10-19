package com.example.toss_test.Login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.toss_test.MainActivity;
import com.example.toss_test.Map.Naver_API;
import com.example.toss_test.Pay_Result.Failure;
import com.example.toss_test.Pay_Result.Successful;
import com.example.toss_test.PyTest;
import com.example.toss_test.R;
import com.example.toss_test.Recommend.Basic_Recommend;
import com.example.toss_test.etc.OkhttpClient_Get;
import com.example.toss_test.etc.OkhttpClient_Post;
import com.tosspayments.paymentsdk.view.TossPaymentView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Login extends AppCompatActivity {

    void hideKeyboard()
    {
        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    Button btn_login , btn_register;
    EditText Et_id, Et_pw;
    String Id, Pw, Check, Url;
    ProgressBar progressBar;
    public static String Status,Member_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LinearLayout layout = findViewById(R.id.linearlayout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
            }
        });


        /**세종시 음식점 받아오고 실행해서 x,y 좌표값 받아오기. 하지만 시간이 너무 오래걸림.

        Object[] Coordinates_x = new Object[1000];
        Object[] Coordinates_y = new Object[1000];

        Object[] duration = new Object[150];
        Object[] distance = new Object[150];

        Object[] sejong_restaurant;
        sejong_restaurant = new Naver_API().find_sejong_restaurant("조치원읍 신안리");

        long beforeTime = System.currentTimeMillis();
        for (int i=0; i< 5 ; i++) {
            Coordinates_x[i] = new Naver_API().geocode(sejong_restaurant[i].toString())[0];
            Coordinates_y[i] = new Naver_API().geocode(sejong_restaurant[i].toString())[1];

            if (Coordinates_x[i] != null) {
                Log.d("세종음식점 x좌표", Coordinates_x[i].toString());
                Log.d("세종음식점 y좌표", Coordinates_y[i].toString());
            } else {
                Log.d("세종음식점 좌표", "Coordinates[" + i + "] is null");
            }
        }

        for (int i=0;i<5; i++){
            duration[i] = new Naver_API().duration_distance("127.726137, 37.864515",Coordinates_x[i]+","+Coordinates_y[i])[0];
            distance[i] = new Naver_API().duration_distance("127.726137, 37.864515",Coordinates_x[i]+","+Coordinates_y[i])[1];
            Log.d("duration // distance",duration[i]+"//"+distance[i]);
        }

        long afterTime = System.currentTimeMillis();
        long secDiffTime = (afterTime - beforeTime)/1000;
        Log.d("시간차이(m) : ", String.valueOf(secDiffTime));
        Log.d("지번을 변환", String.valueOf(new Naver_API().geocode("상수동 72-1")[0]));
         */


/**
 * 결제 완료 했을때 성공적으로 돌아와서 파라미터가 pay_complete 이라면 성공화면으로 이동.
 */

        Intent intent = getIntent();
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri uri = intent.getData();

            if (uri != null) {
                Status = uri.getQueryParameter("status");

                if (Status.equals("PAY_COMPLETE")){ // 토스페이 결제가 성공적으로 완료되었다면 pay_complete 값을 받아오고, 그 뒤 작업 수행.
                    Intent to_success = new Intent(Login.this, Successful.class);
                    startActivity(to_success);

                }
            }
            else{
                Intent to_fail = new Intent(Login.this, Failure.class);
                startActivity(to_fail);
                return;
            }

        }

        progressBar = findViewById(R.id.progressBar4);

        btn_login = findViewById(R.id.btn_login);
        btn_register = findViewById(R.id.btn_register);

        Et_id = findViewById(R.id.Et_id);
        Et_pw = findViewById(R.id.Et_pw);

        /**
         * 로그인 버튼 눌렀을때 true 값을 받아오면 진행, 그렇지않으면 진행x
         */

        btn_login.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onClick(View v) {
                Id = Et_id.getText().toString();
                Pw = Et_pw.getText().toString();
                try {
                    Check = new Check_Database().execute(Id,Pw).get();

                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (Check.contains("true")){
                    String split_check[] = Check.split(" ");
                    Member_key = split_check[1];

                    /**
                     * 로그인 성공했을때 서버로 멤버키 보내기.
                     * 준혁씨 서버 열려야 가능
                     */
//                    Url = "http://3.39.230.197:8000/members/"+Member_key;
//                    try {
//                        String result = new OkhttpClient_Get().execute(Url).get();
//                        Log.d("멤버키 보내고 받아오기 ",result);
//                    } catch (ExecutionException e) {
//                        e.printStackTrace();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }


                    Intent intent_toOrder = new Intent(Login.this, Basic_Recommend.class);
                    startActivity(intent_toOrder);
                    Toast.makeText(Login.this, "로그인"+Member_key, Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(Login.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();

                }

            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent to_idpw = new Intent(Login.this, Register_IdPw.class);
                startActivity(to_idpw);
            }
        });


        }


    private class Check_Database extends AsyncTask<String, Void, String> {

        OkHttpClient client = new OkHttpClient();
        @Override
        protected String doInBackground(String... params) {
            String result = null;

            RequestBody formBody = new FormBody.Builder()
                    .add("Id", params[0])
                    .add("Pw",params[1])
                    .build();

            Request request = new Request.Builder().url("http://221.158.178.99:8081/DBMS_COnnection/android/Login.jsp")
                    .post(formBody)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                result = response.body().string();

            } catch (IOException e) {
                e.printStackTrace();
            }
            catch(Exception e){
                Toast.makeText(Login.this,"에러가 발생했습니다.",Toast.LENGTH_SHORT).show();
            }
            return result;
        }

    }
}

