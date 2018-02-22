package mx.itesm.dognoscis;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void photoActivity(View v){
        Intent intent= new Intent(this,photoActivity.class);
        startActivity(intent);
    }

    public void historyActivity(View v){
        Intent intent= new Intent(this,HistoryActivity.class);
        startActivity(intent);
    }

    public void rankingActivity(View v){
        Intent intent= new Intent(this,HistoryActivity.class);
        startActivity(intent);
    }


}
