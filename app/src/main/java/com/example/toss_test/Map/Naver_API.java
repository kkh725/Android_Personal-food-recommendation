package com.example.toss_test.Map;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Naver_API {

    String apiUrl;

    public Object[] geocode(String location) {
        String x = "널";
        String y = "널";

        /**
         * geocoding location 주소 -> 좌표로 교환
         */

        try {
            apiUrl = "https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode?" +
                    "query=" + URLEncoder.encode(location, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        HttpAsncTask httpAsncTask2 = new HttpAsncTask();
        try {
            String result = httpAsncTask2.execute(apiUrl).get();
            Log.d("좌표변환기 ", result);
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("addresses");
            JSONObject jsonObject1 = new JSONObject(jsonArray.getString(0));
            x = jsonObject1.getString("x");
            y = jsonObject1.getString("y");
            Log.d("경도 // 위도 ", x + " " + y);

            return new Object[] {x,y};

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new Object[] {x,y};
    }


    /**
     * 시작 좌표 -> 도착 좌표의 걸리는 시간 및 km 찾기.
     */
    public Object[] duration_distance(String startLocation, String goalLocation) {
        startLocation = "127.726137, 37.864515"; // 출발점
         //도착점
        try {
            apiUrl = "https://naveropenapi.apigw.ntruss.com/map-direction/v1/driving?" +
                    "start=" + URLEncoder.encode(startLocation, "UTF-8") + "&" +
                    "goal=" + URLEncoder.encode(goalLocation, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        HttpAsncTask httpAsncTask = new HttpAsncTask();
        try {

            String result = httpAsncTask.execute(apiUrl).get();
            Log.d("result", result);
            JSONObject jsonObject = new JSONObject(result);

            String route = jsonObject.getString("route");
            Log.d("경로 / 1번 파싱", route);

            JSONObject jsonObject1 = new JSONObject(route);
            JSONArray jsonArray = jsonObject1.getJSONArray("traoptimal");
            Log.d("최적의 경로 / 2번파싱", jsonArray.getString(0));
            /**
             * 얘는 [] 배열형태로 분류되어있어서 배열로 파싱함.
             */

            JSONObject jsonObject2 = new JSONObject(jsonArray.getString(0));
            String summary = jsonObject2.getString("summary");
            Log.d("요약 / 3번파싱", summary);

            JSONObject jsonObject3 = new JSONObject(summary);
            int duration = jsonObject3.getInt("duration");
            Log.d("소요시간 / 4번파싱 (min)", String.valueOf((duration / 1000) / 60) + " min");

            double distance = jsonObject3.getDouble("distance");
            Log.d("총 거리 / 4-2파싱 (km)", String.valueOf(distance / 1000) + " km");

            return new Object[] {((duration / 1000) / 60)+"분", (distance / 1000)+"km"};


        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new Object[0];
    }

    /**
     * 세종에 있는 음식점 리스트 받아오는 api
     * @param Reduce_Location
     * @return
     */

    public Object[] find_sejong_restaurant(String Reduce_Location) {
        String[] Array_Restaurant;
        List<String> List_Restaurant;


            List_Restaurant = new ArrayList<String>();
        Array_Restaurant = List_Restaurant.toArray(new String[List_Restaurant.size()]);

            String Url = "https://api.odcloud.kr/api/15081905/" +
                    "v1/uddi:f6c71932-d3a2-4a42-a990-abff225c72bc?page=" +
                    "1&perPage=4000&serviceKey=y5hNTYWGplxUEniQbNWy3WhaYtRXCvqfNXkRvbKbg" +
                    "DA1wuW2Ey9NOoNu%2FJ77S%2Byl4bFluNSRZWqTR7v3Mzu%2BuA%3D%3D";


            HttpAsncTask httpAsncTask = new HttpAsncTask();
            try {
                String result = httpAsncTask.execute(Url).get();
//                Log.d("result222", result);

                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("data");
//                String str = jsonArray.getString(0);
//                Log.d("result 33", str);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject_data = jsonArray.getJSONObject(i);
                    String result2 = jsonObject_data.getString("소재지(지번)");
                    if (result2.contains(Reduce_Location) && result2 != null) {
                        List_Restaurant.add(result2);
                    }
                    /**
                     * 조치원읍 신안리 음식점 검색. 후에 gecoding활용해서 좌표로 변환 후
                     *
                     */

                }
                Array_Restaurant = List_Restaurant.toArray(Array_Restaurant);
                for (int i = 0; i < Array_Restaurant.length; i++) {
//                    Log.d("ssdaf", Array_Restaurant[i] + "///" + String.valueOf(i));
                }
                return Array_Restaurant;


            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        return List_Restaurant.toArray();
    }
    }



class HttpAsncTask extends AsyncTask<String, Void, String> {

    OkHttpClient client = new OkHttpClient();

    @Override
    protected String doInBackground(String... params) {  //String... 이란 문자열값이 한개 이상 return 될 수 있다는 의미. >params가 "1" 일수도 문자열의 배열일수도있음.
        String result = null;
        String strUrl = params[0];

        Request request = new Request.Builder().url(strUrl)
                .header("X-NCP-APIGW-API-KEY-ID", "o8rq2w6km0")
                .addHeader("X-NCP-APIGW-API-KEY", "TsP3Sd0YBx4pGAX20ILGol7AZqbKAVof3ABHET7z")
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

class HttpAsncTask2_get extends AsyncTask<String, Void, String> {

    OkHttpClient client = new OkHttpClient();
    @Override
    protected String doInBackground(String... params) {  //String... 이란 문자열값이 한개 이상 return 될 수 있다는 의미. >params가 "1" 일수도 문자열의 배열일수도있음.
        String result = null;
        String strUrl = params[0];

        Request request = new Request.Builder().url(strUrl)
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




