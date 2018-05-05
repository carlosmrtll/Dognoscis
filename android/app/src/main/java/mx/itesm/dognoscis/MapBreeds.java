package mx.itesm.dognoscis;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import org.json.JSONException;

import java.util.List;
import java.util.ArrayList;

public class MapBreeds extends FragmentActivity implements OnMapReadyCallback {


    private GoogleMap mMap;

    private final String TAG = "MAP_SCREEN";

    private final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("map");

    private ClusterManager<MyItem> mClusterManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_breeds);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.light_bg));
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Intent intent = getIntent();
        String breedFetch = intent.getStringExtra("breeds");
        Log.wtf(TAG, "breedFetch: "+breedFetch);
        if(breedFetch.equals("all")){
            setUpClusterer();

            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                String breed;
                double lat, lng, certainty;
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for(DataSnapshot photo : snapshot.getChildren()){
                        breed = photo.child("breed").getValue().toString();
                        lat = (double)photo.child("lat").getValue();
                        lng = (double)photo.child("lng").getValue();
                        certainty = (double)photo.child("certainty").getValue();
                        Log.wtf(TAG, "breed:" + breed);
                        Log.wtf(TAG, "lat:" + lat);
                        Log.wtf(TAG, "lng:" + lng);
                        Log.wtf(TAG, "certainty:" + certainty);
                        MyItem newItem = new MyItem(lat, lng, breed, certainty+"%");
                        mClusterManager.addItem(newItem);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 10));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            ref.orderByChild("breed").equalTo(breedFetch).addListenerForSingleValueEvent(new ValueEventListener() {
                String breed;
                double lat, lng, certainty;
                List<LatLng> list = new ArrayList<>();
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for(DataSnapshot photo : snapshot.getChildren()){
                        breed = photo.child("breed").getValue().toString();
                        lat = (double)photo.child("lat").getValue();
                        lng = (double)photo.child("lng").getValue();
                        certainty = (double)photo.child("certainty").getValue();
                        Log.wtf(TAG, "breed: " + breed);
                        Log.wtf(TAG, "lat:" + lat);
                        Log.wtf(TAG, "lng:" + lng);
                        Log.wtf(TAG, "certainty:" + certainty);
                        list.add(new LatLng(lat, lng));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 10));
                    }
                    if(!list.isEmpty()){
                        // Create a heat map tile provider, passing it the latlngs of the police stations.
                        HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder()
                                .data(list)
                                .build();
                        // Add a tile overlay to the map, using the heat map tile provider.
                        TileOverlay mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void setUpClusterer() {
        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        mClusterManager = new ClusterManager<MyItem>(this, mMap);

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
    }

    public class MyItem implements ClusterItem {
        private final LatLng mPosition;
        private /*final*/ String mTitle;
        private /*final*/ String mSnippet;

        public MyItem(double lat, double lng) {
            mPosition = new LatLng(lat, lng);
        }

        public MyItem(double lat, double lng, String title, String snippet) {
            mPosition = new LatLng(lat, lng);
            mTitle = title;
            mSnippet = snippet;
        }

        @Override
        public LatLng getPosition() {
            return mPosition;
        }

        @Override
        public String getTitle() {
            return mTitle;
        }

        @Override
        public String getSnippet() {
            return mSnippet;
        }
    }
}
