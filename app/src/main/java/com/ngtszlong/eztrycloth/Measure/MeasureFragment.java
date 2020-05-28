package com.ngtszlong.eztrycloth.Measure;

import android.app.ProgressDialog;
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
    private String key;

    private FirebaseUser firebaseUser;
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
    private TextView txt_uppersize;
    private TextView txt_lowersize;
    private String id;

    private ProgressDialog progressDialog;

    private double Bustgirth;
    private double Frontshoulderwidth;
    private double Hipgirth;
    private double Waistgirth;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.framgent_measure, container, false);
        LoadLocale();
        getActivity().setTitle(getText(R.string.YourMeasurement));
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getText(R.string.GettingData));
        progressDialog.show();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    if (dataSnapshot1.getKey().equals("APIKey")) {
                        String value = dataSnapshot1.getValue().toString();
                        key = value;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        TextView txt_sizeguide = view.findViewById(R.id.txt_help);
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
        txt_uppersize = view.findViewById(R.id.upperbodysize);
        txt_lowersize = view.findViewById(R.id.lowerbodysize);
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
                    id = response.getString("task_set_url");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                firebaseDatabase = FirebaseDatabase.getInstance();
                databaseReference = firebaseDatabase.getReference().child("Users");
                databaseReference.child(firebaseUser.getUid()).child("id").setValue(id);
                Toast.makeText(getContext(), R.string.PleaseRestartthepage, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
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
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, id, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d("Response", String.valueOf(response));
                    JSONObject volumeparams = response.getJSONObject("volume_params");
                    Log.d("Response", String.valueOf(volumeparams));
                    for (int x = 0; x < volumeparams.length(); x++) {
                        volumeparams(volumeparams);
                    }
                    JSONObject frontparams = response.getJSONObject("front_params");
                    for (int y = 0; y < frontparams.length(); y++) {
                        Log.d("Response", String.valueOf(frontparams));
                        frontparams(frontparams);
                    }
                    if (gender.equals("Male")) {
                        menuppersizedefine();
                        menlowersizedefine();
                    } else if (gender.equals("Female")) {
                        womenuppersizedefine();
                        womenlowersizedefine();
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

    private void womenlowersizedefine() {
        String size = null;
        if (Hipgirth > 82 && Hipgirth < 88) {//XS
            if (Waistgirth < 63 && Waistgirth > 57) {//XS
                size = "XS";
            } else if (Waistgirth < 66 && Waistgirth > 60) {//S
                size = "S";
            } else if (Waistgirth < 69 && Waistgirth > 63) {//M
                size = "M";
            } else if (Waistgirth < 75 && Waistgirth > 69) {//L
                size = "L";
            } else if (Waistgirth > 75) {//XL
                size = "XL";
            }
        } else if (Hipgirth > 85 && Hipgirth < 91) {//S
            if (Waistgirth < 63 && Waistgirth > 57) {//XS
                size = "XS";
            } else if (Waistgirth < 66 && Waistgirth > 60) {//S
                size = "S";
            } else if (Waistgirth < 69 && Waistgirth > 63) {//M
                size = "M";
            } else if (Waistgirth < 75 && Waistgirth > 69) {//L
                size = "L";
            } else if (Waistgirth > 75) {//XL
                size = "XL";
            }
        } else if (Hipgirth > 88 && Hipgirth < 94) {//M
            if (Waistgirth < 63 && Waistgirth > 57) {//XS
                size = "XS";
            } else if (Waistgirth < 66 && Waistgirth > 60) {//S
                size = "S";
            } else if (Waistgirth < 69 && Waistgirth > 63) {//M
                size = "M";
            } else if (Waistgirth < 75 && Waistgirth > 69) {//L
                size = "L";
            } else if (Waistgirth > 75) {//XL
                size = "XL";
            }
        } else if (Hipgirth > 94 && Hipgirth < 100) {//L
            if (Waistgirth < 63 && Waistgirth > 57) {//XS
                size = "XS";
            } else if (Waistgirth < 66 && Waistgirth > 60) {//S
                size = "S";
            } else if (Waistgirth < 69 && Waistgirth > 63) {//M
                size = "M";
            } else if (Waistgirth < 75 && Waistgirth > 69) {//L
                size = "L";
            } else if (Waistgirth > 75) {//XL
                size = "XL";
            }
        } else if (Hipgirth > 100 && Hipgirth < 106) {//XL
            if (Waistgirth < 63 && Waistgirth > 57) {//XS
                size = "XS";
            } else if (Waistgirth < 66 && Waistgirth > 60) {//S
                size = "S";
            } else if (Waistgirth < 69 && Waistgirth > 63) {//M
                size = "M";
            } else if (Waistgirth < 75 && Waistgirth > 69) {//L
                size = "L";
            } else if (Waistgirth > 75) {//XL
                size = "XL";
            }
        }
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReference.child(firebaseUser.getUid()).child("lowersize").setValue(size);
    }

    private void womenuppersizedefine() {
        String size = null;
        if (Frontshoulderwidth > 35) {//XL
            if (Bustgirth / 2 >= 39) {//XL
                size = "XL";
            } else if (Bustgirth / 2 < 39) {//L
                size = "L";
            }
        } else if (Frontshoulderwidth < 35 && Frontshoulderwidth >= 33.5) {//L
            if (Bustgirth / 2 >= 39) {//XL
                size = "XL";
            } else if (Bustgirth / 2 < 39 && Bustgirth / 2 >= 36) {//L
                size = "L";
            } else if (Bustgirth / 2 < 36) {//M
                size = "L";
            }
        } else if (Frontshoulderwidth < 33.5 && Frontshoulderwidth >= 32.5) {//M
            if (Bustgirth / 2 < 39 && Bustgirth / 2 >= 36) {//L
                size = "L";
            } else if (Bustgirth / 2 < 36 && Bustgirth / 2 >= 33.5) {//M
                size = "M";
            } else if (Bustgirth / 2 < 33.5) {//S
                size = "M";
            }
        } else if (Frontshoulderwidth < 32.5 && Frontshoulderwidth >= 31.5) {//S
            if (Bustgirth / 2 < 36 && Bustgirth / 2 >= 33.5) {//M
                size = "M";
            } else if (Bustgirth / 2 < 33.5 && Bustgirth / 2 >= 31) {//S
                size = "S";
            } else if (Bustgirth / 2 < 31) {//XS
                size = "S";
            }
        } else if (Frontshoulderwidth < 31.5) {//XS
            if (Bustgirth / 2 < 33.5 && Bustgirth / 2 >= 31) {//S
                size = "S";
            } else if (Bustgirth / 2 < 31) {//XS
                size = "XS";
            }
        }
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReference.child(firebaseUser.getUid()).child("uppersize").setValue(size);
    }

    private void menlowersizedefine() {
        String size = null;
        if (Waistgirth > 66 && Waistgirth < 72) {//XS
            size = "XS";
        } else if (Waistgirth > 68 && Waistgirth < 76) {//S
            size = "S";
        } else if (Waistgirth > 76 && Waistgirth < 84) {//M
            size = "M";
        } else if (Waistgirth > 84 && Waistgirth < 92) {//L
            size = "L";
        } else if (Waistgirth > 92) {//XL
            size = "XL";
        }
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReference.child(firebaseUser.getUid()).child("lowersize").setValue(size);
    }

    private void menuppersizedefine() {
        String size = null;
        if (Frontshoulderwidth > 54) {//XL
            if (Bustgirth / 2 >= 60) {//XL
                size = "XL";
            } else if (Bustgirth / 2 < 60) {//L
                size = "L";
            }
        } else if (Frontshoulderwidth < 54 && Frontshoulderwidth >= 52.5) {//L
            if (Bustgirth / 2 >= 60) {//XL
                size = "XL";
            } else if (Bustgirth / 2 < 60 && Bustgirth / 2 >= 57) {//L
                size = "L";
            } else if (Bustgirth / 2 < 57) {//M
                size = "L";
            }
        } else if (Frontshoulderwidth < 52.5 && Frontshoulderwidth >= 51) {//M
            if (Bustgirth / 2 < 60 && Bustgirth / 2 >= 57) {//L
                size = "L";
            } else if (Bustgirth / 2 < 57 && Bustgirth / 2 >= 54) {//M
                size = "M";
            } else if (Bustgirth / 2 < 54) {//S
                size = "M";
            }
        } else if (Frontshoulderwidth < 51 && Frontshoulderwidth >= 49.5) {//S
            if (Bustgirth / 2 < 57 && Bustgirth / 2 >= 54) {//M
                size = "M";
            } else if (Bustgirth / 2 < 54 && Bustgirth / 2 >= 51) {//S
                size = "S";
            } else if (Bustgirth / 2 < 51) {//XS
                size = "S";
            }
        } else if (Frontshoulderwidth < 49.5) {//XS
            if (Bustgirth / 2 < 54 && Bustgirth / 2 >= 51) {//S
                size = "S";
            } else if (Bustgirth / 2 < 51) {//XS
                size = "XS";
            }
        }
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReference.child(firebaseUser.getUid()).child("uppersize").setValue(size);
    }

    private void frontparams(JSONObject frontparams) {
        try {
            txt_Insidelegheight.setText(frontparams.getString("inside_leg_height") + "cm");
            txt_Necktoupperhiplength.setText(frontparams.getString("body_height") + "cm");
            txt_Frontshoulderwidth.setText(frontparams.getString("shoulders") + "cm");
            Frontshoulderwidth = Double.parseDouble(frontparams.getString("shoulders"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void volumeparams(JSONObject volumeparams) {
        try {
            txt_Upperhipgirth.setText(volumeparams.getString("high_hips") + "cm");
            txt_Bustgirth.setText(volumeparams.getString("chest") + "cm");
            Bustgirth = Double.parseDouble(volumeparams.getString("under_bust_girth"));
            txt_Waistgirth.setText(volumeparams.getString("waist") + "cm");
            Waistgirth = Double.parseDouble(volumeparams.getString("waist"));
            txt_Hipgirth.setText(volumeparams.getString("low_hips") + "cm");
            Hipgirth = Double.parseDouble(volumeparams.getString("low_hips"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getdata() {
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
                        txt_uppersize.setText(profile.getUppersize());
                        txt_lowersize.setText(profile.getLowersize());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        databaseReference.keepSynced(true);
    }

    private String convertUrlToBase64(String url) {
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

    private void LoadLocale() {
        SharedPreferences preferences = getActivity().getSharedPreferences("Setting", MODE_PRIVATE);
        String language = preferences.getString("My_Lang", "");
        setLocale(language);
    }
}
