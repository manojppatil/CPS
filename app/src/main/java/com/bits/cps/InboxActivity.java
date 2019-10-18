package com.bits.cps;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bits.cps.Adapter.FoldingCellListAdapter;
import com.bits.cps.pojo.Item;
import com.ramotion.foldingcell.FoldingCell;

import java.util.ArrayList;

public class InboxActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        ListView theListView = findViewById(R.id.mainListView);

        // prepare elements to display
        final ArrayList<Item> items = Item.getTestingList();

        // add custom btn handler to first list item
        items.get(0).setRequestBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "CUSTOM HANDLER FOR FIRST BUTTON", Toast.LENGTH_SHORT).show();
            }
        });

        // create custom adapter that holds elements and their state (we need hold a id's of unfolded elements for reusable elements)
        final FoldingCellListAdapter adapter = new FoldingCellListAdapter(this, items);

        // add default btn handler for each request btn on each item if custom handler not found
        adapter.setDefaultRequestBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "DEFAULT HANDLER FOR ALL BUTTONS", Toast.LENGTH_SHORT).show();
            }
        });

        // set elements to adapter
        theListView.setAdapter(adapter);

        // set on click event listener to list view
        theListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int pos, long l) {
                // toggle clicked cell state
                final FoldingCell fc = (FoldingCell) findViewById(R.id.folding_cell);
                fc.initialize(700, Color.DKGRAY, 6);
                fc.initialize(30, 700, Color.DKGRAY, 6);
                fc.toggle(false);
                // register in adapter that state for selected cell is toggled
                adapter.registerToggle(pos);

            }
        });

    }

}
