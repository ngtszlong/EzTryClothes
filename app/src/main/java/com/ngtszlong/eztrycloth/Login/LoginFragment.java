package com.ngtszlong.eztrycloth.Login;

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
import com.ngtszlong.eztrycloth.MainActivity;
import com.ngtszlong.eztrycloth.R;

public class LoginFragment extends Fragment {
    private FirebaseAuth fAuth;
    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        getActivity().setTitle(getText(R.string.Login));
        fAuth = FirebaseAuth.getInstance();

        CardView btn_login = view.findViewById(R.id.btn_login);
        final EditText edt_email = view.findViewById(R.id.edt_login_email);
        final EditText edt_pw = view.findViewById(R.id.edt_login_pw);
        progressDialog = new ProgressDialog(getContext());

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = edt_email.getText().toString();
                String password = edt_pw.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    edt_email.setError("Email is Required");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    edt_pw.setError("Password must be more than 8 words");
                    return;
                }

                progressDialog.setMessage("Logging in, Please wait...");
                progressDialog.show();

                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            Intent intent = new Intent(getContext(), MainActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                        } else {
                            Toast.makeText(getContext(), "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            System.out.println(task.getException().getMessage());
                        }
                    }
                });
            }
        });

        return view;
    }
}
