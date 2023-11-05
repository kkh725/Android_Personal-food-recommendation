package com.example.toss_test;

import static com.example.toss_test.fragment.Recommend_Fragment.to_home_text;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.example.toss_test.Map.Naver_API;

import java.util.Random;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    TextView tv1,tv_store, tv_menu, tv_price, tv_duration,tv_to_home;
    Button btn;
    String CheckOutUrl,duration,store_address,Cooking_time;
    public static String price,menu,store; // 가격과 메뉴를 지정해주어야할것 @@@@@@@@@@@@@@@@@@
    public static int OrderNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String won = " 원";

        Intent intent = getIntent();
        store = intent.getStringExtra("store");
        duration = intent.getStringExtra("duration");
        if(duration.equals("혼잡도 : 여유")) duration= "10분";
        else if(duration.equals("여유")) duration= "10분";
        else if(duration.equals("혼잡도 : 보통")) duration= "15분";
        else if(duration.equals("보통")) duration= "15분";
        else if(duration.equals("혼잡도 : 혼잡")) duration= "20분";
        else if(duration.equals("혼잡")) duration= "20분";
        else if(duration.equals("혼잡도 : 매우 혼잡")) duration= "25분";
        else if(duration.equals("매우 혼잡")) duration= "25분";
        store_address = intent.getStringExtra("address"); //가게 주소
        Cooking_time = intent.getStringExtra("Cooking_time"); // 조리시간

        tv_menu = findViewById(R.id.tv_menu3);
        tv_store = findViewById(R.id.tv_store);
        tv_price = findViewById(R.id.tv_price);
        tv_duration = findViewById(R.id.tv_duration); //배달 예상 소요시간 = 집에서부터 좌표까지 거리시간+조리시간+혼잡도시간
        tv_to_home = findViewById(R.id.tv_to_home); //귀가하는데 걸리는시간.

        tv_menu.setText(menu);
        tv_store.setText(store);
        tv_price.setText(String.format("%s%s", price, won));
        tv_to_home.setText("약 "+to_home_text);


        Log.d("time",Cooking_time);
        /**
         * 집에서부터 가게까지의 거리 계산.
         */
        Naver_API naver_api = new Naver_API();
        String time = naver_api
                .duration_distance(store_address,"127.286463624484, 36.6160243207095")[0].toString(); //가게에서부터 내집까지. 신안리 354-2
        Log.d("가게 -> 신안리 354-2 소요시간",time);
        Log.d("'착한 돈가스 세트' 조리시간",Cooking_time+"분");
        Log.d("실시간 이백장돈가스 세종조치원점 지연시간",duration);


        /**
         * 집에서부터 가게+
         * 가게에서의 메뉴 조리시간+
         * 가게의 실시간 혼잡도를 다 더한 배달시간 구현.
         * result_time 은 최종 배달시간.
         */
        int i_duration2 = Integer.parseInt(duration.replace("분",""));
        int i_time = Integer.parseInt(time.replace("분",""));
        int i_cooking_time = Integer.parseInt(Cooking_time);
        String result_time = String.valueOf((i_duration2+i_time+i_cooking_time));
        Log.d("최종","가게->집 : "+duration+"/ 혼잡도 지연시간 : "+time+"/ 조리시간 : "+Cooking_time+"분\n최종 배달시간 "+result_time);

        tv_duration.setText(String.format("약 %s", result_time)+"분");

        int i_duration = Integer.parseInt(result_time); // 배달시간 (분 단위)
        int i_home_duration = Integer.parseInt(to_home_text.replaceAll("[^0-9]", "")); // 귀가시간 (분 단위)

        String comparisonText;

        /**
         * 배달시간보다 느리게오는지 빠르게오는지 확인.
         */
        if (i_duration < i_home_duration) {
            int minutesFaster = i_home_duration - i_duration;
            comparisonText = "귀가시간보다 " + minutesFaster + "분 더 빨리 도착해요!";
        } else if (i_duration > i_home_duration) {
            int minutesSlower = i_duration - i_home_duration;
            comparisonText = "귀가시간보다 " + minutesSlower + "분 더 늦게 도착해요!";
        } else {
            comparisonText = "귀가시간과 동일해요!";
        }
        TextView tv_time_guide = findViewById(R.id.tv_time_guide);
        tv_time_guide.setText(comparisonText);

        SpannableStringBuilder spannable = new SpannableStringBuilder(comparisonText);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.RED);
        spannable.setSpan(colorSpan, 7, 10, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_time_guide.setText(spannable);



        //duration ->혼잡도.
        //cookingtime -> 조리시간 합치기.

        btn = findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // 버튼을 클릭하면 토스페이 결제요청이 시작되고, 결제창으로 넘어감.

                String TossPay_result = null;
                try {

                    // 토스페이에 결제요청을 보내고 토스페이의 checkoutpage url을 받아온다.

                    TossPay_result = new TossExam().execute().get();
                    Log.d("TossPay_result",TossPay_result);

                    JSONObject jsonObject = new JSONObject(TossPay_result); // json데이터 파싱하기.
                    CheckOutUrl = jsonObject.getString("checkoutPage"); //토스에 결제요청 보낸 후 checkoutpage url 받아온다.
                    Log.d("TossPay_result",CheckOutUrl);


                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (NullPointerException e){
                    Log.d("결제완료","이미 완료된 결제입니다.");
                }

                Intent intent_payment = new Intent(Intent.ACTION_VIEW, Uri.parse(CheckOutUrl));
                startActivity(intent_payment);

            }
        });
    }

    private class TossExam extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... strings) {

            Random random = new Random();
            OrderNo = random.nextInt(1000000000);
            URL url = null;
            URLConnection connection = null;
            StringBuilder responseBody = new StringBuilder();
            try {
                url = new URL("https://pay.toss.im/api/v2/payments");
                connection = url.openConnection();
                connection.addRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);
                connection.setDoInput(true);

                JSONObject jsonBody = new JSONObject();
                jsonBody.put("orderNo", OrderNo);
                jsonBody.put("amount", price);
                jsonBody.put("amountTaxFree", 0);
                jsonBody.put("productDesc", menu);
                jsonBody.put("apiKey", "sk_test_w5lNQylNqa5lNQe013Nq");
                jsonBody.put("autoExecute", true);
                jsonBody.put("resultCallback", "http://221.158.178.99:8081/DbCon/android/new_jsp.jsp"); //jsp 주소 적어보기.
                jsonBody.put("callbackVersion", "V2");
                jsonBody.put("retUrl", "https://app");
                jsonBody.put("retCancelUrl", "http://221.158.178.99:8081/DbCon/android/ay.jsp");

                BufferedOutputStream bos = new BufferedOutputStream(connection.getOutputStream());

                bos.write(jsonBody.toString().getBytes(StandardCharsets.UTF_8));
                bos.flush();
                bos.close();


                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                String line = null;
                while ((line = br.readLine()) != null) {
                    responseBody.append(line);
                }
                br.close();
            } catch (Exception e) {
                responseBody.append(e);
            }
            String responsebody = responseBody.toString();
            Log.d("responsebody",responsebody);
            return responsebody;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

        }
    }



}