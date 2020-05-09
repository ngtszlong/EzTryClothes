package com.ngtszlong.eztrycloth;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
import com.ngtszlong.eztrycloth.wishlist.Wishlist;
import com.ngtszlong.eztrycloth.wishlist.WishlistAdapter;

import java.util.ArrayList;

public class FittingRoomFragment extends Fragment implements WishlistAdapter.OnItemClickListener {
    RecyclerView rv_clothes;

    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;
    private ArrayList<Wishlist> wishlistArrayList;
    private TryAdapter tryAdapter;
    private FirebaseAuth fAuth;
    private FirebaseUser user;

    private ViewGroup mainlayout;
    private ImageView moveimage;

    private int xDelta;
    private int yDelta;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle("Fitting Room");
        View v = inflater.inflate(R.layout.fragment_fitting_room, container, false);

        rv_clothes = v.findViewById(R.id.rv_clothes);
        rv_clothes.setHasFixedSize(true);
        rv_clothes.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference().child("Wishlist").child(user.getUid());
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                wishlistArrayList = new ArrayList<>();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Wishlist wishlist = dataSnapshot1.getValue(Wishlist.class);
                    wishlistArrayList.add(wishlist);
                }
                tryAdapter = new TryAdapter(getContext(), wishlistArrayList);
                rv_clothes.setAdapter(tryAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mRef.keepSynced(true);

        mainlayout = (RelativeLayout) v.findViewById(R.id.rl_background);
        moveimage = (ImageView) v.findViewById(R.id.moveimage);

        moveimage.setOnTouchListener(onTouchListener(v));

        return v;
    }

    private View.OnTouchListener onTouchListener(View v) {
        return new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
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

    }
}
