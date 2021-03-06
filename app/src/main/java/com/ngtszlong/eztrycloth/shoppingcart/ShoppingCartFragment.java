package com.ngtszlong.eztrycloth.shoppingcart;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import com.ngtszlong.eztrycloth.menu.list.ListItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class ShoppingCartFragment extends Fragment implements ShoppingCartAdapter.OnItemClickListener {

    private RecyclerView shopcartrecyclerView;
    private ArrayList<ShoppingCart> shoppingCartArrayList;
    private ArrayList<ListItem> listItemArrayList;
    private ShoppingCartAdapter shoppingCartAdapter;
    private FirebaseUser user;

    private Order order;

    private String str;

    private String address;
    private String name;
    private double total = 0;

    private int quantity;
    private String stringquantity;

    private TextView txt_upper;
    private TextView txt_lower;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle(getText(R.string.ShoppingCart));
        LoadLocale();
        View view = inflater.inflate(R.layout.framgent_shoppingcart, container, false);
        CardView cardView = view.findViewById(R.id.btn_shopcart);

        shopcartrecyclerView = view.findViewById(R.id.rv_shopcart);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        shopcartrecyclerView.setLayoutManager(linearLayoutManager);

        TextView txt_sizeguide = view.findViewById(R.id.txt_helpguide);
        txt_sizeguide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext()).setView(R.layout.sizeguide).show();
            }
        });

        txt_upper = view.findViewById(R.id.sc_upperbodysize);
        txt_lower = view.findViewById(R.id.sc_lowerbodysize);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = firebaseDatabase.getReference().child("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Profile profile = dataSnapshot1.getValue(Profile.class);
                    if (user.getUid().equals(profile.getUid())){
                        txt_upper.setText(profile.getUppersize());
                        txt_lower.setText(profile.getLowersize());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        order = new Order();

        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        reference = firebaseDatabase.getReference().child("ShoppingCart").child(user.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                shoppingCartArrayList = new ArrayList<>();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    ShoppingCart shoppingCart = dataSnapshot1.getValue(ShoppingCart.class);
                    shoppingCartArrayList.add(shoppingCart);
                }
                if (shoppingCartArrayList.size()==0){
                    Toast.makeText(getContext(), R.string.shoppingCartempty, Toast.LENGTH_SHORT).show();
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

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Profile profile = dataSnapshot1.getValue(Profile.class);
                    if (profile.getUid().equals(user.getUid())) {
                        address = profile.getAddress();
                        name = profile.getName();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference databaseReference2 = firebaseDatabase.getReference().child("Clothes");
        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listItemArrayList = new ArrayList<ListItem>();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    ListItem listItem = dataSnapshot1.getValue(ListItem.class);
                    listItemArrayList.add(listItem);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getcurrenttime();
                StringBuilder message = new StringBuilder(10000);
                for (int i = 0; i < shoppingCartArrayList.size(); i++) {
                    if (!shoppingCartArrayList.get(i).getQuantity().equals("0")) {//user account quantity
                        for (int k = 0; k < listItemArrayList.size(); k++) {
                            if (shoppingCartArrayList.get(i).getNo().equals(listItemArrayList.get(k).getNo())) {
                                if (listItemArrayList.get(k).getQuantity().equals("0")) {
                                    message.append(shoppingCartArrayList.get(i).getName() + "\n");
                                }
                            }
                        }
                    }
                }
                if (!message.toString().equals("")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle(R.string.outofstock)
                            .setMessage(message + getString(R.string.question_order))
                            .setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    yesaction();
                                }
                            }).setNegativeButton(R.string.buynexttime, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
                }else {
                    yesaction();
                }
            }
        });

        return view;
    }

    public void yesaction() {
        final String uid = user.getUid();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        for (int i = 0; i < shoppingCartArrayList.size(); i++) {
            if (!shoppingCartArrayList.get(i).getQuantity().equals("0")) {

                order.setCompanyuid(shoppingCartArrayList.get(i).getCompanyuid());
                order.setDate(str);
                order.setDiscount(shoppingCartArrayList.get(i).getDiscount());
                order.setUid(shoppingCartArrayList.get(i).getUid());
                order.setName(shoppingCartArrayList.get(i).getName());
                order.setImage(shoppingCartArrayList.get(i).getImage());
                order.setPrice(shoppingCartArrayList.get(i).getPrice());
                order.setQuantity(shoppingCartArrayList.get(i).getQuantity());
                order.setNo(shoppingCartArrayList.get(i).getNo());
                order.setSize(shoppingCartArrayList.get(i).getSize());
                total = total + Double.parseDouble(shoppingCartArrayList.get(i).getPrice()) * Double.parseDouble(shoppingCartArrayList.get(i).getQuantity());
                order.setAddress(address);
                order.setCustomername(name);

                final int buyno = Integer.parseInt(shoppingCartArrayList.get(i).getQuantity());
                final String no = shoppingCartArrayList.get(i).getNo();
                for (int k = 0; k < listItemArrayList.size(); k++) {
                    if (listItemArrayList.get(k).getNo().equals(no)) {
                        stringquantity = listItemArrayList.get(k).getQuantity();
                        quantity = Integer.parseInt(stringquantity) - buyno;
                    }
                }
                if (stringquantity.equals("0")) {
                    quantity = 0;
                } else {
                    database.getReference("Clothes").child(no).child("quantity").setValue(String.valueOf(quantity));

                    database.getReference("PurchaseOrder").child(uid).child("Order" + str).child(String.valueOf(i)).setValue(order);

                    DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("ShoppingCart").child(user.getUid()).child(shoppingCartArrayList.get(i).getStr());
                    databaseReference1.removeValue();
                    quantity = 0;
                }
            }
        }
        Toast.makeText(getContext(), "You have already Ordered", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getContext(), MainActivity.class);
        startActivity(intent);
    }

    public void getcurrenttime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        str = format.format(date);
    }

    @Override
    public void onItemClick(int position) {
        ShoppingCart shoppingCart = shoppingCartArrayList.get(position);
        Intent intent = new Intent(getActivity().getApplication(), DetailActivity.class);
        intent.putExtra("No", shoppingCart.getNo());
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
