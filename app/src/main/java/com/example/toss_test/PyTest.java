package com.example.toss_test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class PyTest extends AppCompatActivity {

    List<String> test = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_py_test);


        try {
            String result = new FileReadTask().execute().get();
            Log.d("test2",result);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

//        Log.d("test2","filalllldldldll");
//        ProcessBuilder pb = new ProcessBuilder("/Users/kkh/opt/anaconda3/bin/python","/Users/kkh/Desktop/main.py");
//        try {
//
//            Log.d("test2","success1");
//            Process p = pb.start();
//            Log.d("test2","success1.5");
//            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), "utf-8"));
//
//            Log.d("test2","success2");
//            String line = "";
//            while ((line = br.readLine()) != null) {
//                Log.d("tag2233", line);
//                test.add(line);
//            }
//
//            Log.d("test2","success3");
//            String result = test.toString();
//            Log.d("test", result);
//            Log.d("test2","success4");
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//            Log.d("test2","fail");
//        }

    }

    private class FileReadTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            StringBuilder content = new StringBuilder();

            try {
                AssetManager assetManager = getAssets();
                InputStream inputStream = assetManager.open("assets/main.txt"); // 파일명을 실제 파일명으로 변경
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = br.readLine()) != null) {
                    content.append(line).append("\n");
                }
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return content.toString();
        }

    }

    private class MyTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {


            // 백그라운드에서 ProcessBuilder 실행
            Log.d("test2","filalllldldldll");
            ProcessBuilder pb = new ProcessBuilder("/Users/kkh/opt/anaconda3/bin/python3.9","/Users/kkh/main.py ");
            try {

                Log.d("test2","success1");
                Process p = pb.start();
                Log.d("test2","success1.5");
                BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), "utf-8"));

                Log.d("test2","success2");
                String line = "";
                while ((line = br.readLine()) != null) {
                    Log.d("tag2233", line);
                    test.add(line);
                }

                Log.d("test2","success3");
                String result = test.toString();
                Log.d("test", result);
                Log.d("test2","success4");
                return result;
            }
            catch (IOException e) {
                e.printStackTrace();
                Log.d("test2","fail");
            }
            return null;
        }
    }
}