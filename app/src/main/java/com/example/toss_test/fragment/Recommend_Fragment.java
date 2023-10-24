package com.example.toss_test.fragment;

import static com.example.toss_test.MainActivity.price;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.toss_test.List.ListViewAdapter;
import com.example.toss_test.List.ListViewItem;
import com.example.toss_test.MainActivity;
import com.example.toss_test.Map.Gps;
import com.example.toss_test.Map.Naver_API;
import com.example.toss_test.R;
import com.example.toss_test.etc.HttpWebSocket;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Recommend_Fragment extends Fragment {

    TextView tv_detail;
    TextView tv_address;
    public static ListViewAdapter listViewAdapter;
    public static ListView listView;
    String store, congestion, duration;
    public static String to_home_text;
    Button btn_recycle;
    public static HashMap<String, String> Store_Status = new HashMap<String, String>(); //가게들의 실시간 혼잡도를 받아오는 hashmap.
    public static String[] Store_arr = new String[5]; // api를 통해 받아올 가게 빈도수 5가지
    public static String[] Menu_arr = new String[5]; // api를 통해 받아올 메뉴 빈도수 5가지

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommend_, container, false);

        tv_address = view.findViewById(R.id.tv_address);
        tv_address.setText("세종특별자치시 조치원읍 신안리 354-2");

        /**
         * 로그인 했을때 데이터베이스에서 뽑아온 사용자의 주소를
         * x,y 좌표로 변환하기.
         */
        Naver_API naver_api = new Naver_API();
        String home_location = naver_api.geocode(tv_address.getText().toString());
        Log.d("도착지점 좌표",home_location);

        /**
         * 사용자의 현재 location 사용하기.
         * 지금은 내 집의 위치가 나오므로 잠시 테스트용 코드수정
         *
         Gps gps = new Gps((AppCompatActivity) getContext());
         gps.requestLocationUpdates();
         String start_location = gps.getLongitude()  + ", " + gps.getLatitude();
         Log.d("실시간 현재좌표",start_location);
         */

        // 실시간 위치에서부터 집까지의 시간 계산하기.

        String start_location = "127.2646, 36.4851"; //어딘지 모르겠음
        to_home_text = naver_api.duration_distance(start_location,home_location)[0].toString();
        Log.d("거리 시간",to_home_text);


//      to_home_text = naver_api.duration_distance();

        HttpWebSocket httpWebSocket = new HttpWebSocket();
        httpWebSocket.sendWebSocketMessage("consumer");

        /**
         jsp 와 https 통신
         로그인한 멤버키 ,id,pw값으로 데이터베이스의 주소 가져오기.
         ui 가 너무 깨져서 그냥 settext 로 대체한다.
         */
//        tv_address = view.findViewById(R.id.tv_address);
//        try {
//            Log.d("memberkey",Login.Member_key);
//            String address = new Find_Address().execute(Login.Member_key).get();
//            Log.d("memberkey22",address);
//            tv_address.setText(address);
//            tv_address.append("\n 선호하는 음식 1,2,3,4,5");
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }


        /**
         * 이곳에서 빈도수로 받아오기.
         * 탑5 받아오는 부분
         */
        listView = view.findViewById(R.id.list);
        listViewAdapter = new ListViewAdapter();

        for(int i=0; i<5; i++){
            Store_Status.put(Store_arr[i], "혼잡도 : 보통");
            listViewAdapter.addItem(getResources().getDrawable(R.drawable.fluentfood20regular),
                    Store_arr[i]+"\n"+ Menu_arr[i], Store_Status.get(Store_arr[i]));
        }
        listViewAdapter.addItem(getResources().getDrawable(R.drawable.fluentfood20regular),
                "마라순코우 마라탕\n마라샹궈", "혼잡도 : 혼잡");

        listView.setAdapter(listViewAdapter);

        /**
         * 빈도수별 음식종류 추천 후 음식메뉴 추천에서
         * 음식메뉴를 클릭하면, 음식메뉴명, 가격, 설명 등을 putextra 해서 결제창으로 보낸다.
         */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                ListViewItem clickedItem = (ListViewItem) parent.getItemAtPosition(position);
                MainActivity.menu = Menu_arr[position]; //국밥
                Log.d("메뉴명",MainActivity.menu);
                duration = Store_Status.get(Store_arr[position]); //리스트에선 혼잡도 배달 거리 시간 구해오기
                //조리시간 구해와서 혼잡도에 더하기.
                store = Store_arr[position];
                String price = "11900"; //가격은 데이터베이스에서 찾아오기.

                String url = "http://221.158.178.99:8081/DBMS_COnnection/android/Cookingtime.jsp";

                CookingtimeThread cookingtimeThread =
                        new CookingtimeThread(new Handler(),url,Store_arr[position]);
                cookingtimeThread.start();

            }
        });

        return view;
    }

    private class CookingtimeThread extends Thread{
        public Handler handler;
        private String Store_Name, result, Url;
        private String address,Cooking_time;
        private boolean isDone = false;

        public CookingtimeThread(Handler handler,String Url, String Store_Name){
            this.handler = handler;
            this.Store_Name = Store_Name;
            this.Url = Url;
        }

        @Override
        public void run() {
            super.run();

            OkHttpClient okHttpClient = new OkHttpClient();
            RequestBody formBody = new FormBody.Builder()
                    .add("Store_Name", Store_Name)
                    .add("Menu",MainActivity.menu)
                    .build();

            Request request = new Request.Builder().url(Url)
                    .post(formBody)
                    .build();
            try {
                Response response = okHttpClient.newCall(request).execute();
                result = response.body().string();
                Log.d("경도/위도/가격/조리시간 구해오기",result);
                String[] parse = result.split("/");
                address = parse[0];
                Cooking_time = parse[1];
                price = parse[2];
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //작업이 끝나면 메인 ui 에서 실행한다.
                        Intent intent = new Intent(getContext(), MainActivity.class);
                        intent.putExtra("store", store);
                        intent.putExtra("duration", duration); // 혼잡도. 몇분 더 추가할지
                        Log.d("혼잡도 시간",duration);
                        intent.putExtra("address", address);
                        intent.putExtra("Cooking_time", Cooking_time);
                        // 한마디로 배달시간은 쿠킹타임+배달시간+혼잡도 지연시간
                        Log.d("경도위도",Cooking_time+address);
                        /**
                         * 결제 정보로 넘길때 혼잡도, 걸리는시간 메뉴 등 정보 다 넣어두기.
                         */
                        startActivity(intent);
                    }
                });


                /**
                 * 비동기작업이 수행되기때문에 다른 코드들보다 나중에 수행될수 있음.
                 * 변수들이 null인상태로 들어가게되고 그 다음 run돌아가서 값이 들어갈수있다.
                 */
                Log.d("경도/위도/가격/조리시간 구해오기",address);
                // 한마디로 배달시간은 쿠킹타임+배달시간+혼잡도 지연시간

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }


    /**
     * id,pw,memberkey값을 이용한 주소찾기


    private class Find_Address extends AsyncTask<String, Void, String> {

        OkHttpClient client = new OkHttpClient();
        @Override
        protected String doInBackground(String... params) {
            String result = null;

            RequestBody formBody = new FormBody.Builder()
                    .add("Member_Key", params[0])
                    .build();

            Request request = new Request.Builder().url("http://221.158.178.99:8081/DBMS_COnnection/android/Find_Address.jsp")
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
     */
}