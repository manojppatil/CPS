package com.bits.cps;

import android.content.Intent;
import android.os.Bundle;

import com.bits.cps.Helper.Routes;
import com.bits.cps.Helper.SharedPreferencesWork;

import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.HashMap;

import am.appwise.components.ni.NoInternetDialog;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    NoInternetDialog noInternetDialog;
    HashMap ssname, ssemail;
    String username, useremail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("CPS ADMIN");
        SharedPreferencesWork sharedPreferencesWork = new SharedPreferencesWork(MainActivity.this);
        ssname = sharedPreferencesWork.checkAndReturn(Routes.sharedPrefForLogin, "name");
        ssemail = sharedPreferencesWork.checkAndReturn(Routes.sharedPrefForLogin, "userid");
        username = ssname.get("name").toString();
        useremail = ssemail.get("userid").toString();
        noInternetDialog = new NoInternetDialog.Builder(MainActivity.this).build();
        noInternetDialog.setCancelable(true);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        TextView txtProfileName = navigationView.getHeaderView(0).findViewById(R.id.profile_name);
        txtProfileName.setText(username);
        TextView txtUseremail = navigationView.getHeaderView(0).findViewById(R.id.user_email);
        txtUseremail.setText(useremail);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_createID) {
            Intent intent = new Intent(MainActivity.this, CreateID.class);
            startActivity(intent);
        } else if (id == R.id.nav_task) {
            Intent intent = new Intent(MainActivity.this, AssignTask.class);
            startActivity(intent);
        } else if (id == R.id.show_task) {
            Intent intent = new Intent(MainActivity.this, ShowTasktoAdmin.class);
            startActivity(intent);
        } else if (id == R.id.nav_inbox) {
            Intent intent = new Intent(MainActivity.this, InboxActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            if (new SharedPreferencesWork(MainActivity.this).eraseData(Routes.sharedPrefForLogin)) {
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        noInternetDialog.onDestroy();
    }
}
