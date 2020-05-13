package com.ngtszlong.eztrycloth.Measure;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ngtszlong.eztrycloth.R;
import com.ngtszlong.eztrycloth.Profile.Profile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class MeasureFragment extends Fragment {
    private String url = "https://saia.3dlook.me/api/v2/persons/?measurements_type=all";
    private String key = "APIKey b116dc1b90b7a485f53d349d20c814dc5e90c120";

    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private String gender;
    private String front;
    private String side;
    private String height;
    private String weight;

    private RequestQueue queue;

    private TextView txt_Bustgirth;
    private TextView txt_Waistgirth;
    private TextView txt_Hipgirth;
    private TextView txt_Backneckheight;
    private TextView txt_Necktoupperhiplength;
    private TextView txt_Frontshoulderwidth;
    private TextView txt_Acrossbackshoulderwidth;
    private TextView txt_Acrossbackwidth;
    private TextView txt_Shoulderlength;
    private TextView txt_Chestwidth;
    private TextView txt_Underbustgirth;
    private TextView txt_Upperchestgirth;
    private TextView txt_Abdomengirth;
    private TextView txt_Backshoulderwidth;
    private TextView txt_Neckgirth;
    private TextView txt_Upperarmgirth;
    private TextView txt_Outerarmlength;
    private TextView txt_Jacketlength;
    private TextView txt_Torsoheight;
    private TextView txt_Backnecktohiplength;
    private TextView txt_Waistbreadth;
    private TextView txt_Hipheight;
    private TextView txt_Insidelegheight;
    private TextView txt_Upperhipheight;
    private TextView txt_Thighgirth;
    private String id;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.framgent_measure, container, false);
        LoadLocale();
        getActivity().setTitle(getText(R.string.YourMeasurement));
        initialize(view);
        queue = Volley.newRequestQueue(getContext());
        getdata();
        return view;
    }

    private void initialize(View view) {
        txt_Bustgirth = view.findViewById(R.id.txt_Bustgirth);
        txt_Waistgirth = view.findViewById(R.id.txt_Waistgirth);
        txt_Hipgirth = view.findViewById(R.id.txt_Hipgirth);
        txt_Backneckheight = view.findViewById(R.id.txt_Backneckheight);
        txt_Necktoupperhiplength = view.findViewById(R.id.txt_Necktoupperhiplength);
        txt_Frontshoulderwidth = view.findViewById(R.id.txt_Frontshoulderwidth);
        txt_Acrossbackshoulderwidth = view.findViewById(R.id.txt_Acrossbackshoulderwidth);
        txt_Acrossbackwidth = view.findViewById(R.id.txt_Acrossbackwidth);
        txt_Shoulderlength = view.findViewById(R.id.txt_Shoulderlength);
        txt_Chestwidth = view.findViewById(R.id.txt_Chestwidth);
        txt_Underbustgirth = view.findViewById(R.id.txt_Underbustgirth);
        txt_Upperchestgirth = view.findViewById(R.id.txt_Upperchestgirth);
        txt_Abdomengirth = view.findViewById(R.id.txt_Abdomengirth);
        txt_Backshoulderwidth = view.findViewById(R.id.txt_Backshoulderwidth);
        txt_Neckgirth = view.findViewById(R.id.txt_Neckgirth);
        txt_Upperarmgirth = view.findViewById(R.id.txt_Upperarmgirth);
        txt_Outerarmlength = view.findViewById(R.id.txt_Outerarmlength);
        txt_Jacketlength = view.findViewById(R.id.txt_Jacketlength);
        txt_Torsoheight = view.findViewById(R.id.txt_Torsoheight);
        txt_Backnecktohiplength = view.findViewById(R.id.txt_Backnecktohiplength);
        txt_Waistbreadth = view.findViewById(R.id.txt_Waistbreadth);
        txt_Hipheight = view.findViewById(R.id.txt_Hipheight);
        txt_Insidelegheight = view.findViewById(R.id.txt_Insidelegheight);
        txt_Upperhipheight = view.findViewById(R.id.txt_Upperhipheight);
        txt_Thighgirth = view.findViewById(R.id.txt_Thighgirth);
    }


    private void put() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("gender", gender.toLowerCase());
            jsonObject.put("height", Integer.valueOf(height));
            jsonObject.put("weight", Float.valueOf(weight));
            jsonObject.put("front_image", convertUrlToBase64(front));
            jsonObject.put("side_image", convertUrlToBase64(side));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    id = response.getString("id");
                    firebaseDatabase = FirebaseDatabase.getInstance();
                    databaseReference = firebaseDatabase.getReference().child("Users");
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                Profile profile = dataSnapshot1.getValue(Profile.class);
                                if (firebaseUser.getUid().equals(profile.getUid())) {
                                    profile.setId(id);
                                    databaseReference.child(profile.getUid()).setValue(profile);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    get();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Please Try Again", Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(getContext(), "Successful request", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            public Map getHeaders() {
                HashMap headers = new HashMap();
                headers.put("Authorization", key);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        queue.add(request);
    }

    private void get() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("results");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject bodysize = jsonArray.getJSONObject(i);
                        if (bodysize.getString("id").equals(id)) {
                            JSONObject volumeparams = bodysize.getJSONObject("volume_params");
                            for (int x = 0; x < volumeparams.length(); x++) {
                                volumeparams(volumeparams);
                            }
                            JSONObject frontparams = bodysize.getJSONObject("front_params");
                            for (int y = 0; y < frontparams.length(); y++) {
                                frontparams(frontparams);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(getContext(), "Please Try Again", Toast.LENGTH_SHORT).show();
            }
        }) {
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Authorization", key);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        queue.add(request);
    }

    private void frontparams(JSONObject frontparams) {
        try {
            txt_Insidelegheight.setText(frontparams.getString("inside_leg_height") + "cm");
            txt_Backneckheight.setText(frontparams.getString("back_neck_height") + "cm");
            txt_Necktoupperhiplength.setText(frontparams.getString("body_height") + "cm");
            txt_Frontshoulderwidth.setText(frontparams.getString("shoulders") + "cm");
            txt_Acrossbackshoulderwidth.setText(frontparams.getString("across_back_shoulder_width") + "cm");
            txt_Acrossbackwidth.setText(frontparams.getString("across_back_width") + "cm");
            txt_Shoulderlength.setText(frontparams.getString("shoulder_length") + "cm");
            txt_Chestwidth.setText(frontparams.getString("chest_top") + "cm");
            txt_Backshoulderwidth.setText(frontparams.getString("back_shoulder_width") + "cm");
            txt_Outerarmlength.setText(frontparams.getString("sleeve_length") + "cm");
            txt_Jacketlength.setText(frontparams.getString("jacket_length") + "cm");
            txt_Torsoheight.setText(frontparams.getString("torso_height") + "cm");
            txt_Hipheight.setText(frontparams.getString("hip_height") + "cm");
            txt_Waistbreadth.setText(frontparams.getString("waist") + "cm");
            txt_Backnecktohiplength.setText(frontparams.getString("back_neck_to_hip_length") + "cm");
            txt_Upperhipheight.setText(frontparams.getString("upper_hip_height") + "cm");
            txt_Waistbreadth.setText(frontparams.getString("waist") + "cm");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void volumeparams(JSONObject volumeparams) {
        try {
            txt_Underbustgirth.setText(volumeparams.getString("under_bust_girth") + "cm");
            txt_Upperchestgirth.setText(volumeparams.getString("upper_chest_girth") + "cm");
            txt_Bustgirth.setText(volumeparams.getString("chest") + "cm");
            txt_Waistgirth.setText(volumeparams.getString("waist") + "cm");
            txt_Hipgirth.setText(volumeparams.getString("low_hips") + "cm");
            txt_Abdomengirth.setText(volumeparams.getString("abdomen") + "cm");
            txt_Neckgirth.setText(volumeparams.getString("neck_girth") + "cm");
            txt_Upperarmgirth.setText(volumeparams.getString("bicep") + "cm");
            txt_Thighgirth.setText(volumeparams.getString("thigh") + "cm");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getdata() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Profile profile = dataSnapshot1.getValue(Profile.class);
                    if (firebaseUser.getUid().equals(profile.getUid())) {
                        gender = profile.getGender();
                        height = profile.getHeight();
                        front = profile.getFront();
                        side = profile.getSide();
                        weight = profile.getWeight();
                        id = profile.getId();
                        if (id.equals("")) {
                            put();
                        } else {
                            get();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public String convertUrlToBase64(String url) {
        URL newurl;
        Bitmap bitmap;
        String base64 = "";
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            newurl = new URL(url);
            bitmap = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            base64 = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return base64;
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
