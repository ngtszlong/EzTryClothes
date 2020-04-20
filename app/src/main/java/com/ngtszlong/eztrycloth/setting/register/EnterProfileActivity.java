package com.ngtszlong.eztrycloth.setting.register;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

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
import com.ngtszlong.eztrycloth.MainActivity;
import com.ngtszlong.eztrycloth.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class EnterProfileActivity extends AppCompatActivity {

    private static final String TAG = "EnterProfileActivity";

    CheckBox ckb_male;
    CheckBox ckb_female;
    EditText edt_name;
    EditText edt_age;
    EditText edt_height;
    TextView txt_birth;
    EditText edt_address;
    CardView btn_submit;
    EditText edt_phone;
    CardView btn_img_front;
    CardView btn_img_side;
    ImageView img_addimage;
    DatePickerDialog.OnDateSetListener dateSetListener;

    FirebaseDatabase firebaseDatabase;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    FirebaseAuth fAuth;

    ArrayList<Profile> profileArrayList;
    String gender;

    int TAKE_IMAGE_CODE = 10001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_profile);
        ckb_male = findViewById(R.id.ckb_pro_male);
        ckb_female = findViewById(R.id.ckb_pro_female);
        edt_name = findViewById(R.id.edt_pro_name);
        edt_age = findViewById(R.id.edt_pro_age);
        edt_height = findViewById(R.id.edt_pro_height);
        txt_birth = findViewById(R.id.txt_pro_birth);
        edt_address = findViewById(R.id.edt_pro_address);
        edt_phone = findViewById(R.id.edt_pro_phone);
        btn_submit = findViewById(R.id.btn_submit);
        btn_img_front = findViewById(R.id.btn_img_front);
        btn_img_side = findViewById(R.id.btn_img_side);
        img_addimage = findViewById(R.id.img_addimage);

        ckb_male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ckb_female.setChecked(false);
            }
        });

        ckb_female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ckb_male.setChecked(false);
            }
        });

        if (ckb_male.isChecked()) {
            gender = "Male";
        } else if (ckb_female.isChecked()) {
            gender = "Female";
        }

        txt_birth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int mouth = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(EnterProfileActivity.this, R.style.Theme_AppCompat_Dialog_MinWidth, dateSetListener, year, mouth, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        btn_img_front.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EnterProfileActivity.this, TakePhotoActivity.class);
                intent.putExtra("position", "Front");
                startActivity(intent);
            }
        });

        btn_img_side.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EnterProfileActivity.this, TakePhotoActivity.class);
                intent.putExtra("position", "Side");
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
        updatefirebase();
    }

    public void handleImageClick(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, TAKE_IMAGE_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_IMAGE_CODE) {
            switch (resultCode) {
                case RESULT_OK:
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    img_addimage.setImageBitmap(bitmap);
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
                setUserProfileUrl(uri);
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
                        Toast.makeText(EnterProfileActivity.this, "Updated successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EnterProfileActivity.this, "Profile image failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void updatefirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Users");
        fAuth = FirebaseAuth.getInstance();
        firebaseUser = fAuth.getCurrentUser();
        if (firebaseUser.getPhotoUrl() != null) {
            Glide.with(this).load(firebaseUser.getPhotoUrl()).into(img_addimage);
        }
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        profileArrayList = new ArrayList<Profile>();
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            Profile profile = dataSnapshot1.getValue(Profile.class);
                            if (firebaseUser.getUid().equals(profile.getUid())) {
                                profile.setName(edt_name.getText().toString());
                                profile.setGender(gender);
                                profile.setPhone(edt_phone.getText().toString());
                                profile.setAge(edt_age.getText().toString());
                                profile.setHeight(edt_height.getText().toString());
                                profile.setBirth(txt_birth.getText().toString());
                                profile.setAddress(edt_address.getText().toString());
                                databaseReference.child(profile.getUid()).setValue(profile);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                Intent intent = new Intent(EnterProfileActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
