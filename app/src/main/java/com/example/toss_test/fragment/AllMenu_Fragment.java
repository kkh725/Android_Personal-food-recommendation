package com.example.toss_test.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

import com.example.toss_test.List.ListViewAdapter;
import com.example.toss_test.List.ListViewItem;
import com.example.toss_test.R;

public class AllMenu_Fragment extends Fragment {
    ListViewAdapter listViewAdapter;


    public AllMenu_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_allmenu, container, false);

        // ListView 초기화
        ListView listView = view.findViewById(R.id.listview1);

        listViewAdapter = new ListViewAdapter();
        listViewAdapter.addItem(getResources().getDrawable(R.drawable.ic_baseline_restaurant_24), "가게 이름 1", "음식 이름 1");
        listViewAdapter.addItem(getResources().getDrawable(R.drawable.ic_baseline_restaurant_24), "가게 이름 2", "음식 이름 2");
        listViewAdapter.addItem(getResources().getDrawable(R.drawable.ic_baseline_restaurant_24), "가게 이름 3", "음식 이름 3");
        listViewAdapter.addItem(getResources().getDrawable(R.drawable.ic_baseline_restaurant_24), "가게 이름 4", "음식 이름 4");
        listViewAdapter.addItem(getResources().getDrawable(R.drawable.ic_baseline_restaurant_24), "가게 이름 5", "음식 이름 5");
        listViewAdapter.addItem(getResources().getDrawable(R.drawable.ic_baseline_restaurant_24), "가게 이름 6", "음식 이름 6");
        listViewAdapter.addItem(getResources().getDrawable(R.drawable.ic_baseline_restaurant_24), "가게 이름 7", "음식 이름 7");
        listViewAdapter.addItem(getResources().getDrawable(R.drawable.ic_baseline_restaurant_24), "가게 이름 8", "음식 이름 8");
        listView.setAdapter(listViewAdapter);


//        // ArrayAdapter를 사용하여 데이터와 ListView 연결
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, menuItems);
//        listView.setAdapter(adapter);

        // ListView 항목 클릭 이벤트 처리
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 클릭한 항목의 위치(position)을 가져올 수 있습니다.
                // 여기에서 원하는 작업을 수행하세요.

                // 예를 들어, 클릭한 항목의 데이터를 가져오려면 다음과 같이 사용할 수 있습니다:
                ListViewItem clickedItem = (ListViewItem) parent.getItemAtPosition(position);
                String title = clickedItem.getTitle();
                String desc = clickedItem.getDesc();


                Toast.makeText(getContext().getApplicationContext(), title + desc, Toast.LENGTH_LONG).show();
                // 클릭한 항목의 데이터를 사용하여 원하는 작업을 수행하세요.
            }
        });
        return view;
    }}
