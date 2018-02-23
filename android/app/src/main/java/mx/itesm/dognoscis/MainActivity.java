package mx.itesm.dognoscis;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private Button button;
    static final int REPORT_CODE = 1;

    final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.button);
    }

    public void buttonClick(View v) {
        Intent intent = new Intent(this, RankActivity.class);
        startActivity(intent);
    }

    public void photoActivity(View v){
        Intent intent= new Intent(this,photoActivity.class);
        startActivityForResult(intent, REPORT_CODE);
    }

    public void historyActivity(View v){
        Intent intent= new Intent(this,HistoryActivity.class);
        startActivity(intent);
    }

    public void rankingActivity(View v){
        Intent intent= new Intent(this,HistoryActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REPORT_CODE && resultCode == Activity.RESULT_OK) {
            if(data.getBooleanExtra("clicked",false)){
                String breed = data.getStringExtra("breed");
                Toast.makeText(MainActivity.this,breed,  Toast.LENGTH_LONG).show();

                ref.orderByChild("name").equalTo(breed).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                //Log.d("->","yes:"+snapshot.child(data.getStringExtra("breed")).child("count").getValue());
                                Log.d("->","snap:"+snapshot.toString());
                                for (DataSnapshot child : snapshot.getChildren()) {
                                    Log.d("->","key:"+child.getKey());
                                    Log.d("->","a "+child.getKey()+" cambias "+child.child("count").getValue());
                                    Log.d("->","count:"+child.child("count").getValue());
                                    ref.child(child.getKey()).child("count").setValue((long)child.child("count").getValue()+1);
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError firebaseError) {
                                System.out.println("The read failed: " + firebaseError.getMessage());
                            }
                        });
            }
        }
    }
}
