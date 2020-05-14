package com.ngtszlong.eztrycloth.menu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatDrawableManager;
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
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class MenuFragment extends Fragment implements MenuAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private MenuAdapter menuAdapter;
    public static ArrayList<MenuItem> menuItems;

    RadioButton rb_all;
    RadioButton rb_men;
    RadioButton rb_women;
    RadioGroup rg_type;

    String[] type;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle(getString(R.string.PleasechooseClothesType));
        LoadLocale();
        View view = inflater.inflate(R.layout.framgent_menu, container, false);
        recyclerView = view.findViewById(R.id.rv_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        menuItems = new ArrayList<>();
        rb_all = view.findViewById(R.id.rb_all);
        rb_men = view.findViewById(R.id.rb_men);
        rb_women = view.findViewById(R.id.rb_women);
        rg_type = view.findViewById(R.id.rg_type);

        type = getResources().getStringArray(R.array.type);
        check(type);


        rg_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (rb_all.getId() == checkedId){
                    menuItems.clear();
                    type = getResources().getStringArray(R.array.type);
                    check(type);
                }else if (rb_men.getId() == checkedId){
                    menuItems.clear();
                    type = getResources().getStringArray(R.array.men);
                    check(type);
                }else if (rb_women.getId() == checkedId){
                    menuItems.clear();
                    type = getResources().getStringArray(R.array.women);
                    check(type);
                }
            }
        });
        return view;
    }

    private void check(String[] type) {
        for (int i = 0; i< type.length; i++){
            if (type[i].equals("Hoodies") || type[i].equals("連帽衫")){
                menuItems.add(new MenuItem(type[i], R.drawable.hoodies));
            }else if (type[i].equals("Shirt") || type[i].equals("襯衫")){
                menuItems.add(new MenuItem(type[i], R.drawable.shirt));
            }else if (type[i].equals("T-Shirt") || type[i].equals("上衣")){
                menuItems.add(new MenuItem(type[i], R.drawable.tshirt));
            }else if (type[i].equals("Dress") || type[i].equals("連衣裙")){
                menuItems.add(new MenuItem(type[i], R.drawable.dress));
            }
        }
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        menuAdapter = new MenuAdapter(getActivity(), menuItems);
        recyclerView.setAdapter(menuAdapter);
        menuAdapter.setOnItemClickListener(MenuFragment.this);
    }

    @Override
    public void onItemClick(int position) {
        MenuItem menuItem = menuItems.get(position);
        Intent intent = new Intent(getActivity().getApplication(), ListItemActivity.class);
        intent.putExtra("clothtype", menuItem.getName());
        startActivity(intent);
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("Setting", MODE_PRIVATE).edit();
        editor.putString("My_Lang", lang);
        editor.apply();
    }

    public void LoadLocale() {
        SharedPreferences preferences = getActivity().getSharedPreferences("Setting", MODE_PRIVATE);
        String language = preferences.getString("My_Lang", "");
        setLocale(language);
    }


}
