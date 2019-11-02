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

public class taskAdapter extends RecyclerView.Adapter<taskAdapter.ViewHolder> {
    private ArrayList items;
    private int itemLayout;
    private Context context;
    HashMap hm;

    public taskAdapter(ArrayList items, Context context) {
        this.items = items;
        this.context = context;
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.task_layout, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        hm = (HashMap) items.get(i);
        final String id = hm.get("id").toString();
//        viewHolder.id.setText(id);
        final String name = hm.get("name").toString();
        viewHolder.name.setText(name);
        final String add = hm.get("address").toString();
        viewHolder.address.setText(add);
        final String mob = hm.get("mobile").toString();
        viewHolder.mobile.setText(mob);
        viewHolder.meeting_time.setText(hm.get("meeting_time").toString());
        viewHolder.remark.setText(hm.get("amount").toString());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TaskSheet.class);
                intent.putExtra("id", id);
                intent.putExtra("name", name);
                intent.putExtra("add", add);
                intent.putExtra("contact", mob);
                context.startActivity(intent);
            }
        });
        viewHolder.itemView.setTag(hm);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
//        public TextView id;
        public TextView name;
        public TextView address;
        public TextView mobile;
        public TextView meeting_time;
        public TextView remark;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

//            id = itemView.findViewById(R.id.txttaskid);
            name = itemView.findViewById(R.id.clientname);
            address = itemView.findViewById(R.id.clientadd);
            mobile = itemView.findViewById(R.id.clientphone);
            meeting_time = itemView.findViewById(R.id.meeting_time);
            remark = itemView.findViewById(R.id.clientremark);

        }
    }
}
