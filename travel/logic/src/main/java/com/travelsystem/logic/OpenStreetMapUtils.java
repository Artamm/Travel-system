package com.travelsystem.logic;

import java.io.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONTokener;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import sun.misc.IOUtils;


public class OpenStreetMapUtils {


    private final OkHttpClient httpClient = new OkHttpClient();

    private static OpenStreetMapUtils instance = null;
    private JSONParser jsonParser;

    public OpenStreetMapUtils() {
        jsonParser = new JSONParser();
    }

    public static OpenStreetMapUtils getInstance() {
        if (instance == null) {
            instance = new OpenStreetMapUtils();
        }
        return instance;
    }


    private String sendGet(String query) throws Exception {

        Request request = new Request.Builder()
                .url(query)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {

            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            // Get response body
            return response.body().string();
        }
    }


    private StringBuffer prepareAddress(String address, StringBuffer query) {

        String[] split = address.split(" ");

        query.append("http://nominatim.openstreetmap.org/search?q=");

        if (split.length == 0) {
            return null;
        }

        for (int i = 0; i < split.length; i++) {
            query.append(split[i]);
            if (i < (split.length - 1)) {
                query.append("+");
            }
        }
        query.append("&format=json&addressdetails=1&accept-language=en");

        return query;
    }

    public Map<String, Double> getCoordinates(String address) {
        Map<String, Double> res;
        StringBuffer query;
        String queryResult = null;

        query = new StringBuffer();
        res = new HashMap<String, Double>();

        query = prepareAddress(address, query);


        try {
            queryResult = sendGet(query.toString());
        } catch (Exception e) {
        }

        if (queryResult == null) {
            return null;
        }

        Object obj = JSONValue.parse(queryResult);

        if (obj instanceof JSONArray) {
            JSONArray array = (JSONArray) obj;
            if (array.size() > 0) {
                JSONObject jsonObject = (JSONObject) array.get(0);

                String lon = (String) jsonObject.get("lon");
                String lat = (String) jsonObject.get("lat");
                String country = (String) jsonObject.get("country");
                res.put("lon", Double.parseDouble(lon));
                res.put("lat", Double.parseDouble(lat));

            }
        }

        return res;
    }

    public String getCountry(String address) throws IOException, JSONException {
        String country = "";
        StringBuffer query;
        String queryResult = null;
        query = new StringBuffer();
        query = prepareAddress(address, query);

        try {
            queryResult = sendGet(query.toString());
        } catch (Exception e) {
        }

        if (queryResult == null) {
            return null;
        }

        Object obj = JSONValue.parse(queryResult);

        if (obj instanceof JSONArray) {
            JSONArray array = (JSONArray) obj;
            if (array.size() > 0) {
                JSONObject jsonObject = (JSONObject) array.get(0);
                JSONObject d = (JSONObject) jsonObject.get("address");
                Object o = d.get("country");
                country = (String) o;


            }
        }

        return country;
    }


    public String getDisplayName(String address) throws IOException, JSONException {
        String display_name = "";
        StringBuffer query;
        String queryResult = null;
        query = new StringBuffer();
        query = prepareAddress(address, query);

        try {
            queryResult = sendGet(query.toString());
        } catch (Exception e) {
        }

        if (queryResult == null) {
            return null;
        }

        Object obj = JSONValue.parse(queryResult);

        if (obj instanceof JSONArray) {
            JSONArray array = (JSONArray) obj;
            if (array.size() > 0) {
                JSONObject jsonObject = (JSONObject) array.get(0);
                String d = (String) jsonObject.get("display_name");
                display_name = (String) d;


            }
        }

        return display_name;
    }




    private String QueryResult(StringBuffer query, String queryResult) {

        try {
            queryResult = sendGet(query.toString());
        } catch (Exception ignored) {
        }

        if (queryResult == null) {
            return null;
        } else {
            return queryResult;
        }
    }


    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static org.json.JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            org.json.JSONObject json = new org.json.JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }

    public String CoronavirusMap(String address) throws IOException, JSONException {
        String url = "https://services1.arcgis.com/0MSEUqKaxRlEPj5g/arcgis/rest/services/ncov_cases/FeatureServer/1/query?f=json&where=Confirmed%20%3E%200&returnGeometry=false&spatialRel=esriSpatialRelIntersects&outFields=*&orderByFields=Confirmed%20desc%2CCountry_Region%20asc%2CProvince_State%20asc&resultOffset=0&resultRecordCount=250&cacheHint=true";
        org.json.JSONObject json = readJsonFromUrl(url);
        org.json.JSONArray data = json.getJSONArray("features");
        org.json.JSONObject current = null;
        boolean flag = true;
        for (int i = 0; i < data.length(); i++) {
            org.json.JSONObject j = data.getJSONObject(i);
            org.json.JSONObject d = j.getJSONObject("attributes");
            Iterator key = d.keys();
            while (key.hasNext() & flag) {
                String k = key.next().toString();
                if (k.equals("Country_Region")) {
                    if (d.getString(k).equals(address)) {
                        current = d;
                        flag = false;
                        i = data.length() - 2;
                    }
                }
            }
        }
        int b = 0;
        if (current == null) {
            return "No cases";
        }

        for (int i = 0; i < current.length(); i++) {
            Iterator key = current.keys();
            while (key.hasNext()) {
                String k = key.next().toString();
                if (k.equals("Confirmed")) {
                    b = current.getInt("Confirmed");
                    return Integer.toString(b);
                }
            }

        }
        if (b == 0) {
            return "No cases";
        } else {
            return Integer.toString(b);
        }
    }
}

