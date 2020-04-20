package com.ngtszlong.eztrycloth.setting.register;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.ngtszlong.eztrycloth.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TakePhotoActivity extends AppCompatActivity {

    Camera camera;
    FrameLayout frameLayout;
    ShowCamera showCamera;
    Button btn_capture;
    int i = 1;

    FirebaseStorage storage;
    StorageReference storageReference;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);
        checkPermission();

        frameLayout = findViewById(R.id.framelayout);
        camera = Camera.open();
        showCamera = new ShowCamera(this, camera);
        frameLayout.addView(showCamera);
        btn_capture = findViewById(R.id.btn_capture);


        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        btn_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage(v);
            }
        });
    }

    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File picture_file = getOutputMediaFile();
            if (picture_file == null) {
                return;
            } else {
                try {
                    FileOutputStream fos = new FileOutputStream(picture_file);
                    fos.write(data);
                    fos.close();
                    i++;
                    if (i > 2) {
                        Intent intent = new Intent(TakePhotoActivity.this, EnterProfileActivity.class);
                        startActivity(intent);
                    }
                    camera.startPreview();
                    //safeToTakePicture = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private File getOutputMediaFile() {
        String state = Environment.getExternalStorageState();
        if (!state.equals(Environment.MEDIA_MOUNTED)) {
            return null;
        } else {
            File folder_gui = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "");

            if (!folder_gui.exists()) {
                folder_gui.mkdirs();
            }

            File outputFile = new File(folder_gui, "profile" + i + ".jpg");
            try {
                outputFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return outputFile;
        }
    }

    public void captureImage(View v) {
        if (camera != null) {
            camera.takePicture(null, null, mPictureCallback);
        }
    }

    public void checkPermission() {
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                checkPermission();
                Toast.makeText(TakePhotoActivity.this, "Permission not given", Toast.LENGTH_LONG).show();
            }
        };
        TedPermission.with(TakePhotoActivity.this).setPermissionListener(permissionListener).setPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE).setGotoSettingButton(true).check();
    }
}
