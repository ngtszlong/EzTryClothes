package com.ngtszlong.eztrycloth;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ngtszlong.eztrycloth.setting.register.Profile;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";

    private ImageView img_proimage;
    private ImageView img_front;
    private ImageView img_side;
    private TextView txt_male;
    private TextView txt_female;
    private EditText edt_name;
    private EditText edt_phone;
    private EditText edt_age;
    private EditText edt_height;
    private TextView txt_birth;
    private EditText edt_address;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private static ArrayList<Profile> profileArrayList;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    String gender;
    String action;
    String front;
    String side;

    private int TAKE_IMAGE_CODE = 10001;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Profile");
        final View view = inflater.inflate(R.layout.framgent_profile, container, false);
        img_proimage = view.findViewById(R.id.img_mpro_image);
        img_front = view.findViewById(R.id.img_mpro_front);
        img_side = view.findViewById(R.id.img_mpro_side);
        txt_male = view.findViewById(R.id.txt_mpro_male);
        txt_female = view.findViewById(R.id.txt_mpro_female);
        edt_name = view.findViewById(R.id.edt_mpro_name);
        edt_phone = view.findViewById(R.id.edt_mpro_phone);
        edt_age = view.findViewById(R.id.edt_mpro_age);
        edt_height = view.findViewById(R.id.edt_mpro_height);
        txt_birth = view.findViewById(R.id.txt_mpro_birth);
        edt_address = view.findViewById(R.id.edt_mpro_address);
        CardView btn_update = view.findViewById(R.id.btn_mpro_update);
        CardView btn_change = view.findViewById(R.id.btn_mpro_changepw);
        getdata();

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatedata();
            }
        });
        btn_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChangePwActivity.class);
                startActivity(intent);
            }
        });
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = month + "/" + dayOfMonth + "/" + year;
                txt_birth.setText(date);
            }
        };
        img_proimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleImageClick(v);
            }
        });

        img_front.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlefrontclick(v);
            }
        });

        img_side.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlesideclick(v);
            }
        });

        txt_birth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int mouth = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(getActivity(), R.style.Theme_AppCompat_Dialog_MinWidth, dateSetListener, year, mouth, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        return view;
    }

    private void updatedata() {
        firebaseAuth = FirebaseAuth.getInstance();
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
                        profile.setName(edt_name.getText().toString());
                        profile.setPhone(edt_phone.getText().toString());
                        profile.setAge(edt_age.getText().toString());
                        profile.setHeight(edt_height.getText().toString());
                        profile.setBirth(txt_birth.getText().toString());
                        profile.setAddress(edt_address.getText().toString());
                        profile.setFront(front);
                        profile.setSide(side);
                        databaseReference.child(profile.getUid()).setValue(profile);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Toast.makeText(getContext(), "Data Successfully Updated", Toast.LENGTH_SHORT).show();
    }

    private void getdata() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser.getPhotoUrl() != null) {
            Glide.with(this).load(firebaseUser.getPhotoUrl()).into(img_proimage);
        }
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                profileArrayList = new ArrayList<Profile>();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Profile profile = dataSnapshot1.getValue(Profile.class);
                    if (firebaseUser.getUid().equals(profile.getUid())) {
                        profileArrayList.add(profile);
                        if (profile.getGender().equals("Male")) {
                            gender = profile.getGender();
                            txt_female.setVisibility(View.GONE);
                        } else if (profile.getGender().equals("Female")) {
                            gender = profile.getGender();
                            txt_male.setVisibility(View.GONE);
                        }
                        edt_address.setText(profile.getEmail());
                        edt_name.setText(profile.getName());
                        edt_phone.setText(profile.getPhone());
                        edt_age.setText(profile.getAge());
                        edt_height.setText(profile.getHeight());
                        txt_birth.setText(profile.getBirth());
                        edt_address.setText(profile.getAddress());
                        Picasso.get().load(profile.getFront()).into(img_front);
                        Picasso.get().load(profile.getSide()).into(img_side);
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

    public void handleImageClick(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        action = "profileImages";
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, TAKE_IMAGE_CODE);
        }
    }

    public void handlefrontclick(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        action = "front";
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, TAKE_IMAGE_CODE);
        }
    }

    public void handlesideclick(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        action = "side";
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, TAKE_IMAGE_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_IMAGE_CODE) {
            switch (resultCode) {
                case RESULT_OK:
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    if (action.equals("profileImages")) {
                        img_proimage.setImageBitmap(bitmap);
                    }
                    handleUpload(bitmap);
            }
        }
    }

    private void handleUpload(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://eztryclothes-3b490.appspot.com");
        final StorageReference reference = storageReference
                .child("profileImages")
                .child(uid + ".jpeg");

        reference.putBytes(baos.toByteArray())
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        getDownloadUrl(reference);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: ", e.getCause());
                    }
                });
    }

    private void getDownloadUrl(StorageReference reference) {
        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d(TAG, "onSuccess: " + uri);
                if (action.equals("profileImages")) {
                    setUserProfileUrl(uri);
                } else {
                    if (action.equals("front")){
                        front = uri.toString();
                    }else if (action.equals("side")){
                        side = uri.toString();
                    }
                }
            }
        });
    }

    private void setUserProfileUrl(Uri url) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setPhotoUri(url)
                .build();

        user.updateProfile(request)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Updated successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Profile image failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
