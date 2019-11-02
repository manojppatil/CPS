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

public class AdminTaskAdapter extends RecyclerView.Adapter<AdminTaskAdapter.ViewHolder> {
    private ArrayList items;
    private int itemLayout;
    private Context context;
    HashMap hm;

    public AdminTaskAdapter(ArrayList items, Context context) {
        this.items = items;
        this.context = context;
    }


    @NonNull
    @Override
    public AdminTaskAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_showtask_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminTaskAdapter.ViewHolder viewHolder, int position) {
        hm = (HashMap) items.get(position);
//        final String id = hm.get("id").toString();
//        viewHolder.id.setText(id);
        final String name = hm.get("name").toString();
        viewHolder.name.setText(name);
        final String add = hm.get("address").toString();
        viewHolder.address.setText(add);
        final String mob = hm.get("mobile").toString();
        viewHolder.mobile.setText(mob);
        final String uname = hm.get("username").toString();
        viewHolder.emp_name.setText(uname);
        viewHolder.meeting_time.setText(hm.get("meeting_time").toString());
        viewHolder.remark.setText(hm.get("amount").toString());

        viewHolder.itemView.setTag(hm);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView id;
        public TextView name;
        public TextView address;
        public TextView mobile;
        public TextView emp_name;
        public TextView meeting_time;
        public TextView remark;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

//            id = itemView.findViewById(R.id.adtxttaskid);
            name = itemView.findViewById(R.id.adclientname);
            address = itemView.findViewById(R.id.adclientadd);
            mobile = itemView.findViewById(R.id.adclientphone);
            emp_name = itemView.findViewById(R.id.ademp_name);
            meeting_time = itemView.findViewById(R.id.admeeting_time);
            remark = itemView.findViewById(R.id.adclientremark);

        }
    }
}
