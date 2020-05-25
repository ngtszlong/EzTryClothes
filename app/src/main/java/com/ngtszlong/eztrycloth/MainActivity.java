package com.ngtszlong.eztrycloth;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
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
import com.ngtszlong.eztrycloth.Order.OrderFragment;
import com.ngtszlong.eztrycloth.Profile.ProfileFragment;
import com.ngtszlong.eztrycloth.menu.MenuFragment;
import com.ngtszlong.eztrycloth.Login.LoginFragment;
import com.ngtszlong.eztrycloth.Register.RegisterFragment;
import com.ngtszlong.eztrycloth.Profile.Profile;
import com.ngtszlong.eztrycloth.shoppingcart.ShoppingCartFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    TextView phone;
    TextView name;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseUser firebaseUser;
    ArrayList<Profile> profileArrayList;
    String username;
    String phonenumber;
    ImageView image;
    String front;
    String side;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LoadLocale();
        SharedPreferences pref = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean firststart = pref.getBoolean("firstStart", true);
        if (firststart) {
            showStartDialog();
            FirebaseAuth.getInstance().signOut();
        }
        checkPermission();
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        phone = headerView.findViewById(R.id.header_phone);
        name = headerView.findViewById(R.id.header_name);
        image = headerView.findViewById(R.id.header_image);

        Menu menu = navigationView.getMenu();
        if (firebaseAuth.getCurrentUser() != null) {
            for (int i = 0; i < menu.findItem(R.id.nav_help).getSubMenu().size(); i++) {
                if (menu.findItem(R.id.nav_help).getSubMenu().getItem(i).getItemId() == R.id.nav_login) {
                    menu.findItem(R.id.nav_help).getSubMenu().getItem(i).setVisible(false);
                }
                if (menu.findItem(R.id.nav_help).getSubMenu().getItem(i).getItemId() == R.id.nav_register) {
                    menu.findItem(R.id.nav_help).getSubMenu().getItem(i).setVisible(false);
                }
            }
        } else {
            firebaseAuth.getCurrentUser();
            for (int i = 0; i < menu.findItem(R.id.nav_help).getSubMenu().size(); i++) {
                if (menu.findItem(R.id.nav_help).getSubMenu().getItem(i).getItemId() == R.id.nav_logout) {
                    menu.findItem(R.id.nav_help).getSubMenu().getItem(i).setVisible(false);
                }
            }
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MenuFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_menu);
        }
        setHeaderInfo();
        if (firebaseAuth.getCurrentUser() != null) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    profileArrayList = new ArrayList<Profile>();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        Profile profile = dataSnapshot1.getValue(Profile.class);
                        if (firebaseUser.getUid().equals(profile.getUid())) {
                            profileArrayList.add(profile);
                            if (!profile.getFront().equals("")) {
                                front = profile.getFront();
                            }
                            if (!profile.getSide().equals("")) {
                                side = profile.getSide();
                            }
                            front = profile.getFront();
                            side = profile.getSide();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.Chinese:
                setLocale("zh");
                finish();
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.English:
                setLocale("en");
                finish();
                startActivity(new Intent(this, MainActivity.class));
                break;
        }
        return true;
    }

    public void setHeaderInfo() {
        if (firebaseAuth.getCurrentUser() != null) {
            firebaseUser = firebaseAuth.getCurrentUser();
            firebaseDatabase = FirebaseDatabase.getInstance();
            firebaseDatabase.setPersistenceEnabled(true);
            DatabaseReference databaseReference = firebaseDatabase.getReference().child("Users");
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
                                name.setText(getText(R.string.Username) + username);
                            } else {
                                name.setText("You need to complete your profile");
                            }
                            if (!phonenumber.isEmpty()) {
                                phone.setText(getText(R.string.PhoneNumber) + phonenumber);
                            } else {
                                phone.setText("You need to complete your profile");
                            }
                            if (!profile.getFront().equals("")) {
                                Picasso.get().load(profile.getFront()).into(image);
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_menu:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MenuFragment()).commit();
                break;
            case R.id.nav_straighten:
                if (firebaseAuth.getCurrentUser() == null) {
                    Toast.makeText(this, R.string.logininfirst, Toast.LENGTH_SHORT).show();
                } else if (front.equals("") || side.equals("")) {
                    Toast.makeText(this, "You need to upload front and side image", Toast.LENGTH_SHORT).show();
                } else {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MeasureFragment()).commit();
                }
                break;
            case R.id.nav_fitting:
                if (firebaseAuth.getCurrentUser() == null) {
                    Toast.makeText(this, R.string.logininfirst, Toast.LENGTH_SHORT).show();
                } else {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FittingRoomFragment()).commit();
                }
                break;
            case R.id.nav_shoppingCart:
                if (firebaseAuth.getCurrentUser() == null) {
                    Toast.makeText(this, R.string.logininfirst, Toast.LENGTH_SHORT).show();
                } else {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ShoppingCartFragment()).commit();
                }
                break;
            case R.id.nav_order:
                if (firebaseAuth.getCurrentUser() == null) {
                    Toast.makeText(this, R.string.logininfirst, Toast.LENGTH_SHORT).show();
                } else {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new OrderFragment()).commit();
                }
                break;
            case R.id.nav_profile:
                if (firebaseAuth.getCurrentUser() == null) {
                    Toast.makeText(this, R.string.logininfirst, Toast.LENGTH_SHORT).show();
                } else {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                }
                break;
            case R.id.nav_login:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LoginFragment()).commit();
                break;
            case R.id.nav_logout:
                firebaseAuth.signOut();
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            case R.id.nav_register:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new RegisterFragment()).commit();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
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

    private void showStartDialog() {
        final String[] listItems = {"中文", "English"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("選擇語言 Choose Language");
        builder.setCancelable(false);
        builder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    setLocale("zh");
                    finish();
                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                    firststart();
                }
                if (which == 1) {
                    setLocale("en");
                    finish();
                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                    firststart();
                }
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void firststart() {
        SharedPreferences pref = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("firstStart", false);
        editor.apply();
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getSharedPreferences("Setting", MODE_PRIVATE).edit();
        editor.putString("My_Lang", lang);
        editor.apply();
    }

    public void LoadLocale() {
        SharedPreferences preferences = getSharedPreferences("Setting", MODE_PRIVATE);
        String language = preferences.getString("My_Lang", "");
        setLocale(language);
    }
}
