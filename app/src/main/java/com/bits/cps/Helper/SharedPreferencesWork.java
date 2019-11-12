package com.bits.cps.Helper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.bits.cps.LoginActivity;
import com.bits.cps.MainActivity;
import com.bits.cps.PunchActivity;
import com.bits.cps.UserActivity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class SharedPreferencesWork {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context ctx;

    public SharedPreferencesWork(Context ctx) {
        this.ctx = ctx;
    }

    public void insertOrReplace(HashMap hm, String filename) {
        sharedPreferences = (SharedPreferences) ctx.getSharedPreferences(filename, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        Set set = hm.entrySet();
        Iterator ite = set.iterator();
        while (ite.hasNext()) {
            Map.Entry entry = (Map.Entry) ite.next();
            editor.putString("" + entry.getKey(), "" + entry.getValue());
        }
        editor.commit();

    }

    public HashMap checkAndReturn(String filename, String key) {
        SharedPreferences prefs = ctx.getSharedPreferences(filename, Context.MODE_PRIVATE);
        return (HashMap) prefs.getAll();
    }

    public boolean eraseData(HashMap hm, String filename) {
        SharedPreferences preferences = ctx.getSharedPreferences(filename, 0);
        Set set = hm.entrySet();
        boolean b = false;
        Iterator ite = set.iterator();
        while (ite.hasNext()) {
            Map.Entry mp = (Map.Entry) ite.next();
            b = preferences.edit().remove("" + mp.getKey()).commit();
        }
        return b;
    }

    public boolean eraseData(String filename) {
        SharedPreferences preferences = ctx.getSharedPreferences(filename, 0);
        boolean b = preferences.edit().clear().commit();
        return b;
    }

    public boolean checkForLogin() {
        String role = "";
        String id = "";
        ;
        HashMap hashMap = checkAndReturn(Routes.sharedPrefForLogin, "userid");
        if (hashMap.containsKey("userid")) {
            role = hashMap.get("role").toString();
            id = hashMap.get("userid").toString();
            if (hashMap.get("role") != null) {
                role = hashMap.get("role").toString();
            }


            if (!id.equals(null) && role.equals("admin")) {
                Intent homeIntent = new Intent(ctx, MainActivity.class);
                homeIntent.putExtra("userid", id);
                ctx.startActivity(homeIntent);

            } else if (!id.equals(null) && role.equals("employee")) {
                Intent homeIntent = new Intent(ctx, UserActivity.class);
                homeIntent.putExtra("userid", id);
                ctx.startActivity(homeIntent);

            } else if (!id.equals(null) && role.equals("team_leader")) {
                Intent homeIntent = new Intent(ctx, PunchActivity.class);
                homeIntent.putExtra("role","Team Leader");
                homeIntent.putExtra("userid", id);
                ctx.startActivity(homeIntent);

            } else if (!id.equals(null) && role.equals("data_operator")) {
                Intent homeIntent = new Intent(ctx, PunchActivity.class);
                homeIntent.putExtra("role","Data Entry Operator");
                homeIntent.putExtra("userid", id);
                ctx.startActivity(homeIntent);
            }
        }
        return false;
    }

    public int checkExceptLogin() {
        HashMap hashMap = checkAndReturn(Routes.sharedPrefForLogin, "userid");
        if (hashMap.containsKey("userid")) {
            String suserid = hashMap.get("userid").toString();
            return Integer.parseInt(suserid.trim());
        } else {
            Intent intent = new Intent(ctx, LoginActivity.class);
            ctx.startActivity(intent);
        }

        return 0;
    }

}
