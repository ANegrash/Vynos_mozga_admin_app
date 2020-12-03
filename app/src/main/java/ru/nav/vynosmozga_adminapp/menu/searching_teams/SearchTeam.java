package ru.nav.vynosmozga_adminapp.menu.searching_teams;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import ru.nav.vynosmozga_adminapp.send_request.Post;
import ru.nav.vynosmozga_adminapp.R;
import ru.nav.vynosmozga_adminapp.adapters_for_list.State;
import ru.nav.vynosmozga_adminapp.adapters_for_list.StateAdapter;

public class SearchTeam extends AppCompatActivity {
    private List<State> states = new ArrayList();
    SharedPreferences prefs;
    String[] allTeams = new String[1000];
    EditText searchTeam;
    ImageButton doFind;

    ListView countriesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_team);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        countriesList = (ListView) findViewById(R.id.list_searching);
        searchTeam = (EditText) findViewById(R.id.nameTeamSearch);
        doFind = (ImageButton) findViewById(R.id.btnDoSearch);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        doFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String team = searchTeam.getText().toString();

                prefs = getSharedPreferences("ru.nav.vynosmozga_adminapp", MODE_PRIVATE);
                String log = "";
                String pass = "";
                final String request = "5";
                log = prefs.getString("myEmail", "");
                pass = prefs.getString("myPass", "");

                final Post test = new Post();

                final String finalLog = log;
                final String finalPass = pass;

                test.run("https://vynosmozga.ru/admin/app/index.php?email=" + log + "&pass=" + pass + "&typeRequest=" + request + "&team=" + team,
                        new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                SearchTeam.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                    }
                                });
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                //todo work with response, parse and etc...
                                String result = response.body().string();
                                if (!result.isEmpty()) {
                                    if (result.contains(";")) {
                                        String[] registred = result.split(";");
                                        int barRes;
                                        states.clear();
                                        for (int i = 0; i < registred.length; i++) {
                                            String forplace = registred[i].trim();
                                            String bar = forplace.split("-bar")[0];
                                            String lefts = forplace.split("-bar")[1];
                                            String people = lefts.split("people-")[1];
                                            String nameTeam = lefts.split("people-")[0];
                                            String game = people.split("gameNum-")[1];
                                            String countPeople = people.split("gameNum-")[0];
                                            switch (bar) {
                                                case "Тот Ещё?! Бар":
                                                    barRes = R.drawable.teb_img;
                                                    break;
                                                case "Евразия mix":
                                                    barRes = R.drawable.evr_img;
                                                    break;
                                                case "кафе Кочерга":
                                                    barRes = R.drawable.kocherga;
                                                    break;
                                                case "караоке клуб Stars":
                                                    barRes = R.drawable.stars;
                                                    break;
                                                case "караоке Stars":
                                                    barRes = R.drawable.stars;
                                                    break;
                                                default:
                                                    barRes = R.drawable.icon;
                                                    break;
                                            }
                                            if (countPeople.equals("3") || countPeople.equals("4"))
                                                countPeople = countPeople + " игрока";
                                            else countPeople = countPeople + " игроков";
                                            countPeople += " · Игра #" + game;
                                            setInitialData(nameTeam, countPeople, barRes);
                                            allTeams[i] = nameTeam;

                                        }
                                    } else {
                                        setInitialData("Поиск не дал результатов", "Совсем", R.drawable.icon);
                                    }
                                    SearchTeam.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // создаем адаптер
                                            StateAdapter stateAdapter = new StateAdapter(SearchTeam.this, R.layout.new_list_item, states);
                                            // устанавливаем адаптер
                                            countriesList.setAdapter(stateAdapter);

                                        }
                                    });
                                } else {
                                    SearchTeam.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            //errTW.setText("Регистрация закрыта - данные о командах недоступны");
                                        }
                                    });
                                }

                            }

                        });
            }
        });

    }

    private void setInitialData(String name, String info, int res) {

        states.add(new State(name, info, res));
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
