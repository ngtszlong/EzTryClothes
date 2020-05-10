package com.ngtszlong.eztrycloth.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.ngtszlong.eztrycloth.MainActivity;
import com.ngtszlong.eztrycloth.R;
import com.ngtszlong.eztrycloth.setting.register.RegisterActivity;

public class SettingFragment extends Fragment {

    Switch st_chinese;
    Switch st_english;
    Switch st_large;
    Switch st_medium;
    Switch st_small;
    FirebaseAuth firebaseAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.framgent_setting, container, false);
        getActivity().setTitle("Setting");
        Button login = view.findViewById(R.id.login);
        Button logout = view.findViewById(R.id.logout);
        Button register = view.findViewById(R.id.set_register);

        firebaseAuth = FirebaseAuth.getInstance();
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RegisterActivity.class);
                startActivity(intent);
            }
        });

        if (firebaseAuth.getCurrentUser() == null) {
            logout.setVisibility(View.GONE);
        } else {
            login.setVisibility(View.GONE);
            register.setVisibility(View.GONE);
            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth.getInstance().signOut();
                    getActivity().finish();
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                }
            });
        }

        st_chinese = view.findViewById(R.id.chinese);
        st_english = view.findViewById(R.id.english);
        st_large = view.findViewById(R.id.large);
        st_medium = view.findViewById(R.id.medium);
        st_small = view.findViewById(R.id.small);
        st_english.setChecked(true);
        st_medium.setChecked(true);

        st_chinese.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    st_english.setChecked(false);
                }
            }
        });

        st_english.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    st_chinese.setChecked(false);
                }
            }
        });

        st_large.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    st_medium.setChecked(false);
                    st_small.setChecked(false);
                }
            }
        });

        st_medium.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    st_large.setChecked(false);
                    st_small.setChecked(false);
                }
            }
        });

        st_small.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    st_large.setChecked(false);
                    st_medium.setChecked(false);
                }
            }
        });

        return view;
    }
}
