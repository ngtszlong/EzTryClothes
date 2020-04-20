package com.ngtszlong.eztrycloth.wishlist;

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

public class WishListFragment extends Fragment implements WishlistAdapter.OnItemClickListener {

    private RecyclerView WishlistRecyclerlist;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;
    private ArrayList<Wishlist> wishlistArrayList;
    private WishlistAdapter wishlistAdapter;
    private FirebaseAuth fAuth;
    private FirebaseUser user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("WishList");
        View view = inflater.inflate(R.layout.framgent_wishlist, container, false);

        WishlistRecyclerlist = view.findViewById(R.id.rv_wishlist);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        WishlistRecyclerlist.setLayoutManager(linearLayoutManager);

        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference().child("Wishlist").child(user.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                wishlistArrayList = new ArrayList<>();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Wishlist wishlist = dataSnapshot1.getValue(Wishlist.class);
                    wishlistArrayList.add(wishlist);
                }
                wishlistAdapter = new WishlistAdapter(getActivity(), wishlistArrayList);
                WishlistRecyclerlist.setAdapter(wishlistAdapter);
                wishlistAdapter.setOnItemClickListener(WishListFragment.this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        reference.keepSynced(true);
        return view;
    }

    public void onItemClick(int position) {
        Wishlist wishlist = wishlistArrayList.get(position);
        Intent intent = new Intent(getActivity().getApplication(), DetailActivity.class);
        intent.putExtra("No", wishlist.getNo());
        startActivity(intent);
    }
}
