package ru.nav.vynosmozga_adminapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {


    ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
    SharedPreferences prefs;
    HashMap<String, String> map;
    ListView listView;
    SimpleAdapter adapter;
    String[] allTeams=new String[1000];
    TextView errTW;
    FloatingActionButton fab;

    void insertPlayer(String place, String teamInsert, String peopleInsert){
        map = new HashMap<>();
        map.put("Place", place);
        map.put("Team", teamInsert);
        map.put("People", peopleInsert);
        arrayList.add(map);

    }

    void doInsert(){
        adapter = new SimpleAdapter(this, arrayList, R.layout.list_item,
                new String[]{"Place","Team", "People"},
                new int[]{R.id.tw_place, R.id.tw_team, R.id.tw_people});

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefs = getSharedPreferences("ru.nav.vynosmozga_adminapp", MODE_PRIVATE);
        errTW=(TextView)findViewById(R.id.errorTextMain);
        fab = findViewById(R.id.fab);
        listView=(ListView)findViewById(R.id.listView);
        final SwipeRefreshLayout swipeView = (SwipeRefreshLayout) findViewById(R.id.swipe);

        swipeView.setEnabled(false);



        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(scrollState==0){
                    fab.show();
                }else{
                    fab.hide();
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if (i == 0)
                    swipeView.setEnabled(true);
                else
                    swipeView.setEnabled(false);
            }

        });
        if (prefs.getBoolean("isLogged", true)) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }else{
            final String log,pass,request;
            log=prefs.getString("myEmail","");
            pass=prefs.getString("myPass","");
            request="1";
            final Post test = new Post();

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String stat=prefs.getString("statistic","");
                    String header=stat.split("<br>")[0];
                    String info=stat.split("<br>")[1];
                    AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle(header).setMessage(info).setPositiveButton("Супер!", null).create().show();
                }
            });

            swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    swipeView.setRefreshing(true);
                    ( new Handler()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            swipeView.setRefreshing(false);
                            test.run("https://vynosmozga.ru/admin/app/index.php?email="+log+"&pass="+pass+"&typeRequest="+request,
                                    new Callback() {
                                        @Override
                                        public void onFailure(Call call, IOException e) {
                                            MainActivity.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                }
                                            });
                                        }

                                        @Override
                                        public void onResponse(Call call, Response response) throws IOException {
                                            //todo work with response, parse and etc...
                                            String result=response.body().string();
                                            if(!result.isEmpty()) {
                                                String gameNumNow=result.split("-gameNum")[0];
                                                result=result.split("-gameNum")[1];
                                                String[] registred = result.split(";");
                                                int teb=0;
                                                int evr=0;

                                                arrayList.clear();
                                                for (int i = 0; i < registred.length; i++) {
                                                    String forplace = registred[i];
                                                    String bar = forplace.split("-bar")[0];
                                                    String lefts = forplace.split("-bar")[1];
                                                    String people = lefts.split("people-")[1];
                                                    String nameTeam = lefts.split("people-")[0];
                                                    switch (bar) {
                                                        case "Тот Ещё?! Бар":
                                                            bar = "ТЕБ";
                                                            teb++;
                                                            break;
                                                        case "Евразия mix":
                                                            bar = "ЕвМ";
                                                            evr++;
                                                            break;
                                                        default: bar="-";
                                                            break;
                                                    }
                                                    insertPlayer(bar, nameTeam, people);
                                                    allTeams[i] = nameTeam;

                                                }
                                                prefs.edit().putString("statistic", "Игра #"+gameNumNow+"<br>ТотЕщё?!Бар: "+teb+"\nЕвразия mix: "+evr).commit();
                                                MainActivity.this.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        doInsert();
                                                        listView = (ListView) findViewById(R.id.listView);
                                                        listView.setAdapter(adapter);
                                                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                            public void onItemClick(AdapterView<?> parent, View view,
                                                                                    int position, long id) {
                                                                Intent intent = new Intent(MainActivity.this, TeamData.class);
                                                                intent.putExtra("teamName", allTeams[position]);
                                                                startActivity(intent);
                                                            }
                                                        });
                                                    }
                                                });
                                            }else{
                                                MainActivity.this.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        errTW.setText("Регистрация закрыта - данные о командах недоступны");
                                                    }
                                                });
                                            }

                                        }

                                    });

                        }
                    }, 3000);
                }
            });


            test.run("https://vynosmozga.ru/admin/app/index.php?email="+log+"&pass="+pass+"&typeRequest="+request,
                    new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    }
                            });
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            //todo work with response, parse and etc...
                            String result=response.body().string();
                            if(!result.isEmpty()) {
                                String gameNumNow=result.split("-gameNum")[0];
                                result=result.split("-gameNum")[1];
                                String[] registred = result.split(";");
                                int teb=0;
                                int evr=0;

                                for (int i = 0; i < registred.length; i++) {
                                    String forplace = registred[i];
                                    String bar = forplace.split("-bar")[0];
                                    String lefts = forplace.split("-bar")[1];
                                    String people = lefts.split("people-")[1];
                                    String nameTeam = lefts.split("people-")[0];
                                    switch (bar) {
                                        case "Тот Ещё?! Бар":
                                            bar = "ТЕБ";
                                            teb++;
                                            break;
                                        case "Евразия mix":
                                            bar = "ЕвМ";
                                            evr++;
                                            break;
                                        default: bar="-";
                                            break;
                                    }
                                    insertPlayer(bar, nameTeam, people);
                                    allTeams[i] = nameTeam;

                                }
                                prefs.edit().putString("statistic", "Игра #"+gameNumNow+"<br>ТотЕщё?!Бар: "+teb+"\nЕвразия mix: "+evr).commit();
                                MainActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        doInsert();
                                        listView = (ListView) findViewById(R.id.listView);
                                        listView.setAdapter(adapter);
                                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            public void onItemClick(AdapterView<?> parent, View view,
                                                                    int position, long id) {
                                                Intent intent = new Intent(MainActivity.this, TeamData.class);
                                                intent.putExtra("teamName", allTeams[position]);
                                                startActivity(intent);
                                            }
                                        });
                                    }
                                });
                            }else{
                                MainActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        errTW.setText("Регистрация закрыта - данные о командах недоступны");
                                    }
                                });
                            }

                        }

                    });

        }
    }
}