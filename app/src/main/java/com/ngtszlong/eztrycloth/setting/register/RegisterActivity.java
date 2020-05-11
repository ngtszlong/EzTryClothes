package com.ngtszlong.eztrycloth.setting.register;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ngtszlong.eztrycloth.MainActivity;
import com.ngtszlong.eztrycloth.Profile.Profile;
import com.ngtszlong.eztrycloth.R;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth fAuth;
    ProgressDialog progressDialog;
    Profile profile;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        toolbar = findViewById(R.id.tb_register);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Register");

        fAuth = FirebaseAuth.getInstance();

        final EditText edt_email = findViewById(R.id.edt_email);
        final EditText edt_pw = findViewById(R.id.edt_pw);
        final EditText edt_pwc = findViewById(R.id.edt_pwc);
        CardView btn_register = findViewById(R.id.btn_reg_register);
        progressDialog = new ProgressDialog(this);
        profile = new Profile();

        if (fAuth.getCurrentUser() != null) {
            Toast.makeText(this, "You have already login", Toast.LENGTH_LONG).show();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edt_email.getText().toString().trim();
                String pw = edt_pw.getText().toString().trim();
                String pwc = edt_pwc.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    edt_email.setError("Email is Required");
                } else if (TextUtils.isEmpty(pw)) {
                    edt_pw.setError("Password must be more than 8 words");
                } else if (!pw.equals(pwc)) {
                    edt_pwc.setError("It must be same as the password");
                } else {
                    progressDialog.setMessage("Registering Please Wait...");
                    progressDialog.show();
                    fAuth.createUserWithEmailAndPassword(email, pw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                FirebaseUser user = fAuth.getCurrentUser();
                                String email = user.getEmail();
                                String uid = user.getUid();
                                profile.setUid(uid);
                                profile.setEmail(email);
                                profile.setName("");
                                profile.setGender("");
                                profile.setAge("");
                                profile.setHeight("");
                                profile.setBirth("");
                                profile.setAddress("");
                                profile.setPhone("");
                                profile.setFront("");
                                profile.setSide("");
                                profile.setWeight("");
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference reference = database.getReference("Users");
                                reference.child(uid).setValue(profile);

                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            } else {
                                Toast.makeText(RegisterActivity.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                System.out.println(task.getException().getMessage());
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
