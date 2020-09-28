package com.travelsystem;

import com.travelsystem.logic.OpenStreetMapUtils;
import com.travelsystem.model.dao.Destination;
import com.travelsystem.model.dao.Journey;
import lombok.NoArgsConstructor;
import org.json.JSONException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@NoArgsConstructor
public class SupportMethods {

    @Async

    public void tempImage(Journey journey, MultipartFile file){
        if(!file.isEmpty()) {
            try {
                journey.setThumbnail(file.getBytes());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @Async
    public void setCoordinates(Destination destination){
        Map<String, Double> coords;
        coords = OpenStreetMapUtils.getInstance().getCoordinates(destination.getCoordinates());
        if(coords.get("lat") ==null || coords.get("lon")==null){
            destination.setLatlngX(0);
            destination.setLatlngY(0);
        }else{
            destination.setLatlngX(coords.get("lat"));
            destination.setLatlngY(coords.get("lon"));
        }

    }
    @Async

    public String setDisplayName(Destination destination) throws IOException, JSONException {
       return  OpenStreetMapUtils.getInstance().getDisplayName(destination.getCoordinates());

}

    @Async

    public void setCountry(Destination destination) throws IOException, JSONException {
        String country = OpenStreetMapUtils.getInstance().getCountry(destination.getCoordinates());
        destination.setCountry(country);
    }




}
