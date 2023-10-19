package com.example.toss_test.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.toss_test.R;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Register extends AppCompatActivity {

    String[] items = {"10대","20대","30대","40대","50대","60대","70대","80대"};
    String Name, Age, Sex, Resisdence, Member_Key , Address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Spinner spinner_age = findViewById(R.id.Spinner_age);
        ArrayAdapter AgeAdapter = ArrayAdapter.createFromResource(this,R.array.age, android.R.layout.simple_spinner_item);

        AgeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner_age.setAdapter(AgeAdapter);

        spinner_age.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Age = items[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button btn_complete = findViewById(R.id.btn_complete);

        EditText Et_name = findViewById(R.id.Et_name);
        EditText Et_Resisdence = findViewById(R.id.et_residence);
        EditText Et_address = findViewById(R.id.et_address);

        RadioButton btn_male = findViewById(R.id.radio_male);
        RadioButton btn_female = findViewById(R.id.radio_female);

        RadioGroup.OnCheckedChangeListener Check = new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (group.getId() == R.id.radioGroup){
                    switch (checkedId){
                        case R.id.radio_male:
                            Sex = String.valueOf(btn_male.getText());
                            break;
                        case R.id.radio_female:
                            Sex = String.valueOf(btn_female.getText());
                            break;
                    }
                }
            }
        };

        RadioGroup Radio_Sex = findViewById(R.id.radioGroup);
        Radio_Sex.setOnCheckedChangeListener(Check);


        btn_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Member_Key = UUID.randomUUID().toString();
               Name = String.valueOf(Et_name.getText());
               Resisdence = String.valueOf(Et_Resisdence.getText());
               Address = String.valueOf(Et_address.getText());

               new Member_to_DB2().execute(Member_Key,Register_IdPw.Id,Register_IdPw.Pw,
                       Name,Age,Sex,Resisdence,Address);

               // 뭐 중복이나 안되는거 있는지 체크해보고 로그인 화면으로 넘겨야한다.
                Intent to_login = new Intent(Register.this,Login.class);
                startActivity(to_login);
                Toast.makeText(Register.this, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private class Member_to_DB2 extends AsyncTask<String, Void, String> {

        OkHttpClient client = new OkHttpClient();
        @Override
        protected String doInBackground(String... params) {
            String result = null;

            RequestBody formBody = new FormBody.Builder()
                    .add("Member_Key", params[0])
                    .add("Id",params[1])
                    .add("Pw",params[2])
                    .add("Name",params[3])
                    .add("Age",params[4])
                    .add("Sex",params[5])
                    .add("Resisdence",params[6])
                    .add("Address",params[7])
                    .build();

            Request request = new Request.Builder().url("http://221.158.178.99:8081/DBMS_COnnection/android/Insert_Member.jsp")
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

}




