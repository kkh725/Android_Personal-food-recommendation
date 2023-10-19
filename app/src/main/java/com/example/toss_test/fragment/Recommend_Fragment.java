package com.example.toss_test.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

import com.example.toss_test.List.ListViewAdapter;
import com.example.toss_test.List.ListViewItem;
import com.example.toss_test.Login.Login;
import com.example.toss_test.MainActivity;
import com.example.toss_test.R;
import com.example.toss_test.Recommend.Detail_Recommend;
import com.example.toss_test.etc.Check_Address;
import com.example.toss_test.etc.HttpWebSocket;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

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
    String store, menu, congestion, duration;
    Button btn_recycle;
    public static HashMap<String, String> Store_Status = new HashMap<String, String>();
    public static String[] Store_arr = new String[5]; // api를 통해 받아올 가게 빈도수 5가지
    public static String[] Menu_arr = new String[5]; // api를 통해 받아올 메뉴 빈도수 5가지

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommend_, container, false);

        Store_arr = new String[] {"BBQ치킨 조치원점 (Express)","BHC치킨 조치원점","지코바치킨 조치원점","store4","store5","store6"};
        Store_Status.put("BBQ치킨 조치원점 (Express)","0");
        Store_Status.put("BHC치킨 조치원점","0");
        Store_Status.put("지코바치킨 조치원점","0");
        Store_Status.put("store4","0");
        Store_Status.put("store5","0");
        Store_Status.put("store6","0");

        tv_address = view.findViewById(R.id.tv_address);
        tv_address.setText("세종특별자치시 조치원읍 신안리 354-2");


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

        listViewAdapter = new ListViewAdapter();

//        for(int i=0; i<5; i++){
//            listViewAdapter.addItem(getResources().getDrawable(R.drawable.ic_baseline_restaurant_24),
//                    "가게 "+String.valueOf(i)+"번","음식 "+String.valueOf(i)+"번");
//        }
        listView = view.findViewById(R.id.list);
        listViewAdapter = new ListViewAdapter();
        listViewAdapter.addItem(getResources().getDrawable(R.drawable.ic_baseline_restaurant_24), Store_arr[0], "혼잡도 : " + Store_Status.get(Store_arr[0]));
        listViewAdapter.addItem(getResources().getDrawable(R.drawable.ic_baseline_restaurant_24), Store_arr[1], "혼잡도 : " + Store_Status.get(Store_arr[1]));
        listViewAdapter.addItem(getResources().getDrawable(R.drawable.ic_baseline_restaurant_24), Store_arr[2], "혼잡도 : " + Store_Status.get(Store_arr[2]));
        listViewAdapter.addItem(getResources().getDrawable(R.drawable.ic_baseline_restaurant_24), Store_arr[3], "혼잡도 : " + Store_Status.get(Store_arr[3]));
        listViewAdapter.addItem(getResources().getDrawable(R.drawable.ic_baseline_restaurant_24), Store_arr[4], "혼잡도 : " + Store_Status.get(Store_arr[4]));
        listViewAdapter.addItem(getResources().getDrawable(R.drawable.ic_baseline_restaurant_24), Store_arr[5], "혼잡도 : " + Store_Status.get(Store_arr[5]));

        listView.setAdapter(listViewAdapter);

        /**
         * 빈도수별 음식종류 추천 후 음식메뉴 추천에서
         * 음식메뉴를 클릭하면, 음식메뉴명, 가격, 설명 등을 putextra 해서 결제창으로 보낸다.
         */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), MainActivity.class);

                ListViewItem clickedItem = (ListViewItem) parent.getItemAtPosition(position);
                menu = clickedItem.getTitle(); //국밥
                duration = clickedItem.getDesc(); //리스트에선 혼잡도
                duration = "소요시간 40분";
                String price = "11900"; //가격은 데이터베이스에서 찾아오기.

                intent.putExtra("store", store);
                intent.putExtra("menu", menu);
                intent.putExtra("duration", duration);
                intent.putExtra("price", price);
                /**
                 * 결제 정보로 넘길때 혼잡도, 걸리는시간 메뉴 등 정보 다 넣어두기.
                 */
                startActivity(intent);
            }
        });

        return view;
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