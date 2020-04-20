package com.ngtszlong.eztrycloth.menu;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ngtszlong.eztrycloth.R;
import com.ngtszlong.eztrycloth.menu.list.ListItem;
import com.ngtszlong.eztrycloth.menu.list.ListItemActivity;

import java.util.ArrayList;

public class MenuFragment extends Fragment implements MenuAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private MenuAdapter menuAdapter;
    public static ArrayList<MenuItem> menuItems;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.framgent_menu, container, false);
        recyclerView = view.findViewById(R.id.rv_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        menuItems = new ArrayList<>();
        menuItems.add(new MenuItem("jacket", R.drawable.jacket));
        menuItems.add(new MenuItem("HOODIE ", R.drawable.hoodies));
        menuItems.add(new MenuItem("Shirt", R.drawable.shirt));
        menuItems.add(new MenuItem("PANTS", R.drawable.pants));
        menuItems.add(new MenuItem("Polo", R.drawable.polo));

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        menuAdapter = new MenuAdapter(getActivity(), menuItems);
        recyclerView.setAdapter(menuAdapter);
        menuAdapter.setOnItemClickListener(MenuFragment.this);
        return view;
    }

    @Override
    public void onItemClick(int position) {
        MenuItem menuItem = menuItems.get(position);
        Intent intent = new Intent(getActivity().getApplication(), ListItemActivity.class);
        intent.putExtra("clothtype", menuItem.getName());
        startActivity(intent);
    }
}
