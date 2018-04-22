package mx.itesm.dognoscis;

import com.google.firebase.database.IgnoreExtraProperties;

import java.text.DecimalFormat;

/**
 * Created by MARTELL on 21-Apr-18.
 */

@IgnoreExtraProperties
public class BreedLocation {
    private String breed;
    private double lat, lng, certainty;
    private DecimalFormat df = new DecimalFormat("#.##");

    public BreedLocation(){ // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }
    public BreedLocation(String breed, double lat, double lng, double certainty){
        this.breed = breed;
        this.lat = lat;
        this.lng = lng;
        this.certainty = Double.valueOf(df.format(certainty));
    }
    public String getBreed(){
        return this.breed;
    }
    public double getLat(){
        return this.lat;
    }
    public double getLng(){
        return this.lng;
    }
    public double getCertainty() { return this.certainty; }
}
