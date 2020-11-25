package ru.nav.vynosmozga_adminapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class TeamData extends AppCompatActivity {
SharedPreferences prefs;
ImageButton phoning;
EditText comandName, comandPeople, comandCap, comandEmail, comandPhone, comandSocial, comandComment;
RadioGroup barSel;
Button delete, update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_data);
        prefs = getSharedPreferences("ru.nav.vynosmozga_adminapp", MODE_PRIVATE);
        comandName = findViewById(R.id.nameTeamEdit);
        barSel = findViewById(R.id.barSelect);
        comandPeople = findViewById(R.id.countPeopleEdit);
        comandCap = findViewById(R.id.capNameEdit);
        comandEmail = findViewById(R.id.emailEdit);
        comandPhone = findViewById(R.id.phoneEdit);
        comandSocial = findViewById(R.id.socialEdit);
        comandComment = findViewById(R.id.commentEdit);
        phoning = findViewById(R.id.phoneButton);
        delete= findViewById(R.id.deleteTeam);
        update= findViewById(R.id.updateTeam);


        ActionBar actionBar =getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);


        Intent intent = getIntent();
        final String teamName = intent.getStringExtra("teamName");

        String log,pass,request;
        log=prefs.getString("myEmail","");
        pass=prefs.getString("myPass","");
        request="2";

        Post test = new Post();

        test.run("https://vynosmozga.ru/admin/app/index.php?email="+log+"&pass="+pass+"&typeRequest="+request+"&team="+teamName,
                new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {

                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        //todo work with response, parse and etc...
                        final String result = response.body().string();
                        TeamData.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String[] myData = result.split("<br>");
                                comandName.setText(myData[1]);
                                if(myData[5].equals("Тот Ещё?! Бар"))
                                barSel.check(R.id.tebRadio);
                                else barSel.check(R.id.evrRadio);
                                comandPeople.setText(myData[0]);
                                comandPhone.setText(myData[3]);
                                comandEmail.setText(myData[4]);
                                comandCap.setText(myData[2]);
                                comandSocial.setText(myData[6]);
                                comandComment.setText(myData[7]);
                                final String numberPh = "tel:"+myData[3];
                                phoning.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(numberPh));
                                        startActivity(callIntent);
                                    }
                                });


                                delete.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        deleteTeam(teamName);
                                    }
                                });

                                update.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String comandUpdate, newName, newCap, newBar, newCount, newEmail, newPhone, newSoc, newComment;
                                        int checkedRadioButtonId = barSel.getCheckedRadioButtonId();

                                        // Найдём переключатель по его id
                                        RadioButton myRadioButton = (RadioButton) findViewById(checkedRadioButtonId);
                                        newBar=myRadioButton.getText().toString();
                                        comandUpdate=teamName;
                                        newName=comandName.getText().toString();
                                        newCap=comandCap.getText().toString();
                                        newCount=comandPeople.getText().toString();
                                        newEmail=comandEmail.getText().toString();
                                        newPhone=comandPhone.getText().toString();
                                        newSoc=comandSocial.getText().toString();
                                        newComment=comandComment.getText().toString();
                                        updateTeam(comandUpdate,newName,newCap, newBar, newCount, newEmail, newPhone, newSoc, newComment);
                                    }
                                });
                            }
                        });
                    }
                });
    }

    void deleteTeam(String teamName){
        final String log,pass,request;
        log=prefs.getString("myEmail","");
        pass=prefs.getString("myPass","");
        request="3";
        Post test = new Post();

        test.run("https://vynosmozga.ru/admin/app/index.php?email="+log+"&pass="+pass+"&typeRequest="+request+"&team="+teamName,
                new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        TeamData.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog.Builder builder=new AlertDialog.Builder(TeamData.this);
                                builder.setTitle("Ошибка").setMessage("Не удалось отправить запрос на удаление этой команды. Попробуйте позже").setPositiveButton("Ладно...", null).create().show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        //todo work with response, parse and etc...
                        final String result = response.body().string();
                        TeamData.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (result.equals("teamDeleted")) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(TeamData.this);
                                    builder.setTitle("Успех").setMessage("Команда удалена! Обновите список команд, чтобы увидеть изменения").setPositiveButton("Договорились", null).create().show();
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(TeamData.this);
                                    builder.setTitle("Ошибка").setMessage("Что-то пошло не так...").setPositiveButton("Попробую позже", null).create().show();
                                }
                            }
                        });

                    }
                });
    }

    void updateTeam(String comandUpdate,String newName,String newCap,String newBar,String newCount,String newEmail,String newPhone,String newSoc,String newComment){
        final String log,pass,request;
        log=prefs.getString("myEmail","");
        pass=prefs.getString("myPass","");
        request="4";
        Post test = new Post();

        test.run("https://vynosmozga.ru/admin/app/index.php?email="+log+"&pass="+pass+"&typeRequest="+request+"&team="+comandUpdate+"&newName="+newName+"&newCap="+newCap+"&newBar="+newBar+"&newCount="+newCount+"&newEmail="+newEmail+"&newPhone="+newPhone+"&newSoc="+newSoc+"&newComment="+newComment,
                new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        TeamData.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog.Builder builder=new AlertDialog.Builder(TeamData.this);
                                builder.setTitle("Ошибка").setMessage("Не удалось отправить запрос на изменение данных этой команды. Попробуйте позже").setPositiveButton("Ладно...", null).create().show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        //todo work with response, parse and etc...
                        final String result = response.body().string();
                        TeamData.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (result.equals("teamUpdated")) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(TeamData.this);
                                    builder.setTitle("Успех").setMessage("Данные о команде обновлены! Обновите список команд, чтобы увидеть изменения").setPositiveButton("Договорились", null).create().show();
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(TeamData.this);
                                    builder.setTitle("Ошибка").setMessage("Что-то пошло не так...").setPositiveButton("Попробую позже", null).create().show();
                                }
                            }
                        });

                    }
                });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
