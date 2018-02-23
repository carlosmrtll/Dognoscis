package mx.itesm.dognoscis;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private Button button;
    static final int REPORT_CODE = 1;

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
                Toast.makeText(MainActivity.this,data.getStringExtra("breed"),  Toast.LENGTH_LONG).show();
            } 
        }
    }
}
