package com.example.SurakshaPersonalSafetyApp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataParser {
    private HashMap<String , String> getSingleNearbyPlace(JSONObject googlePlaceJASON)
    {
        HashMap<String,String>googlePlaceMap = new HashMap<>();
        String NameOfPlace = "-NA-";
        String vicinity = "-NA-";
        String latitude = "";
        String longitude = "";
        String reference = "";

        try {//for fetching the data this we are doing
            if (!googlePlaceJASON.isNull("name")){
                NameOfPlace = googlePlaceJASON.getString("name");
            }
            if (!googlePlaceJASON.isNull("vicinity")){
                vicinity = googlePlaceJASON.getString("vicinity");
            }
            latitude = googlePlaceJASON.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = googlePlaceJASON.getJSONObject("geometry").getJSONObject("location").getString("lng");
            reference = googlePlaceJASON.getString("name");

            googlePlaceMap.put("place_name",NameOfPlace);
            googlePlaceMap.put("vicinity",vicinity);
            googlePlaceMap.put("lat",latitude);
            googlePlaceMap.put("lng",longitude);
            googlePlaceMap.put("reference",reference);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return googlePlaceMap;
    }
    //for searching more we need more hashmap list
    private List<HashMap<String,String>> getAllNearbyPlaces(JSONArray jsonArray)
    {
        int counter = jsonArray.length();
        List<HashMap<String,String>> NearbyPlacesList = new ArrayList<>();
        HashMap<String,String> NearbyplaceMap = null;
        for (int i = 0;i<counter;i++){
            try {
                NearbyplaceMap = getSingleNearbyPlace((JSONObject) jsonArray.get(i));
                NearbyPlacesList.add(NearbyplaceMap);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return NearbyPlacesList;
    }

    public List<HashMap<String,String>> parse(String jSONdata){
        JSONArray jsonArray = null;
        JSONObject jsonObject;

        try
        {
            jsonObject = new JSONObject(jSONdata);
            jsonArray = jsonObject.getJSONArray("results");
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return getAllNearbyPlaces(jsonArray);
    }
}
