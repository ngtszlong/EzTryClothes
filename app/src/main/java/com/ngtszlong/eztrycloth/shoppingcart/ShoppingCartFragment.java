package com.ngtszlong.eztrycloth.shoppingcart;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ngtszlong.eztrycloth.R;
import com.ngtszlong.eztrycloth.menu.detail.DetailActivity;

import java.util.ArrayList;

public class ShoppingCartFragment extends Fragment implements ShoppingCartAdapter.OnItemClickListener {

    private RecyclerView shopcartrecyclerView;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;
    private ArrayList<ShoppingCart> shoppingCartArrayList;
    private ShoppingCartAdapter shoppingCartAdapter;
    private FirebaseAuth fAuth;
    private FirebaseUser user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Shopping Cart");
        View view = inflater.inflate(R.layout.framgent_shoppingcart, container, false);
        shopcartrecyclerView = view.findViewById(R.id.rv_shopcart);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        shopcartrecyclerView.setLayoutManager(linearLayoutManager);

        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference().child("ShoppingCart").child(user.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                shoppingCartArrayList = new ArrayList<>();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    ShoppingCart shoppingCart = dataSnapshot1.getValue(ShoppingCart.class);
                    shoppingCartArrayList.add(shoppingCart);
                }
                shoppingCartAdapter = new ShoppingCartAdapter(getActivity(), shoppingCartArrayList);
                shopcartrecyclerView.setAdapter(shoppingCartAdapter);
                shoppingCartAdapter.setOnItemClickListener(ShoppingCartFragment.this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        reference.keepSynced(true);
        return view;
    }

    @Override
    public void onItemClick(int position) {
        ShoppingCart shoppingCart = shoppingCartArrayList.get(position);
        Intent intent = new Intent(getActivity().getApplication(), DetailActivity.class);
        intent.putExtra("No", shoppingCart.getNo());
        startActivity(intent);
    }
}
