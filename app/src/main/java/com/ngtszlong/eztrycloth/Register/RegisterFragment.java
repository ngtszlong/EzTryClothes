package com.ngtszlong.eztrycloth.Register;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


public class RegisterFragment extends Fragment {
    private FirebaseAuth fAuth;
    ProgressDialog progressDialog;
    Profile profile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        getActivity().setTitle(getText(R.string.register));

        fAuth = FirebaseAuth.getInstance();

        final EditText edt_email = view.findViewById(R.id.edt_email);
        final EditText edt_pw = view.findViewById(R.id.edt_pw);
        final EditText edt_pwc = view.findViewById(R.id.edt_pwc);
        CardView btn_register = view.findViewById(R.id.btn_reg_register);
        progressDialog = new ProgressDialog(getContext());
        profile = new Profile();

        if (fAuth.getCurrentUser() != null) {
            Toast.makeText(getContext(), "You have already login", Toast.LENGTH_LONG).show();
            startActivity(new Intent(getActivity().getApplicationContext(), MainActivity.class));
            getActivity().finish();
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
                                profile.setHeight("160");
                                profile.setBirth("");
                                profile.setAddress("");
                                profile.setPhone("");
                                profile.setFront("");
                                profile.setSide("");
                                profile.setWeight("40");
                                profile.setId("");
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference reference = database.getReference("Users");
                                reference.child(uid).setValue(profile);

                                startActivity(new Intent(getActivity().getApplicationContext(), MainActivity.class));
                            } else {
                                Toast.makeText(getContext(), "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                System.out.println(task.getException().getMessage());
                            }
                        }
                    });
                }
            }
        });
        return view;
    }
}
