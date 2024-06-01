package com.mirea.kt.ribo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnStartActivity = findViewById(R.id.btnStartActivity);
        btnStartActivity.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, MainWindow.class);
        //Хранение логина и пароля
        EditText etLogin = findViewById(R.id.login);
        EditText etPassword = findViewById(R.id.password);
        String login = etLogin.getText().toString();
        String password = etPassword.getText().toString();
        //Выгрузка с сайта:
        String server = "https://android-for-students.ru/";
        String serverPath = "/coursework/login.php\n";
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("lgn",login);
        hashMap.put("pwd",password);
        hashMap.put("g","RIBO-05-22");
        HTTPRunnable httpRunnable = new HTTPRunnable(server+serverPath, hashMap);
        Thread th = new Thread(httpRunnable);
        th.start();
        try {
            th.join();
        }catch (InterruptedException e) {

        }finally {
            try {
                JSONObject jsonObject = new JSONObject(httpRunnable.getResponseBody());
                int result = jsonObject.getInt("result_code");
                Log.d("result_code", String.valueOf(result));
                if (result == 1){
                    startActivity(intent);
                }else {
                    Toast.makeText(this, "Неверный логин или пароль", Toast.LENGTH_SHORT).show();
                }

            }catch (JSONException ex){
                Toast.makeText(this, "Ошибка сервера", Toast.LENGTH_SHORT).show();

            }

        }

    }

}