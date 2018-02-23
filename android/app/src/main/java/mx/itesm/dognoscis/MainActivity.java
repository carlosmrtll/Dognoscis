package mx.itesm.dognoscis;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class MainActivity extends AppCompatActivity {
    private Button button;

    private Properties properties;
    public static final String PROPERTIES_FILE = "properties.xml";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.button);

        properties = new Properties();



        File file = new File(getFilesDir(), PROPERTIES_FILE);
        try{
            if(!file.exists()){

                saveProperties();
            }else{
                FileInputStream fis = openFileInput(PROPERTIES_FILE);
                properties.loadFromXML(fis);
                fis.close();

                if(properties.getProperty("quantity")==null){
                    properties.put("quantity","0");
                    saveProperties();
                }
            }
        }catch (IOException ioe){
            ioe.printStackTrace();
        }


    }

    public void buttonClick(View v) {
        Intent intent = new Intent(this, RankActivity.class);
        startActivity(intent);
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

    private void saveProperties() throws IOException{

        FileOutputStream fos = openFileOutput(PROPERTIES_FILE, Context.MODE_PRIVATE);
        properties.storeToXML(fos, null);
        fos.close();
        Toast.makeText(this, "FILE SAVED", Toast.LENGTH_SHORT).show();
    }

}
