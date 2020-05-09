package com.ngtszlong.eztrycloth.menu.detail;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ngtszlong.eztrycloth.R;
import com.ngtszlong.eztrycloth.menu.list.ListItem;
import com.ngtszlong.eztrycloth.wishlist.Wishlist;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DetailActivity extends AppCompatActivity {

    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;
    public static ArrayList<ListItem> listItems;
    String No;
    CardView cv_wishlist;
    CardView cv_shopcart;
    CardView cv_try;
    FirebaseAuth fAuth;
    FirebaseUser user;
    Wishlist wishlist;

    SimpleDateFormat format;
    Date date;
    String str;

    String color;
    String name;
    String gender;
    String price;
    String image;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        toolbar = findViewById(R.id.tb_detail);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cv_wishlist = findViewById(R.id.cv_wishlist);
        cv_shopcart = findViewById(R.id.cv_shopcart);

        Intent intent = getIntent();
        No = intent.getStringExtra("No");

        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        wishlist = new Wishlist();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference().child("Clothes");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listItems = new ArrayList<ListItem>();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    ListItem l = dataSnapshot1.getValue(ListItem.class);
                    if (No.equals(l.getNo())) {
                        listItems.add(l);
                        putdata(l);
                        setfirebasedata(l);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        cv_wishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getcurrenttime();
                String uid = user.getUid();
                wishlist.setNo(No);
                wishlist.setColor(color);
                wishlist.setName(name);
                wishlist.setGender(gender);
                wishlist.setPrice(price);
                wishlist.setImage(image);
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference reference = database.getReference("Wishlist");
                reference.child(uid).child(str).setValue(wishlist);
                Toast.makeText(DetailActivity.this, "Added to WishList", Toast.LENGTH_SHORT).show();
            }
        });

        cv_shopcart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getcurrenttime();
                String uid = user.getUid();
                wishlist.setNo(No);
                wishlist.setColor(color);
                wishlist.setName(name);
                wishlist.setGender(gender);
                wishlist.setPrice(price);
                wishlist.setImage(image);
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference reference = database.getReference("ShoppingCart");
                reference.child(uid).child(str).setValue(wishlist);
                Toast.makeText(DetailActivity.this, "Added to Shopping Cart", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getcurrenttime() {
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        date = new Date(System.currentTimeMillis());
        str = format.format(date);
    }

    public void putdata(ListItem l) {
        ImageView image = findViewById(R.id.detail_image);
        TextView name = findViewById(R.id.detail_name);
        TextView gender = findViewById(R.id.detail_gender);
        TextView color = findViewById(R.id.detail_color);
        TextView price = findViewById(R.id.detail_price);
        TextView quantity = findViewById(R.id.detail_Quantity);
        TextView description = findViewById(R.id.detail_description);
        TextView material = findViewById(R.id.detail_material);
        Picasso.get().load(l.getImage()).into(image);
        name.setText(l.getName_Eng());
        gender.setText(l.getGender());
        color.setText(l.getColor_Eng());
        price.setText(l.getPrice());
        quantity.setText(l.getQuantity());
        description.setText(l.getDescription_Eng());
        material.setText(l.getMaterial_Eng());
        getSupportActionBar().setTitle(l.getName_Eng());
    }

    public void setfirebasedata(ListItem l) {
        name = l.getName_Eng();
        color = l.getColor_Eng();
        gender = l.getGender();
        price = l.getPrice();
        image = l.getImage();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
