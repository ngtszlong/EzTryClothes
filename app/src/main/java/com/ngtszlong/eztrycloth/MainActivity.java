package com.ngtszlong.eztrycloth;

import android.Manifest;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.ngtszlong.eztrycloth.FittingRoom.FittingRoomFragment;
import com.ngtszlong.eztrycloth.Measure.MeasureFragment;
import com.ngtszlong.eztrycloth.Profile.ProfileFragment;
import com.ngtszlong.eztrycloth.menu.MenuFragment;
import com.ngtszlong.eztrycloth.setting.SettingFragment;
import com.ngtszlong.eztrycloth.Profile.Profile;
import com.ngtszlong.eztrycloth.shoppingcart.ShoppingCartFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    TextView phone;
    TextView name;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    FirebaseUser firebaseUser;
    ArrayList<Profile> profileArrayList;
    String username;
    String phonenumber;
    ImageView image;
    SharedPreferences sharedPreferences;
    boolean aBoolean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
        /*SharedPreferences.Editor editor = getSharedPreferences("profiledata", MODE_PRIVATE).edit();
        editor.putBoolean("info", false);
        editor.apply();
        sharedPreferences = getSharedPreferences("profiledata", MODE_PRIVATE);
        aBoolean = sharedPreferences.getBoolean("info", false);*/

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        phone = headerView.findViewById(R.id.header_phone);
        name = headerView.findViewById(R.id.header_name);
        image = headerView.findViewById(R.id.header_image);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MenuFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_menu);
        }
        setHeaderInfo();
    }

    public void setHeaderInfo() {
        if (firebaseAuth.getCurrentUser() != null) {
            firebaseUser = firebaseAuth.getCurrentUser();
            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference().child("Users");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    profileArrayList = new ArrayList<Profile>();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        Profile profile = dataSnapshot1.getValue(Profile.class);
                        if (firebaseUser.getUid().equals(profile.getUid())) {
                            username = String.valueOf(profile.getName());
                            phonenumber = String.valueOf(profile.getPhone());
                            if (!username.isEmpty()) {
                                name.setText("Username : " + username);
                            } else {
                                name.setText("You need to complete your profile");
                            }
                            if (!phonenumber.isEmpty()) {
                                phone.setText("Phone number : " + phonenumber);
                            } else {
                                phone.setText("You need to complete your profile");
                            }
                            Picasso.get().load(profile.getFront()).into(image);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_menu:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MenuFragment()).commit();
                break;
            case R.id.nav_straighten:
                if (firebaseAuth.getCurrentUser() == null) {
                    Toast.makeText(this, "You need to login in Setting first", Toast.LENGTH_SHORT).show();
                } else {
                    //checkData();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MeasureFragment()).commit();
                }
                break;
            case R.id.nav_fitting:
                if (firebaseAuth.getCurrentUser() == null) {
                    Toast.makeText(this, "You need to login in Setting first", Toast.LENGTH_SHORT).show();
                } else {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FittingRoomFragment()).commit();
                }
                break;
            case R.id.nav_shoppingCart:
                if (firebaseAuth.getCurrentUser() == null) {
                    Toast.makeText(this, "You need to login in Setting first", Toast.LENGTH_SHORT).show();
                } else {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ShoppingCartFragment()).commit();
                }
                break;
            case R.id.nav_profile:
                if (firebaseAuth.getCurrentUser() == null) {
                    Toast.makeText(this, "You need to login in Setting first", Toast.LENGTH_SHORT).show();
                } else {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                }
                break;
            case R.id.nav_setting:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingFragment()).commit();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void checkData() {
        if (firebaseAuth.getCurrentUser() != null) {
            firebaseUser = firebaseAuth.getCurrentUser();
            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference().child("Users");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        Profile profile = dataSnapshot1.getValue(Profile.class);
                        if (firebaseUser.getUid().equals(profile.getUid())) {
                            if (!profile.getFront().equals("") || !profile.getSide().equals("") || !profile.getGender().equals("") || !profile.getHeight().equals("")) {
                                SharedPreferences.Editor editor = getSharedPreferences("profiledata", MODE_PRIVATE).edit();
                                editor.putBoolean("info", true);
                                editor.apply();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void checkPermission() {
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(MainActivity.this, "Permission not given", Toast.LENGTH_LONG).show();
            }
        };
        TedPermission.with(MainActivity.this).setPermissionListener(permissionListener).setPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE).setGotoSettingButton(true).check();
    }
}
