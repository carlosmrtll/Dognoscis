package mx.itesm.dognoscis;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RankActivity extends AppCompatActivity {

    private String pname = "Husky";
    private String readablePname = "";
    private ListView listView;
    //private String[] source = {"hola" , "hola", "hola", "hola", "hola" , "hola", "hola", "hola", "hola"};
    private ArrayList<Rank> source = new ArrayList<>();
    //private String[] perros = {"husky", "dalmata", "chihuahua", "sanbernardo"};
    //private String[] source = new String[4];
    //private String[] perros = {"husky", "dalmata", "chihuahua", "sanbernardo"};

    private final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("ranking");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.light_bg));
        getSupportActionBar().hide();
        listView = (ListView)findViewById(R.id.ListView);
        source.add(new Rank("hola", 0));
        source.add(new Rank("hola", 0));
        source.add(new Rank("hola", 0));
        source.add(new Rank("hola", 0));
        source.add(new Rank("hola", 0));
        source.add(new Rank("hola", 0));
        source.add(new Rank("hola", 0));
        source.add(new Rank("hola", 0));
        source.add(new Rank("hola", 0));
        source.add(new Rank("hola", 0));
        source.add(new Rank("hola", 0));
        source.add(new Rank("hola", 0));
        source.add(new Rank("hola", 0));

        ref.orderByChild("count").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //Log.d("->","yes:"+snapshot.child(data.getStringExtra("breed")).child("count").getValue());
                int i = source.size()-1;
                Log.d("-->", "llega");
                for (DataSnapshot child : snapshot.getChildren()) {
                    Long count = (Long)child.child("count").getValue();
                    Log.wtf("-->","key:"+child.getKey());
                    Log.wtf("-->","count: "+count.intValue());
                    source.set(i, new Rank(child.getKey().toString(), count.intValue()));
                    i--;
                }
                Log.d("-->", "sale");
                // Adapter
                /*ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                        RankActivity.this,
                        android.R.layout.simple_list_item_1,
                        source
                );*/

                listView.setAdapter(new RankAdapter(RankActivity.this, source));
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //String temp = ""+(listView.getItemAtPosition(position));
                ViewGroup row = (ViewGroup)listView.getChildAt(1);
                TextView name = view.findViewById(R.id.breedName);
                readablePname = name.getText().toString();
                pname = readablePname.toLowerCase().replaceAll("\\s", "");
                //String temp = row.
                //TextView name = listView;
                //String temp = name.getText().toString();
                Log.d("-->", "pname:"+pname);
                click(view);
            }
        });
    }

    public void click(View v){
        Intent intent = new Intent(this, RankInfoActivity.class);
        intent.putExtra("perroname", pname.toLowerCase());
        intent.putExtra("readablePerroname", readablePname);
        Log.d("-->", "pname2:"+pname);

        startActivity(intent);
    }

    public void openMap(View v){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(RankActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        } else{
            Log.wtf("PERMISOS", "ALREADY AUTHORIZED");
            Intent intent = new Intent(this, MapBreeds.class);
            intent.putExtra("breeds", "all");
            startActivity(intent);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] p, int[] r){
        if(requestCode == 0 && r[0] == PackageManager.PERMISSION_GRANTED){
            Log.wtf("PERMISOS", "SI AUTORIZO");
            Intent intent = new Intent(this, MapBreeds.class);
            intent.putExtra("breeds", "all");
            startActivity(intent);
        } else {
            Log.wtf("PERMISOS", "NO HAY AUTORIZACION");
        }
    }
}

