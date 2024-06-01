/* Проверить как работает двойная выгрузка(нужен флаг страны)

Во втором фрагменты просить от пользователя айпи и выводить
конкретную инфу по этому айпи.

добавить возможность делиться своим айпи
* */
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
import com.google.gson.GsonBuilder;
import com.google.gson.*;
import org.json.JSONException;
import org.json.JSONObject;


public class IpInfoFragment extends Fragment {
    private String url_freeIpApi = "https://freeipapi.com/api/json/";
    private String url_ipApi = "http://ip-api.com/json/?fields=66821934";

    public IpInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ip_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView tvCityInfo = view.findViewById(R.id.cityInfo);
        TextView tvCoordinates = view.findViewById(R.id.Coordinates);
        TextView tvCountry = view.findViewById(R.id.Country);
        TextView ipInfo = view.findViewById(R.id.ipInfo);
        TextView moreInfo = view.findViewById(R.id.moreInfo);
        Button btShare = view.findViewById(R.id.share);
        ImageView ivCountryFlag = view.findViewById(R.id.counrtyFlag);


        mainFetchIPInfo(tvCityInfo, tvCoordinates,ivCountryFlag,ipInfo,tvCountry,btShare);
        moreFetchIPInfo(moreInfo);

    }

    private void mainFetchIPInfo(TextView tvCityInfo, TextView tvCoordinates, ImageView ivCountryFlag,
                             TextView ipInfo, TextView tvCountry,Button btShare) {
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest(Request.Method.GET, url_freeIpApi, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Обработка JSON ответа
                            String query = response.getString("ipAddress");
                            Log.i(TAG,"IP " + query);
                            String country = response.getString("countryName");
                            String city = response.getString("cityName");
                            String countryCode = response.getString("countryCode").toLowerCase();
                            String coordinates = response.getString("latitude")+","+response.getString("longitude");

                            //Флаг страны
                            String flagUrl = "https://flagcdn.com/"+"16x12/" + countryCode+".png";
                            Glide.with(requireContext()).load(flagUrl).into(ivCountryFlag);

                            // Вывод информации в TextView
                            tvCountry.setText(String.format("Country: %s", country));
                            ipInfo.setText(String.format("IP: %s", query));
                            tvCityInfo.setText(String.format("City: %s", city));
                            tvCoordinates.setText(String.format("Coordinates: %s", coordinates));

                            //Поделиться в мессенджерах
                            String shareText ="IP: "+query + "\n" + "Country: "+country+"\n"
                                    +"City: "+city+"\n"+ "Coordinates: "+coordinates;
                            btShare.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (v.getId()==R.id.share){
                                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                                        shareIntent.setType("text/plain");
                                        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                                        startActivity(shareIntent);
                                    }
                                }
                            });

                            // Устанавливаем OnClickListener для открытия карт при нажатии на координаты
                            tvCoordinates.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (v.getId()==R.id.Coordinates){
                                        Uri uriLocation = Uri.parse("geo:"+coordinates);
                                        Intent locationIntent = new Intent(Intent.ACTION_VIEW);
                                        locationIntent.setData(uriLocation);
                                        startActivity(locationIntent);
                                    }
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
                Log.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getActivity(),"Error fetching IP information.",Toast.LENGTH_SHORT).show();
            }
        });
        // Добавляем запрос в очередь
        queue.add(jsonObjectRequest1);
    }





    private void moreFetchIPInfo(TextView moreInfo) {
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        JsonObjectRequest jsonObjectRequest2 = new JsonObjectRequest(Request.Method.GET, url_ipApi, null,
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
