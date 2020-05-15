package com.ngtszlong.eztrycloth.menu.list;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ngtszlong.eztrycloth.R;
import com.ngtszlong.eztrycloth.menu.detail.DetailActivity;

import java.util.ArrayList;

public class ListItemActivity extends AppCompatActivity implements ListAdapter.OnItemClickListener {

    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;

    RecyclerView recyclerView;
    private ListAdapter listAdapter;
    public static ArrayList<ListItem> listItems;
    String type;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listitem);

        Intent intent = getIntent();
        type = intent.getStringExtra("clothtype");

        toolbar = findViewById(R.id.tb_listitem);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(type);

        if (type.equals("連帽衫")) {
            type = "Hoodies";
        } else if (type.equals("襯衫")) {
            type = "Shirt";
        } else if (type.equals("上衣")) {
            type = "T-Shirt";
        } else if (type.equals("連衣裙")) {
            type = "Dress";
        }


        recyclerView = findViewById(R.id.rv_itemlist);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference().child("Clothes");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listItems = new ArrayList<ListItem>();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    ListItem l = dataSnapshot1.getValue(ListItem.class);
                    if (type.equals(l.getType())) {
                        listItems.add(l);
                    }
                }
                listAdapter = new ListAdapter(ListItemActivity.this, listItems);
                recyclerView.setAdapter(listAdapter);
                listAdapter.setOnItemClickListener(ListItemActivity.this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mRef.keepSynced(true);
    }

    public void onItemClick(int position) {
        ListItem listItem = listItems.get(position);
        Intent intent = new Intent(ListItemActivity.this, DetailActivity.class);
        intent.putExtra("No", listItem.getNo());
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
