package com.example.toss_test.Login;

import androidx.appcompat.app.AppCompatActivity;
import com.example.toss_test.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Register_IdPw extends AppCompatActivity {

    public static String Id, Pw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_id_pw);

        Button btn_next = findViewById(R.id.btn_next);

        EditText Et_id = findViewById(R.id.Et_id2);
        EditText Et_pw = findViewById(R.id.Et_pw2);

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Id = String.valueOf(Et_id.getText());
                Pw = String.valueOf(Et_pw.getText());

                Intent toRegister = new Intent(Register_IdPw.this,Register.class);
                startActivity(toRegister);
            }
        });
    }
}