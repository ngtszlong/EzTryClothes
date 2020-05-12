package com.ngtszlong.eztrycloth.shoppingcart;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
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
import com.ngtszlong.eztrycloth.MainActivity;
import com.ngtszlong.eztrycloth.Order.Order;
import com.ngtszlong.eztrycloth.Profile.Profile;
import com.ngtszlong.eztrycloth.R;
import com.ngtszlong.eztrycloth.menu.detail.DetailActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ShoppingCartFragment extends Fragment implements ShoppingCartAdapter.OnItemClickListener {

    private RecyclerView shopcartrecyclerView;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;
    private ArrayList<ShoppingCart> shoppingCartArrayList;
    private ShoppingCartAdapter shoppingCartAdapter;
    private FirebaseAuth fAuth;
    private FirebaseUser user;
    private CardView cardView;

    Order order;

    SimpleDateFormat format;
    Date date;
    String str;

    String address;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Shopping Cart");
        View view = inflater.inflate(R.layout.framgent_shoppingcart, container, false);
        cardView = view.findViewById(R.id.btn_shopcart);

        shopcartrecyclerView = view.findViewById(R.id.rv_shopcart);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        shopcartrecyclerView.setLayoutManager(linearLayoutManager);

        order = new Order();

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

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getcurrenttime();
                final String uid = user.getUid();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference reference = database.getReference("PurchaseOrder");
                for (int i = 0; i < shoppingCartArrayList.size(); i++) {
                    if (!shoppingCartArrayList.get(i).getQuantity().equals("0")) {
                        order.setCompanyuid(shoppingCartArrayList.get(i).getCompanyuid());
                        order.setDate(str);
                        order.setDiscount(shoppingCartArrayList.get(i).getDiscount());
                        order.setUid(shoppingCartArrayList.get(i).getUid());
                        order.setStr(shoppingCartArrayList.get(i).getStr());
                        order.setName(shoppingCartArrayList.get(i).getName());
                        order.setImage(shoppingCartArrayList.get(i).getImage());
                        order.setPrice(shoppingCartArrayList.get(i).getPrice());
                        order.setQuantity(shoppingCartArrayList.get(i).getQuantity());
                        order.setNo(shoppingCartArrayList.get(i).getNo());
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                    Profile profile = dataSnapshot1.getValue(Profile.class);
                                    if (profile.getUid().equals(uid)){
                                        address = profile.getAddress();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        order.setAddress(address);

                        reference.child(uid).child("Order"+str).child(String.valueOf(i)).setValue(order);

                        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("ShoppingCart").child(user.getUid()).child(shoppingCartArrayList.get(i).getStr());
                        databaseReference1.removeValue();
                    }
                }
                Toast.makeText(getContext(), "You have already Ordered", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    public void getcurrenttime() {
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        date = new Date(System.currentTimeMillis());
        str = format.format(date);
    }

    @Override
    public void onItemClick(int position) {
        ShoppingCart shoppingCart = shoppingCartArrayList.get(position);
        Intent intent = new Intent(getActivity().getApplication(), DetailActivity.class);
        intent.putExtra("No", shoppingCart.getNo());
        startActivity(intent);
    }
}
