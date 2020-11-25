package ru.nav.vynosmozga_adminapp;

import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TabHost;


public class MainActivity extends TabActivity {
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefs = getSharedPreferences("ru.nav.vynosmozga_adminapp", MODE_PRIVATE);
        TabHost tabHost = getTabHost();

        if (prefs.getBoolean("isLogged", true)) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }else{
            TabHost.TabSpec tabSpec;

            tabSpec = tabHost.newTabSpec("tag2");
            tabSpec.setIndicator("Регистрация");
            tabSpec.setContent(new Intent(MainActivity.this, SettingsActivity.class));
            tabHost.addTab(tabSpec);

            tabSpec = tabHost.newTabSpec("tag1");
            tabSpec.setIndicator("Меню");
            tabSpec.setContent(new Intent(MainActivity.this, NowRegActivity.class));
            tabHost.addTab(tabSpec);
        }
    }
}