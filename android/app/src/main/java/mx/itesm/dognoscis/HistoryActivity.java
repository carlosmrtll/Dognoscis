package mx.itesm.dognoscis;

import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static mx.itesm.dognoscis.photoActivity.PROPERTIES_FILE;

public class HistoryActivity extends AppCompatActivity {
    ListView myList;
    Properties properties;
    int startingNum=0;
    int quantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.light_bg));
        getSupportActionBar().hide();

        properties = new Properties();
        try{
            FileInputStream fis = openFileInput(PROPERTIES_FILE);
            properties.loadFromXML(fis);
            fis.close();
        }catch (IOException ioe){
            ioe.printStackTrace();
        }

        if(properties.getProperty("quantity")==null){
            quantity=0;
        }else {
            quantity = Integer.parseInt(properties.getProperty("quantity"));
        }


        myList= findViewById(R.id.listHistory);
        loadhistory();



    }

    public void next(View v){
        if (startingNum+3<=quantity){
            startingNum+=3;
            loadhistory();
        }

    }

    public void prev(View v){
        if (startingNum>0) {
            startingNum -= 3;
            loadhistory();
        }
    }

    public void loadhistory(){

        CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(),properties,startingNum);
        myList.setAdapter(customAdapter);
    }
}
