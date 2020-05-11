package com.ngtszlong.eztrycloth.Profile;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ngtszlong.eztrycloth.setting.ChangePwActivity;
import com.ngtszlong.eztrycloth.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";

    private ImageView img_front;
    private ImageView img_side;
    private EditText edt_name;
    private EditText edt_phone;
    private EditText edt_age;
    private EditText edt_height;
    private TextView txt_birth;
    private EditText edt_address;
    private EditText edt_weight;
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
    RadioGroup rg_gender;
    RadioButton rb_male;
    RadioButton rb_female;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Profile");
        final View view = inflater.inflate(R.layout.framgent_profile, container, false);
        img_front = view.findViewById(R.id.img_mpro_front);
        img_side = view.findViewById(R.id.img_mpro_side);
        edt_name = view.findViewById(R.id.edt_mpro_name);
        edt_phone = view.findViewById(R.id.edt_mpro_phone);
        edt_age = view.findViewById(R.id.edt_mpro_age);
        edt_height = view.findViewById(R.id.edt_mpro_height);
        edt_weight = view.findViewById(R.id.edt_mpro_weight);
        txt_birth = view.findViewById(R.id.txt_mpro_birth);
        edt_address = view.findViewById(R.id.edt_mpro_address);
        rg_gender = view.findViewById(R.id.rg_mpro_gender);
        rb_male = view.findViewById(R.id.rb_mpro_male);
        rb_female = view.findViewById(R.id.rb_mpro_female);
        CardView btn_update = view.findViewById(R.id.btn_mpro_update);
        CardView btn_change = view.findViewById(R.id.btn_mpro_changepw);
        getdata();

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(edt_height.getText().toString().trim()) < 150){
                    edt_height.setError("The minimum height is 150cm");
                }else if (Integer.parseInt(edt_height.getText().toString().trim()) > 200){
                    edt_height.setError("The maximum height is 200cm");
                }else{
                    updatedata();
                }
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

        edt_height.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (Integer.parseInt(edt_height.getText().toString().trim()) < 150){
                    edt_height.setError("The minimum height is 150cm");
                }else if (Integer.parseInt(edt_height.getText().toString().trim()) > 200){
                    edt_height.setError("The maximum height is 200cm");
                }
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
        if (rb_male.isChecked()) {
            gender = "Male";
        } else if (rb_female.isChecked()) {
            gender = "Female";
        }
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
                        profile.setGender(gender);
                        profile.setWeight(edt_weight.getText().toString());
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
                            rb_male.setChecked(true);
                        } else if (profile.getGender().equals("Female")) {
                            rb_female.setChecked(true);
                        }
                        edt_address.setText(profile.getEmail());
                        edt_name.setText(profile.getName());
                        edt_phone.setText(profile.getPhone());
                        edt_age.setText(profile.getAge());
                        edt_height.setText(profile.getHeight());
                        edt_weight.setText(profile.getWeight());
                        txt_birth.setText(profile.getBirth());
                        edt_address.setText(profile.getAddress());
                        if (!profile.getFront().equals("")) {
                            Picasso.get().load(profile.getFront()).into(img_front);
                        }
                        if (!profile.getSide().equals("")) {
                            Picasso.get().load(profile.getSide()).into(img_side);
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

    public void handlefrontclick(View view) {
        action = "front";
        alertdialog();
    }

    public void handlesideclick(View view) {
        action = "side";
        alertdialog();
    }

    public void alertdialog() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivityForResult(intent, 1);
                    }
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            switch (resultCode) {
                case RESULT_OK:
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    handleUpload(bitmap);
                    if (action.equals("front")){
                        img_front.setImageBitmap(bitmap);
                    }else if (action.equals("side")){
                        img_side.setImageBitmap(bitmap);
                    }
            }
        } else if (requestCode == 2) {
            switch (resultCode) {
                case RESULT_OK:
                    Uri selectedImage = data.getData();
                    try {
                        String[] orientationColumn = {MediaStore.Images.Media.ORIENTATION};
                        Cursor cur = getActivity().managedQuery(selectedImage, orientationColumn, null, null, null);
                        int orientation = -1;
                        if (cur != null && cur.moveToFirst()) {
                            orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]));
                        }
                        InputStream imageStream = getActivity().getContentResolver().openInputStream(selectedImage);
                        Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                        switch(orientation) {
                            case 90:
                                bitmap = rotateImage(bitmap, 90);
                                break;
                            case 180:
                                bitmap = rotateImage(bitmap, 180);
                                break;
                            case 270:
                                bitmap = rotateImage(bitmap, 270);
                                break;
                            default:
                                break;
                        }
                        //Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                        handleUpload(bitmap);
                        if (action.equals("front")){
                            img_front.setImageBitmap(bitmap);
                        }else if (action.equals("side")){
                            img_side.setImageBitmap(bitmap);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix,
                true);
    }

    private void handleUpload(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://eztryclothes-3b490.appspot.com");
        final StorageReference reference = storageReference
                .child(action)
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
                if (action.equals("front")) {
                    front = uri.toString();
                } else if (action.equals("side")) {
                    side = uri.toString();
                }

            }
        });
    }
}
