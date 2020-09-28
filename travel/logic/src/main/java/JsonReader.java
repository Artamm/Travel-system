import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonReader {

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }

    public static void main(String[] args) throws IOException, JSONException {
        //"https://file1.dxycdn.com/2020/0131/090/3394052471398860228-62.json?t=26385059"
        String url ="https://services1.arcgis.com/0MSEUqKaxRlEPj5g/arcgis/rest/services/ncov_cases/FeatureServer/2/query?f=json&where=Confirmed%20%3E%200&returnGeometry=false&spatialRel=esriSpatialRelIntersects&outFields=*&orderByFields=Confirmed%20desc&resultOffset=0&resultRecordCount=100&cacheHint=true";
       String url2="https://www.osmap.uk/#19/50.49260/30.59221";
       String url3="http://nominatim.openstreetmap.org/search?q=Lithuania&format=json&addressdetails=1&accept-language=en";
        JSONObject json = readJsonFromUrl(url3);
        JSONArray data = json.getJSONArray("0");


        for (int i= 0;i<data.length();i++){
            JSONObject j = data.getJSONObject(i);
            JSONObject d = j.getJSONObject("attributes");
            Iterator key = d.keys();
            while (key.hasNext()) {
                String k = key.next().toString();
                if(k.equals("Country_Region")) {
                    if(d.getString(k).equals("Lithuania")) {
                        System.out.println("\nKey : " + k + "\nName : " + d.getString(k) + ", \nValue : "+d.getInt("Confirmed") );
                    }
                }
            }


        }
        System.out.println(json.get("Lithuania"));

    }
}