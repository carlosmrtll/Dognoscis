package mx.itesm.dognoscis;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by MARTELL on 21-Apr-18.
 */

@IgnoreExtraProperties
public class BreedLocation {
    String breed;
    double lat;
    double lng;

    public BreedLocation(){ // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }
    public BreedLocation(String breed, double lat, double lng){
        this.breed = breed;
        this.lat = lat;
        this.lng = lng;
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
}
