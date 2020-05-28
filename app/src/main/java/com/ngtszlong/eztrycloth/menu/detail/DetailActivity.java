package com.ngtszlong.eztrycloth.menu.detail;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.ngtszlong.eztrycloth.shoppingcart.ShoppingCart;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DetailActivity extends AppCompatActivity {

    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;
    public static ArrayList<ListItem> listItems;
    String No;
    CardView cv_shopcart;
    FirebaseAuth fAuth;
    FirebaseUser user;

    SimpleDateFormat format;
    Date date;
    String str;

    String color;
    String name;
    String gender;
    String price;
    String image;
    String companyuid;
    String imagetry;
    String action = "No";

    Toolbar toolbar;

    ShoppingCart shoppingCart;

    SharedPreferences preferences;
    String XL;
    String L;
    String M;
    String S;
    String XS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        preferences = getSharedPreferences("Setting", MODE_PRIVATE);

        toolbar = findViewById(R.id.tb_detail);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cv_shopcart = findViewById(R.id.cv_shopcart);

        shoppingCart = new ShoppingCart();

        Intent intent = getIntent();
        No = intent.getStringExtra("No");

        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();

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

        cv_shopcart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    mFirebaseDatabase = FirebaseDatabase.getInstance();
                    mRef = mFirebaseDatabase.getReference().child("ShoppingCart").child(user.getUid());
                    mRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                ListItem l = dataSnapshot1.getValue(ListItem.class);
                                if (l.getNo().equals(No)) {
                                    action = "Yes";
                                    Toast.makeText(DetailActivity.this, "It has already added to your Shopping Cart", Toast.LENGTH_SHORT).show();
                                }
                            }
                            if (action.equals("No")) {
                                getcurrenttime();
                                String uid = user.getUid();
                                shoppingCart.setNo(No);
                                shoppingCart.setColor(color);
                                shoppingCart.setName(name);
                                shoppingCart.setGender(gender);
                                shoppingCart.setPrice(price);
                                shoppingCart.setImage(image);
                                shoppingCart.setCompanyuid(companyuid);
                                shoppingCart.setTryimage(imagetry);
                                shoppingCart.setQuantity("1");
                                shoppingCart.setStr(str);
                                shoppingCart.setXL(XL);
                                shoppingCart.setL(L);
                                shoppingCart.setM(M);
                                shoppingCart.setS(S);
                                shoppingCart.setXS(XS);
                                shoppingCart.setSize("");
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference reference = database.getReference("ShoppingCart");
                                reference.child(uid).child(str).setValue(shoppingCart);
                                Toast.makeText(DetailActivity.this, "Added to Shopping Cart", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    Toast.makeText(DetailActivity.this, "You must login first", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void getcurrenttime() {
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        date = new Date(System.currentTimeMillis());
        str = format.format(date);
    }

    public void putdata(ListItem l) {
        String language = preferences.getString("My_Lang", "");
        ImageView image = findViewById(R.id.detail_image);
        TextView name = findViewById(R.id.detail_name);
        TextView gender = findViewById(R.id.detail_gender);
        TextView color = findViewById(R.id.detail_color);
        TextView price = findViewById(R.id.detail_price);
        TextView quantity = findViewById(R.id.detail_Quantity);
        TextView description = findViewById(R.id.detail_description);
        TextView material = findViewById(R.id.detail_material);
        TextView size = findViewById(R.id.detail_size);
        if (!l.getImage().equals("")) {
            Picasso.get().load(l.getImage()).into(image);
        }
        price.setText(l.getPrice());
        quantity.setText(l.getQuantity());
        StringBuilder stringBuilder = new StringBuilder();
        if (l.getXL().equals("Y")) {
            XL = "Y";
            stringBuilder.append("XL ");
        } else {
            XL = "N";
        }
        if (l.getL().equals("Y")) {
            L = "Y";
            stringBuilder.append("L ");
        } else {
            L = "N";
        }
        if (l.getM().equals("Y")) {
            M = "Y";
            stringBuilder.append("M ");
        } else {
            M = "N";
        }
        if (l.getS().equals("Y")) {
            S = "Y";
            stringBuilder.append("S ");
        } else {
            S = "N";
        }
        if (l.getXS().equals("Y")) {
            XS = "Y";
            stringBuilder.append("XS ");
        } else {
            XS = "N";
        }
        size.setText(stringBuilder);
        if (language.equals("en")) {
            name.setText(l.getName_Eng());
            gender.setText(l.getGender());
            color.setText(l.getColor_Eng());
            description.setText(l.getDescription_Eng());
            material.setText(l.getMaterial_Eng());
            getSupportActionBar().setTitle(l.getName_Eng());
        } else if (language.equals("zh")) {
            name.setText(l.getName_Chi());
            color.setText(l.getColor_Chi());
            description.setText(l.getDescription_Chi());
            material.setText(l.getMaterial_Chi());
            if (l.getGender().equals("MEN")) {
                gender.setText("男裝");
            } else if (l.getGender().equals("WOMEN")) {
                gender.setText("女裝");
            }
            getSupportActionBar().setTitle(l.getName_Chi());
        }
    }

    public void setfirebasedata(ListItem l) {
        companyuid = l.getUid();
        name = l.getName_Eng();
        color = l.getColor_Eng();
        gender = l.getGender();
        price = l.getPrice();
        image = l.getImage();
        imagetry = l.getTry_photo();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
