package com.mirea.kt.ribo;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

public class UserIpInfoFragment extends Fragment {

    private String url_freeIpApi = "https://freeipapi.com/api/json/";
    private String url_ipApi = "http://ip-api.com/json/";

    public UserIpInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_ip_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText etUserIpInput = view.findViewById(R.id.userIpInfo);
        Button btnFetchIpInfo = view.findViewById(R.id.btnStartShowIpInfo);
        TextView tvCityInfo = view.findViewById(R.id.cityInfo2);
        TextView tvCoordinates = view.findViewById(R.id.Coordinates2);
        TextView tvCountry = view.findViewById(R.id.Country2);
        ImageView ivCountryFlag = view.findViewById(R.id.counrtyFlag2);
        TextView tvMoreInfo = view.findViewById(R.id.moreInfo2);

        btnFetchIpInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userIp = etUserIpInput.getText().toString();
                if (!userIp.isEmpty()) {
                    fetchIpInfo(userIp, tvCityInfo, tvCoordinates, tvCountry, ivCountryFlag);
                    moreFetchIPInfo(userIp,tvMoreInfo);
                } else {
                    tvCityInfo.setText("Please enter an IP address.");
                }
            }
        });
    }

    private void fetchIpInfo(String userIp, TextView tvCityInfo, TextView tvCoordinates,
                             TextView tvCountry, ImageView ivCountryFlag) {
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url_freeIpApi + userIp, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String country = response.getString("countryName");
                            String city = response.getString("cityName");
                            String countryCode = response.getString("countryCode").toLowerCase();
                            String coordinates = response.getString("latitude") + "," + response.getString("longitude");

                            // Флаг страны
                            String flagUrl = "https://flagcdn.com/16x12/" + countryCode + ".png";
                            Glide.with(requireContext()).load(flagUrl).into(ivCountryFlag);

                            // Вывод информации в TextView
                            tvCountry.setText(String.format("Country: %s", country));
                            tvCityInfo.setText(String.format("City: %s", city));
                            tvCoordinates.setText(String.format("Coordinates: %s", coordinates));



                            // Устанавливаем OnClickListener для открытия карт при нажатии на координаты
                            tvCoordinates.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Uri uriLocation = Uri.parse("geo:" + coordinates);
                                    Intent locationIntent = new Intent(Intent.ACTION_VIEW);
                                    locationIntent.setData(uriLocation);
                                    startActivity(locationIntent);
                                }
                            });

                        } catch (JSONException e) {
                            Toast.makeText(getActivity(),"Error parsing JSON response.",Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Error: " + e.getMessage());

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getActivity(),"Error fetching IP information.",Toast.LENGTH_SHORT).show();
            }
        });
        // Добавляем запрос в очередь
        queue.add(jsonObjectRequest);
    }



    private void moreFetchIPInfo(String userIp, TextView moreInfo) {
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        JsonObjectRequest jsonObjectRequest2 = new JsonObjectRequest(Request.Method.GET,
                url_ipApi+userIp+"?fields=66821934",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Вывод красивого Json объекта с помощью (Gson)
                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                        JsonElement je = JsonParser.parseString(String.valueOf(response));
                        String prettyJsonString = gson.toJson(je);
                        moreInfo.setText(prettyJsonString);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getActivity(),"Error fetching IP information.",Toast.LENGTH_SHORT).show();

            }
        });
        // Добавляем запрос в очередь
        queue.add(jsonObjectRequest2);

    }
}
