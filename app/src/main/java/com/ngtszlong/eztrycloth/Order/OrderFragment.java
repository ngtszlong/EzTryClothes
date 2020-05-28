package com.ngtszlong.eztrycloth.Order;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ngtszlong.eztrycloth.R;

import java.util.ArrayList;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class OrderFragment extends Fragment {
    private static ArrayList<Order> orderArrayList;
    private OrderAdapter orderAdapter;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);
        getActivity().setTitle(getText(R.string.PurchaseHistory));
        LoadLocale();

        recyclerView = view.findViewById(R.id.rv_order);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference().child("PurchaseOrder").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orderArrayList = new ArrayList<Order>();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                        Order order = dataSnapshot2.getValue(Order.class);
                        orderArrayList.add(order);
                    }
                }
                orderAdapter = new OrderAdapter(getContext(), orderArrayList);
                recyclerView.setAdapter(orderAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return view;
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
