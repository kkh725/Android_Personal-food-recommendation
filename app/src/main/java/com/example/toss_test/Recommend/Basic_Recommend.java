package com.example.toss_test.Recommend;

import static com.example.toss_test.Recommend.Detail_Recommend.Store_Status;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;

import com.example.toss_test.R;
import com.example.toss_test.etc.OkhttpClient_Get;
import com.example.toss_test.fragment.AllMenu_Fragment;
import com.example.toss_test.fragment.My_Fragment;
import com.example.toss_test.fragment.Recommend_Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class Basic_Recommend extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_recommend);

        /**
         * 앱이 재가동 / 화면이 다시 켜질시 혼잡도가 초기화됨.
         * 어떻게 해야될까?
         */
        Store_Status = new HashMap<String, String>();
        Store_Status.put("BBQ치킨 조치원점 (Express)", "0");
        Store_Status.put("BHC치킨 조치원점", "0");
        Store_Status.put("지코바치킨 조치원점", "0");
        Store_Status.put("store4", "0");
        Store_Status.put("store5", "0");
        Log.d("hash data",Store_Status.values().toString());
        Log.d("hash data",Store_Status.keySet().toString());

        /**
         * 1차로 이제 많이먹은 음식종류 탑5 가져오는과정 사용

        OkhttpClient_Get okhttpClient_get = new OkhttpClient_Get();
        try {
            String result = okhttpClient_get.execute
                    ("http://3.39.230.197:8000/recom/wjsjejas-12fs-gw2f-1sdf-2jskkkwlak20").get();
            Log.d("api result",result);
            String menu = result.split("\"")[1];
            String first = menu.split(",")[0];
            String second = menu.split(",")[1];
            String third = menu.split(",")[2];
            Log.d("result 1 menu",first);
            Log.d("result 2 menu",second);
            Log.d("result 3 menu",third);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        getSupportFragmentManager().beginTransaction().replace(R.id.Container, new Recommend_Fragment()).commit(); // FrameLayout에 fragment.xml 띄우기

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    // item을 클릭시 id값을 가져와 FrameLayout에 fragment.xml 띄우기
                    case R.id.itme1:
                        getSupportFragmentManager().beginTransaction().replace(R.id.Container, new Recommend_Fragment()).commit();
                        break;
                    case R.id.itme2:
                        getSupportFragmentManager().beginTransaction().replace(R.id.Container, new AllMenu_Fragment()).commit();
                        break;
                    case R.id.itme3:
                        getSupportFragmentManager().beginTransaction().replace(R.id.Container, new My_Fragment()).commit();
                        break;
                }
                return true;
            }
        });
    }
}