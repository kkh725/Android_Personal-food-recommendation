package com.example.toss_test.Login;

import static com.example.toss_test.fragment.Recommend_Fragment.Menu_arr;
import static com.example.toss_test.fragment.Recommend_Fragment.Store_arr;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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
    private String Id, Pw, Check, Url;
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
                     * 로그인 후 서버로 멤버키를 보내서 다섯가지 추천을 받아온다.
                     * 다섯가지 추천 받아오는 칸.
                     * 추천한다는 화면 띄워줌
                     */
                    Url = "http://3.39.230.197:8000/re/"+Member_key;
                    Get_Recommend_Thread get_recommend_thread = new Get_Recommend_Thread(new Handler());
                    get_recommend_thread.showLoadingDialog();
                    get_recommend_thread.start();

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

    /**
     * 아이디 비밀번호 체크하고
     * 데이터베이스 확인 후 로그인시키기. true값이면 로그인
     */
    private class Check_Database extends AsyncTask<String, Void, String> {

        OkHttpClient client = new OkHttpClient();
        @Override
        protected String doInBackground(String... params) {
            String result = null;

            RequestBody formBody = new FormBody.Builder()
                    .add("Id", params[0])
                    .add("Pw",params[1])
                    .build();

            Request request = new Request.Builder()
                    .url("http://221.158.178.99:8081/DBMS_COnnection/android/Login.jsp")
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

    /**
     * 로그인 한 후 멤버키값 보내고
     * 5가지 추천 받아오기
     */
    private class Get_Recommend_Thread extends Thread{
        Handler handler;
        AlertDialog customLoadingDialog;
        String result;

        public Get_Recommend_Thread (Handler handler){
            this.handler = handler;
        }

        @Override
        public void run(){
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(Url)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                result = response.body().string();
                String parse[] = result.split(",");
                Log.d("추천 결과",parse[0]);
                /**
                 * 추천받아 들어온 다섯가지 가게와 메뉴를
                 * 가게/메뉴로 분리해서 배열에 넣는다. ,으로 한번자르고 :으로 두번잘라서 파싱함
                 * trim()메서드는 문자열 앞뒤의 공백 지우는 메서드
                 */

                for (int i = 0; i < parse.length; i++) {
                    String[] parts = parse[i].split(":");
                    if (parts.length == 2) {
                        Store_arr[i] = parts[0].trim();
                        Menu_arr[i] = parts[1].trim();
                        if (Store_arr[i].contains("\"")) {
                            Store_arr[i]=Store_arr[i].replace("\"","");
                        }
                        if(Menu_arr[i].contains("\"")){
                            Menu_arr[i]=Menu_arr[i].replace("\"","");
                        }
                        Log.d("추천2","추천" + Store_arr[i]+"///"+Menu_arr[i]);
                    }
                }



            } catch (IOException e) {
                e.printStackTrace();
            }
            /**
             * 백그라운드에서 수행할 작업. 비동기
             */
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            runOnUiThread(new Runnable() {
                /**
                 * ui스레드에서 작업할 내용 작성.
                 * runOnUIThread 대신에 handler.post(new runnable){}도 사용 가능하다.
                 * runonui는 ui 스레드에서 변경될점 위주로 사용. handler는 데이터이동같은 부분에서 사용한다.
                 */
                @Override
                public void run() {
                    hideLoadingDialog();
                }
            });

        }

        private void showLoadingDialog(){
            /**
             * 작업이 시작되기 직전 ui 변경점.
             */
            View loading = getLayoutInflater().inflate(R.layout.loading,null);

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Login.this);
            dialogBuilder.setView(loading);
            customLoadingDialog = dialogBuilder.create();
            customLoadingDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            customLoadingDialog.show(); // 로딩 시작
        }

        private void hideLoadingDialog(){
            /**
             * 작업이 완료된 후 ui 변경점.
             */
            customLoadingDialog.dismiss(); // 로딩 완료
            Intent intent_toOrder = new Intent(Login.this, Basic_Recommend.class);
            startActivity(intent_toOrder);
            Toast.makeText(Login.this, "로그인"+Member_key, Toast.LENGTH_SHORT).show();
        }
    }
}

