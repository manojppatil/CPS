package com.bits.cps.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bits.cps.R;
import com.bits.cps.TaskSheet;

import java.util.ArrayList;
import java.util.HashMap;

public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.ViewHolder> {
    private ArrayList items;
    private int itemLayout;
    private Context context;
    HashMap hm;

    public InboxAdapter(ArrayList items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cell_title_layout, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        hm = (HashMap) items.get(i);
        final String id = hm.get("id").toString();
        viewHolder.id.setText(id);
        final String name = hm.get("name").toString();
        viewHolder.name.setText(name);
        final String ctotal = hm.get("ctotal").toString();
        viewHolder.ctotal.setText(ctotal);
        final String ptotal = hm.get("ptotal").toString();
        viewHolder.ptotal.setText(ptotal);
        viewHolder.login.setText(hm.get("login_time").toString());
        viewHolder.logout.setText(hm.get("logout_time").toString());

        viewHolder.itemView.setTag(hm);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView id;
        public TextView name;
        public TextView ctotal;
        public TextView ptotal;
        public TextView login;
        public TextView logout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            id = itemView.findViewById(R.id.inbox_emp_id);
            name = itemView.findViewById(R.id.inbox_emp_name);
            ctotal = itemView.findViewById(R.id.inbox_completed_task);
            ptotal = itemView.findViewById(R.id.inbox_pending_task);
            login = itemView.findViewById(R.id.inbox_login_time);
            logout = itemView.findViewById(R.id.inbox_logout_time);

        }
    }
}