package com.ngtszlong.eztrycloth.FittingRoom;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Matrix;
import android.graphics.Point;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ngtszlong.eztrycloth.R;
import com.ngtszlong.eztrycloth.Profile.Profile;
import com.ngtszlong.eztrycloth.shoppingcart.ShoppingCart;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class FittingRoomFragment extends Fragment implements TryAdapter.OnItemClickListener {
    private RecyclerView rv_clothes;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    private ArrayList<ShoppingCart> shoppingCartArrayList;
    private TryAdapter tryAdapter;
    private FirebaseAuth fAuth;
    private FirebaseUser user;

    private ViewGroup mainlayout;
    private RelativeLayout relativeLayout;

    private int xDelta;
    private int yDelta;

    private ImageView img_background;

    private Matrix matrix = new Matrix();
    private Float scale = 1f;
    private ScaleGestureDetector SGD;
    ImageView imageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle(getText(R.string.FittingRoom));
        LoadLocale();
        View v = inflater.inflate(R.layout.fragment_fitting_room, container, false);

        rv_clothes = v.findViewById(R.id.rv_clothes);
        rv_clothes.setHasFixedSize(true);
        rv_clothes.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        relativeLayout = v.findViewById(R.id.rl_background);
        img_background = v.findViewById(R.id.img_background);

        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        getImage();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference().child("ShoppingCart").child(user.getUid());
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                shoppingCartArrayList = new ArrayList<>();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    ShoppingCart shoppingCart = dataSnapshot1.getValue(ShoppingCart.class);
                    shoppingCartArrayList.add(shoppingCart);
                }
                tryAdapter = new TryAdapter(getContext(), shoppingCartArrayList);
                rv_clothes.setAdapter(tryAdapter);
                tryAdapter.setOnItemClickListener(FittingRoomFragment.this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mRef.keepSynced(true);
        mainlayout = (RelativeLayout) v.findViewById(R.id.rl_background);

        SGD = new ScaleGestureDetector(getContext(), new ScaleListener());
        return v;
    }

    private void getImage() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference().child("Users");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Profile profile = dataSnapshot1.getValue(Profile.class);
                    if (user.getUid().equals(profile.getUid())) {
                        Picasso.get().load(profile.getFront()).into(img_background);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mRef.keepSynced(true);
    }

    private View.OnTouchListener onTouchListener() {
        return new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                SGD.onTouchEvent(event);
                final int x = (int) event.getRawX();
                final int y = (int) event.getRawY();

                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                        xDelta = x - layoutParams.leftMargin;
                        yDelta = y - layoutParams.topMargin;
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                        int[] imagelocation = new int[2];
                        imageView.getLocationOnScreen(imagelocation);
                        lParams.leftMargin = x - xDelta;
                        lParams.topMargin = y - yDelta;
                        lParams.rightMargin = 0;
                        lParams.bottomMargin = 0;
                        view.setLayoutParams(lParams);
                        break;
                }
                mainlayout.invalidate();
                return true;
            }
        };
    }

    @Override
    public void onItemClick(int position) {
        ShoppingCart shoppingCart = shoppingCartArrayList.get(position);
        imageView = new ImageView(getContext());
        Picasso.get().load(shoppingCart.getTryimage()).resize(500, 666).into(imageView);
        imageView.setOnTouchListener(onTouchListener());
        imageView.setLayoutParams(new RelativeLayout.LayoutParams(700, 700));
        imageView.setScaleType(ImageView.ScaleType.MATRIX);
        relativeLayout.addView(imageView);
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scale = scale * detector.getScaleFactor();
            scale = Math.max(0.1f, Math.min(scale, 5f));
            matrix.setScale(scale, scale);
            imageView.setImageMatrix(matrix);
            return true;
        }
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
