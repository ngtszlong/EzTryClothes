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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class MeasureFragment extends Fragment {
    private String url = "https://saia.3dlook.me/api/v2/persons/?measurements_type=all";
    private String key = "APIKey 93bb8c48e5289c0f4feda4ff48a647e3e8465372";

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
    private TextView txt_Necktoupperhiplength;
    private TextView txt_Frontshoulderwidth;
    private TextView txt_Insidelegheight;
    private TextView txt_Upperhipgirth;
    private String id;

    ProgressDialog progressDialog;

    TextView txt_sizeguide;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.framgent_measure, container, false);
        LoadLocale();
        getActivity().setTitle(getText(R.string.YourMeasurement));
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getText(R.string.GettingData));
        progressDialog.show();
        txt_sizeguide = view.findViewById(R.id.txt_help);
        txt_sizeguide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext()).setView(R.layout.sizeguide).show();
            }
        });
        initialize(view);
        queue = Volley.newRequestQueue(getContext());
        getdata();
        return view;
    }

    private void initialize(View view) {
        txt_Bustgirth = view.findViewById(R.id.txt_Bustgirth);
        txt_Waistgirth = view.findViewById(R.id.txt_Waistgirth);
        txt_Hipgirth = view.findViewById(R.id.txt_Hipgirth);
        txt_Necktoupperhiplength = view.findViewById(R.id.txt_Necktoupperhiplength);
        txt_Frontshoulderwidth = view.findViewById(R.id.txt_Frontshoulderwidth);
        txt_Insidelegheight = view.findViewById(R.id.txt_Insidelegheight);
        txt_Upperhipgirth = view.findViewById(R.id.txt_Upperhipgirth);
    }


    private void put() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
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
                Log.d("onResponse", String.valueOf(response));
                try {
                    id = String.valueOf(response.getJSONObject("id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                firebaseDatabase = FirebaseDatabase.getInstance();
                databaseReference = firebaseDatabase.getReference().child("Users");
                databaseReference.child(firebaseUser.getUid()).child("id").setValue(id);
                get();
                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse response = error.networkResponse;
                if(response != null && response.data != null) {
                    String errorString = new String(response.data);
                    Log.i("log error", errorString);
                }
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Fail to Get data", Toast.LENGTH_SHORT).show();
            }
        }) {
            public Map getHeaders() {
                HashMap headers = new HashMap();
                headers.put("Authorization", key);
                headers.put("Content-Type", "application/json");
                Log.i("sending ", headers.toString());
                return headers;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(100000, 1, 1.0f));
        queue.add(request);
    }

    private void get() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, "https://saia.3dlook.me/api/v2/persons/?measurements_type=all", null, new Response.Listener<JSONObject>() {
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
                    progressDialog.dismiss();
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
            txt_Insidelegheight.setText(frontparams.getString("inseam") + "cm");
            txt_Necktoupperhiplength.setText(frontparams.getString("body_height") + "cm");
            txt_Frontshoulderwidth.setText(frontparams.getString("shoulders") + "cm");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void volumeparams(JSONObject volumeparams) {
        try {
            txt_Upperhipgirth.setText(volumeparams.getString("hips") + "cm");
            txt_Bustgirth.setText(volumeparams.getString("chest") + "cm");
            txt_Waistgirth.setText(volumeparams.getString("waist") + "cm");
            txt_Hipgirth.setText(volumeparams.getString("low_hips") + "cm");
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
