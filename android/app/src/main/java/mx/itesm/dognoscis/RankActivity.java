package mx.itesm.dognoscis;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RankActivity extends AppCompatActivity {

    private String pname = "Husky";
    private ListView listView;
    private String[] source = {"hola" , "hola", "hola", "hola"};
    //private String[] perros = {"husky", "dalmata", "chihuahua", "sanbernardo"};
    //private String[] source = new String[4];
    //private String[] perros = {"husky", "dalmata", "chihuahua", "sanbernardo"};

    final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);

        listView = (ListView)findViewById(R.id.ListView);

        ref.orderByChild("count").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //Log.d("->","yes:"+snapshot.child(data.getStringExtra("breed")).child("count").getValue());
                int i=3;
                Log.d("-->", "llega");
                for (DataSnapshot child : snapshot.getChildren()) {
                    source[i] = child.getKey().toString();
                    Log.d("-->","key:"+child.getKey());
                    Log.d("-->","count:"+child.child("count").getValue());
                    i--;
                }
                Log.d("-->", "sale");
                // Adapter
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                        RankActivity.this,
                        android.R.layout.simple_list_item_1,
                        source
                );

                listView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String temp = ""+(listView.getItemAtPosition(position));
                if(temp.equals("San Bernardo")){
                    pname = "sanbernardo";
                } else {
                    pname = temp;
                }
                Log.d("-->", "pname:"+pname);
                click(view);
            }
        });
    }

    public void click(View v){
        Intent intent = new Intent(this, RankInfoActivity.class);
        intent.putExtra("perroname", pname);
        Log.d("-->", "pname2:"+pname);

        startActivity(intent);
    }

}
