package com.blaqh.myrestaurants.services;

import com.blaqh.myrestaurants.Constants;
import com.blaqh.myrestaurants.models.Restaurant;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class YelpService {

    public static OkHttpClient client = new OkHttpClient.Builder().build();

    public static void findRestaurants(String location, Callback callback) {

        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constants.YELP_BASE_URL).newBuilder();
        urlBuilder.addQueryParameter(Constants.YELP_LOCATION_QUERY_PARAMETER, location);
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .header("Authorization", "Bearer " + Constants.YELP_CONSUMER_KEY)
                .url(url)
                .build();

        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public List<Restaurant> processResults(Response response) {
        List<Restaurant> restaurants = new ArrayList<>();

        try {
            String jsonData = response.body().string();

            if (response.isSuccessful()) {
                //the response json is an array of businesses objects within an object so we need to get thaat array
                JSONObject yelpJSON = new JSONObject(jsonData);
                JSONArray businessesJSON = yelpJSON.getJSONArray("businesses");

                Type collectionType = new TypeToken<List<Restaurant>>() {}.getType();
                Gson gson = new GsonBuilder().create();
                restaurants = gson.fromJson(businessesJSON.toString(), collectionType);

            }
        }catch (JSONException | NullPointerException | IOException e) {
            e.printStackTrace();
        }

        return restaurants;
    }
}