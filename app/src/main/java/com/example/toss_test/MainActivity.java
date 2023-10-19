package com.example.toss_test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.example.toss_test.Recommend.Detail_Recommend;

import java.util.Random;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    TextView tv1,tv_store, tv_menu, tv_price, tv_duration;
    Button btn;
    String CheckOutUrl,duration;
    public static String price,menu,store; // 가격과 메뉴를 지정해주어야할것 @@@@@@@@@@@@@@@@@@
    public static int OrderNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        store = intent.getStringExtra("store");
        menu = intent.getStringExtra("menu");
        duration = intent.getStringExtra("duration");
        price = intent.getStringExtra("price");

        tv_store = findViewById(R.id.tv_store);
        tv_menu = findViewById(R.id.tv_menu);
        tv_price = findViewById(R.id.tv_price);
        tv_duration = findViewById(R.id.tv_duration);

        tv_store.setText(store);
        tv_menu.setText(menu);
        tv_price.setText(price);
        tv_duration.setText(duration);


        btn = findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // 버튼을 클릭하면 토스페이 결제요청이 시작되고, 결제창으로 넘어감.

                String TossPay_result = null;
                try {
                    tv1 = findViewById(R.id.tv1);

                    // 토스페이에 결제요청을 보내고 토스페이의 checkoutpage url을 받아온다.

                    TossPay_result = new TossExam().execute().get();
                    Log.d("TossPay_result",TossPay_result);

                    JSONObject jsonObject = new JSONObject(TossPay_result); // json데이터 파싱하기.
                    CheckOutUrl = jsonObject.getString("checkoutPage"); //토스에 결제요청 보낸 후 checkoutpage url 받아온다.
                    tv1.setText(CheckOutUrl);
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