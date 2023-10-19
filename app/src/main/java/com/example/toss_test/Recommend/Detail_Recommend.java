package com.example.toss_test.Recommend;


import static com.example.toss_test.Recommend.Detail_Recommend.Store_Status;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.toss_test.List.ListViewAdapter;
import com.example.toss_test.List.ListViewItem;
import com.example.toss_test.MainActivity;
import com.example.toss_test.R;
import com.example.toss_test.etc.HttpWebSocket;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.HashMap;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class Detail_Recommend extends AppCompatActivity {
    TextView tv_detail;
    public static ListViewAdapter listViewAdapter;
    public static ListView listView;
    String store, menu, congestion, duration;
    Button btn_recycle;
    public static HashMap<String, String> Store_Status = new HashMap<String, String>();
    public static String[] Store_arr = new String[5]; // api를 통해 받아올 가게 빈도수 5가지
    public static String[] Menu_arr = new String[5]; // api를 통해 받아올 메뉴 빈도수 5가지

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /**
         * rest api를 통해서
         * 빈도수 2를 구해온다. 가게로 자르고, 음식메뉴명으로 자른다.
         */
        Store_arr = new String[] {"BBQ치킨 조치원점 (Express)","BHC치킨 조치원점","지코바치킨 조치원점","store4"};

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_recommend);

        /** 리사이클러 뷰 쓰면 좋겠지만 귀찮은 관계로 버튼누르면 화면 재시작.
         *
         */
        btn_recycle = findViewById(R.id.btn_recycle);
        btn_recycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                finish(); // 현재 엑티비티 종료
                startActivity(intent); // 현재 엑티비티 재시작
            }
        });


        HttpWebSocket httpWebSocket = new HttpWebSocket();
        httpWebSocket.sendWebSocketMessage("consumer");

        Intent intent = getIntent();
        String type_food = intent.getStringExtra("type_food");

        tv_detail = findViewById(R.id.tv_detail);
        tv_detail.setText(type_food);

        listView = findViewById(R.id.detail_list);
        listViewAdapter = new ListViewAdapter();
        listViewAdapter.addItem(getResources().getDrawable(R.drawable.ic_baseline_restaurant_24), Store_arr[0], "혼잡도 : " + Store_Status.get(Store_arr[0]));
        listViewAdapter.addItem(getResources().getDrawable(R.drawable.ic_baseline_restaurant_24), Store_arr[1], Store_Status.get(Store_arr[1]));
        listViewAdapter.addItem(getResources().getDrawable(R.drawable.ic_baseline_restaurant_24), Store_arr[2], Store_Status.get(Store_arr[2]));
        listViewAdapter.addItem(getResources().getDrawable(R.drawable.ic_baseline_restaurant_24), Store_arr[3], Store_Status.get(Store_arr[3]));

        listView.setAdapter(listViewAdapter);

        /**
         * 빈도수별 음식종류 추천 후 음식메뉴 추천에서
         * 음식메뉴를 클릭하면, 음식메뉴명, 가격, 설명 등을 putextra 해서 결제창으로 보낸다.
         */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Detail_Recommend.this, MainActivity.class);

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
    }


}