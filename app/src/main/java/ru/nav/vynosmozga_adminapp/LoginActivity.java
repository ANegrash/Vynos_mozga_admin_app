package ru.nav.vynosmozga_adminapp;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    public EditText login;
    public EditText password;
    SharedPreferences prefs;
    TextView error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        login = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        error = (TextView) findViewById(R.id.erorLogin);
        prefs = getSharedPreferences("ru.nav.vynosmozga_adminapp", MODE_PRIVATE);
        Button btn = (Button) findViewById(R.id.login);


        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final String log, pass, request;
                log=login.getText().toString();
                pass=password.getText().toString();
                request="0";

                Post test = new Post();

                test.run("https://vynosmozga.ru/admin/app/index.php?email="+log+"&pass="+pass+"&typeRequest="+request,
                        new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                LoginActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        error.setText("Проблемы с соединением. Попробуйте позже");
                                    }
                                });
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                //todo work with response, parse and etc...
                                String result=response.body().string();
                                if(result.equals("youLogged")){
                                    prefs.edit().putBoolean("isLogged", false).commit();
                                    prefs.edit().putString("myEmail", log).commit();
                                    prefs.edit().putString("myPass", pass).commit();
                                    LoginActivity.super.finish();
                                }else{
                                    error.setText("Неверный e-mail или пароль");
                                }


                            }
                        });

            }
        });
    }

}