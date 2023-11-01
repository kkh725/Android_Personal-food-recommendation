package com.example.toss_test;

import static com.example.toss_test.Login.Login.Member_key;
import static com.example.toss_test.MainActivity.OrderNo;
import static com.example.toss_test.Login.Login.Status;
import static com.example.toss_test.MainActivity.price;
import static com.example.toss_test.MainActivity.store;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.toss_test.Pay_Result.Failure;
import com.example.toss_test.Pay_Result.Successful;
import com.example.toss_test.Recommend.Basic_Recommend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;

/**
 * 결제 완료 후 데이터를
 * 데이터베이스에 보내는 화면
 * 가게명 음식명 가격 시간 등등 저장
 */

public class MainActivity2 extends AppCompatActivity {
    ProgressBar progressBar;
    String Type,Day,Date,FormatedTime;

    /**
     * type 을 좀 제대로 설정해주어야할듯.
     * @param savedInstanceState
     */


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        Intent success = new Intent(this, Successful.class);
        Intent fail = new Intent(this, Failure.class);

        Button btn = findViewById(R.id.button);

        TextView tv2 = findViewById(R.id.Tv_Status);
        tv2.setText(Status);

        TextView tv_menu = findViewById(R.id.Tv_Menu);
        tv_menu.setText(MainActivity.menu);

        TextView tv4 = findViewById(R.id.tv_amount);
        tv4.setText(price);
        TextView tv_time = findViewById(R.id.tv_time);

        TextView Et_Store = findViewById(R.id.text_Store);
        Et_Store.setText(store);
        TextView Et_Type = findViewById(R.id.text_Type);
        Et_Type.setText("typetypetype");


/**
 * 오늘의 날짜 구하고 그 날짜의 요일을 캘린더에서 찾아서 출력하기.
 * + 요일과 현재 (결제) 시간을 구해서 출력한다.
 *
 */
        //오늘 날짜 구하기.
        Date today = new Date();
        SimpleDateFormat date = new SimpleDateFormat("yyyy/MM/dd");
        Date = date.format(today); //todayStr ---->> 오늘의 날짜.

        String[] week = {"일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"};

        //위의 현재 날짜에 요일 구하기.
        Calendar cal = Calendar.getInstance();
        // 위에서 구한 날짜 정보를 넣어줌
        cal.setTime(today);
        int num = cal.get(Calendar.DAY_OF_WEEK)-1;
        Day = week[num]; // today2는 오늘의 요일.

        // 현재 시간
        LocalTime now = LocalTime.now();
        System.out.println(now);  // 06:20:57.008731300
        tv_time.setText(now.toString());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH");
        FormatedTime = now.format(formatter); // formatedNow --------->>> 6시면 6 출력
        // --시

        /**
         * 끝
         */

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                store = String.valueOf(Et_Store.getText());
                Type = String.valueOf(Et_Type.getText());

                /**
                 * 이 창은 이미 결제완료가 뜬 후에만 들어올 수 있는 창이므로 연결 ㄴㄴ
                 */

                            try {
                                String Insert_DB = new Toss_to_DB().execute(String.valueOf(OrderNo),Status,store,Type
                                        ,MainActivity.menu,price,Date,FormatedTime,Day, Member_key).get(); //jsp를 통해 디비에 값 보내고 받아올 수 있음.
                                Log.d("day",Day);
                                Log.d("member",Member_key);
                                Toast.makeText(MainActivity2.this,"데이터베이스에 저장완료 !", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MainActivity2.this, Basic_Recommend.class);
                                startActivity(intent);
                                finish();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    });


    }

    private class Toss_to_DB extends AsyncTask<String, Void, String>{

        String sendMsg, receiveMsg;

        @Override
        protected void onPreExecute() {
//            super.onPreExecute();
//            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                String str;

                // 접속할 서버 주소 (이클립스에서 android.jsp 실행시 웹브라우저 주소)

                URL url = new URL(
                        "http://221.158.178.99:8081/DBMS_COnnection/android/Insert_Test.jsp");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream(),StandardCharsets.UTF_8);

                sendMsg = "OrderNo=" + strings[0] + "&Status=" + strings[1] + "&Store=" + strings[2]+"&Type_of_food="
                        + strings[3] + "&Menu=" +strings[4] + "&Amount=" + strings[5]+"&Date=" + strings[6] + "&FormatedTime=" +strings[7]
                        + "&Day=" + strings[8] + "&Member_Key="+ strings[9];


                osw.write(sendMsg);
                osw.flush();

                //jsp와 통신 성공 시 수행
                if (conn.getResponseCode() == conn.HTTP_OK) { //jsp파일에 있는 문자를 읽어온다.
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "utf-8");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuffer buffer = new StringBuffer();
                    Log.d("23","잘됨22");
                    // jsp에서 보낸 값을 받는 부분
                    while ((str = reader.readLine()) != null) {
                        buffer.append(str);
                    }
                    receiveMsg = buffer.toString();
                } else {
                    Log.d("1","오류");
                    // 통신 실패
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //jsp로부터 받은 리턴 값
            return receiveMsg;


        }

    }
}