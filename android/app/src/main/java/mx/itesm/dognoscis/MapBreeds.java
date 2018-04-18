package mx.itesm.dognoscis;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapBreeds extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private final String TAG = "MAP_SCREEN";

    private final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("map");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_breeds);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            String breed;
            double lat,lng;
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for(DataSnapshot photo : snapshot.getChildren()){
                    breed = photo.child("breed").getValue().toString();
                    lat = (double)photo.child("lat").getValue();
                    lng = (double)photo.child("lng").getValue();
                    Log.wtf(TAG, "breed:" + breed);
                    Log.wtf(TAG, "breed:" + lat);
                    Log.wtf(TAG, "breed:" + lng);
                    LatLng photoLatLng = new LatLng(lat, lng);
                    mMap.addMarker(new MarkerOptions().position(photoLatLng).title(breed));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(photoLatLng));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
